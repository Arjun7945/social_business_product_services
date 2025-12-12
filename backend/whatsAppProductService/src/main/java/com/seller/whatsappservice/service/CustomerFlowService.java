package com.seller.whatsappservice.service;

import com.seller.whatsappservice.dto.CartItemDto;
import com.seller.whatsappservice.dto.UserLookupResult;
import com.seller.whatsappservice.dto.WhatsAppMessageDto;
import com.seller.whatsappservice.dto.WhatsAppWebhookDto;
import com.seller.whatsappservice.model.*;
import com.seller.whatsappservice.model.enums.CustomerFlowStage;
import com.seller.whatsappservice.model.enums.UserRole;
import com.seller.whatsappservice.repository.*;
import com.seller.whatsappservice.service.business.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service to handle the complete customer conversation flow via WhatsApp
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerFlowService {

    private final CustomerRepository customerRepository;
    private final FishProductRepository fishProductRepository;
    // Removed CustomerOrderRepository
    // Removed OrderItemRepository
    private final OrderService orderService; // Added dependency
    private final WhatsAppService whatsAppService;
    private final LocationValidationService locationValidationService;
    private final ShoppingCartService shoppingCartService;
    private final ExecutiveFlowService executiveFlowService;
    private final DeliveryFlowService deliveryFlowService;
    private final UserRoleLookupService userRoleLookupService;
    private final AdminFlowService adminFlowService;
    private final DeveloperFlowService developerFlowService;
    private final CustomerMessageService messageService;
    private final DeliveryPersonMessageService deliveryMessageService;
    private final WhatsAppMediaService whatsAppMediaService;

    /**
     * Main entry point for processing incoming WhatsApp messages
     * Routes messages based on sender role: TeamMember (Executive/Delivery) or
     * Customer
     */
    @Transactional
    public void processIncomingMessage(WhatsAppWebhookDto.Value messageValue) {
        if (messageValue.getMessages() == null || messageValue.getMessages().isEmpty()) {
            return;
        }

        WhatsAppWebhookDto.Message message = messageValue.getMessages().get(0);
        String fromWaId = message.getFrom();

        log.info("Processing message from: {}", fromWaId);

        // Step 1: Lookup user role
        UserLookupResult lookupResult = userRoleLookupService.lookupUserByWaPhoneNumber(fromWaId);

        // Step 2: Route based on role
        if (lookupResult != null) {
            // User exists in system - route based on role
            switch (lookupResult.getRole()) {
                case ADMIN:
                    log.info("Routing to Admin Flow for: {}", lookupResult.asTeamMember().getName());
                    adminFlowService.handleAdminMessage(lookupResult.asTeamMember(), message);
                    return;

                case DEVELOPER:
                    TeamMember developer = lookupResult.asTeamMember();
                    log.info("Routing to Developer Flow for: {}", developer.getName());
                    developerFlowService.handleDeveloperMessage(developer, message);
                    return;

                case EXECUTIVE:
                    // Route to Executive Flow
                    TeamMember executive = lookupResult.asTeamMember();
                    log.info("Routing to Executive Flow for: {}", executive.getName());
                    executiveFlowService.handleExecutiveMessage(executive, message);
                    return;

                case DELIVERY_PERSON:
                    // Route to Delivery Flow
                    TeamMember deliveryPerson = lookupResult.asTeamMember();
                    log.info("Routing to Delivery Flow for: {}", deliveryPerson.getName());

                    // Handle delivery person button replies (order confirmation)
                    if (message.getType().equals("interactive") &&
                            message.getInteractive().getType().equals("button_reply")) {
                        WhatsAppWebhookDto.ButtonReply buttonReply = message.getInteractive().getButtonReply();
                        if (buttonReply.getId().startsWith("DELIVERY_TAKE_")) {
                            Long orderId = Long.parseLong(buttonReply.getId().replace("DELIVERY_TAKE_", ""));
                            deliveryFlowService.handleDeliveryConfirmation(fromWaId, orderId);
                            return;
                        }
                    }
                    // For other messages from delivery persons, just acknowledge
                    whatsAppService.sendSimpleText(fromWaId,
                            deliveryMessageService.getDeliveryPersonAcknowledgment());
                    return;

                case CUSTOMER:
                    // Route to Customer Flow
                    Customer customer = lookupResult.asCustomer();
                    log.info("Routing to Customer Flow for: {} (Stage: {})",
                            customer.getName() != null ? customer.getName() : "Unknown",
                            customer.getCurrentFlowStage());
                    handleCustomerMessage(customer, message);
                    return;

                default:
                    log.warn("Unknown role for user: {}", fromWaId);
                    return;
            }
        } else {
            // Unknown number - create new customer and start registration flow
            log.info("Unknown number {} - creating new customer", fromWaId);
            Customer newCustomer = Customer.builder()
                    .waPhoneNumber(fromWaId)
                    .currentFlowStage(CustomerFlowStage.NEW)
                    .role(UserRole.CUSTOMER)
                    .build();
            newCustomer = customerRepository.save(newCustomer);

            handleCustomerMessage(newCustomer, message);
        }
    }

    /**
     * Handle messages from customers (both new and existing)
     */
    private void handleCustomerMessage(Customer customer, WhatsAppWebhookDto.Message message) {
        // Allow customer to restart flow from any stage by sending "start" or "hi"
        if (message.getType().equals("text") && message.getText() != null) {
            String text = message.getText().getBody().trim();
            if (text.equalsIgnoreCase("start")) {
                customer.setCurrentFlowStage(CustomerFlowStage.REGISTERED);
                showProductCatalog(customer);
                customerRepository.save(customer);
                return;
            } else if (text.equalsIgnoreCase("hi") || text.equalsIgnoreCase("hello")) {
                // Reset to registered if already registered, otherwise treat as new customer
                if (customer.getCurrentFlowStage() != CustomerFlowStage.NEW &&
                        customer.getCurrentFlowStage() != CustomerFlowStage.AWAITING_NAME) {
                    customer.setCurrentFlowStage(CustomerFlowStage.REGISTERED);
                    whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                            messageService.getRegisteredCustomerGreeting(customer.getName()));
                    customerRepository.save(customer);
                    return;
                }
            }
        }

        // Handle different message types for customers
        if (message.getType().equals("text") && message.getText() != null) {
            handleTextMessage(customer, message.getText().getBody());
        } else if (message.getType().equals("location") && message.getLocation() != null) {
            handleLocationMessage(customer, message.getLocation());
        } else if (message.getType().equals("interactive")) {
            if (message.getInteractive().getType().equals("button_reply")) {
                handleButtonReply(customer, message.getInteractive().getButtonReply());
            } else if (message.getInteractive().getType().equals("list_reply")) {
                handleListReply(customer, message.getInteractive().getListReply());
            }
        }

        customerRepository.save(customer);
    }

    /**
     * Handle text messages based on customer flow stage
     */
    private void handleTextMessage(Customer customer, String text) {
        log.info("Customer {} in stage {} sent: {}", customer.getWaPhoneNumber(), customer.getCurrentFlowStage(), text);

        switch (customer.getCurrentFlowStage()) {
            case NEW:
                handleNewCustomer(customer, text);
                break;
            case AWAITING_NAME:
                handleAwaitingName(customer, text);
                break;
            case AWAITING_PHONE:
                handleAwaitingPhone(customer, text);
                break;
            case AWAITING_QUANTITY:
                handleAwaitingQuantity(customer, text);
                break;
            case REGISTERED:
            case BROWSING:
            case ADDING_TO_CART:
                handleRegisteredCustomer(customer, text);
                break;
            default:
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                        messageService.getUnrecognizedCommand());
        }
    }

    /**
     * 1. Handle new customer - greet and ask for name
     */
    private void handleNewCustomer(Customer customer, String text) {
        if (text.trim().equalsIgnoreCase("hi") || text.trim().equalsIgnoreCase("hello")) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getWelcomeMessageNewCustomer());
            customer.setCurrentFlowStage(CustomerFlowStage.AWAITING_NAME);
        } else {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getWelcomeMessageOtherText());
        }
    }

    /**
     * 2. Collect customer name
     */
    private void handleAwaitingName(Customer customer, String text) {
        customer.setName(text.trim());
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getNameConfirmation(text.trim()));
        customer.setCurrentFlowStage(CustomerFlowStage.AWAITING_PHONE);
    }

    /**
     * 3. Collect phone number and request location
     */
    private void handleAwaitingPhone(Customer customer, String text) {
        customer.setPhoneNumber(text.trim());
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                messageService.getPhoneConfirmationAndLocationRequest());
        customer.setCurrentFlowStage(CustomerFlowStage.AWAITING_LOCATION);
    }

    /**
     * 4. Handle location message and validate delivery area
     */
    private void handleLocationMessage(Customer customer, WhatsAppWebhookDto.Location location) {
        double customerLat = location.getLatitude();
        double customerLon = location.getLongitude();

        log.info("Received location from customer {}: lat={}, lon={}", customer.getWaPhoneNumber(), customerLat,
                customerLon);

        // Save location
        customer.setLocationLat(customerLat);
        customer.setLocationLon(customerLon);

        // Calculate distance
        double distance = locationValidationService.getDistanceFromBusiness(customerLat, customerLon);
        customer.setDistanceFromBusinessKm(distance);

        // Validate delivery area
        if (locationValidationService.isWithinDeliveryRadius(customerLat, customerLon)) {
            customer.setCurrentFlowStage(CustomerFlowStage.REGISTERED);
            customer.setRegisteredAt(LocalDateTime.now());

            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getLocationAccepted(customer.getName(), distance));
        } else {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getLocationRejected(customer.getName(), distance));
            customer.setCurrentFlowStage(CustomerFlowStage.NEW);
        }
    }

    /**
     * 5. Handle registered customer commands
     */
    private void handleRegisteredCustomer(Customer customer, String text) {
        if (text.trim().equalsIgnoreCase("start")) {
            showProductCatalog(customer);
        } else {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getPromptToBrowse(customer.getName()));
        }
    }

    /**
     * 6. Show product catalog
     */
    private void showProductCatalog(Customer customer) {
        log.info("Fetching available fish products for customer: {}", customer.getWaPhoneNumber());
        // Use eager loading to fetch images
        List<FishProduct> products = fishProductRepository.findByIsAvailableTrueWithImages();
        log.info("Found {} available fish products", products.size());

        if (products.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getNoProductsAvailable(customer.getName()));
            return;
        }

        // Get dummy image media ID if needed
        String dummyMediaId = whatsAppMediaService.getDummyImageMediaId();

        // Separate products for carousel (valid image or dummy available) vs list (no
        // image and no dummy)
        List<FishProduct> carouselProducts = new java.util.ArrayList<>();
        List<FishProduct> listProducts = new java.util.ArrayList<>();

        for (FishProduct product : products) {
            boolean hasImage = !product.getImages().isEmpty();
            if (hasImage || dummyMediaId != null) {
                carouselProducts.add(product);
            } else {
                listProducts.add(product);
            }
        }

        // Display products in carousel (up to 10)
        if (!carouselProducts.isEmpty()) {
            // If we have more than 10, the rest go to list
            List<FishProduct> carouselSubset = carouselProducts.stream().limit(10).collect(Collectors.toList());
            List<FishProduct> remaining = carouselProducts.stream().skip(10).collect(Collectors.toList());

            listProducts.addAll(remaining);

            sendProductCarousel(customer, carouselSubset, dummyMediaId);
        }

        // Display remaining products in list
        if (!listProducts.isEmpty()) {
            sendProductList(customer, listProducts);
        }

        customer.setCurrentFlowStage(CustomerFlowStage.BROWSING);
    }

    @org.springframework.beans.factory.annotation.Value("${app.server.url}")
    private String appServerUrl;

    /**
     * Send product carousel for products with images
     */
    private void sendProductCarousel(Customer customer, List<FishProduct> products, String dummyMediaId) {
        log.info("Preparing carousel with {} products", products.size());
        List<FishProduct> limitedProducts = products.stream()
                .limit(10)
                .collect(Collectors.toList());

        List<WhatsAppMessageDto.CarouselCardDto> cards = java.util.stream.IntStream.range(0, limitedProducts.size())
                .mapToObj(i -> {
                    FishProduct product = limitedProducts.get(i);
                    // Construct image URL
                    String imageUrl;
                    if (!product.getOrderedImages().isEmpty()) {
                        imageUrl = appServerUrl + "/api/public/images/products/" + product.getId() + "?t="
                                + System.currentTimeMillis();
                    } else {
                        // Fallback to placeholder image URL if available or use a default one
                        // Since we don't have a public URL for the dummy image, we'll try to use the
                        // Media ID logic
                        // BUT mixed types might fail. For now, let's stick to URL.
                        // Ideally, we should have a /api/public/images/placeholder endpoint or similar.
                        // But for now, let's use the wikimedia one as fallback if no backend image
                        // exists,
                        // although the filtering logic ensures 'carouselProducts' have images OR
                        // dummyMediaId.
                        // Since we are shifting to URLs, we should probably serve the dummy image via
                        // URL too.
                        // For this iteration, I'll use the hardcoded WA logo as the "Dummy" URL
                        // fallback if no product image.
                        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/WhatsApp.svg/1200px-WhatsApp.svg.png";
                    }

                    return WhatsAppMessageDto.CarouselCardDto.builder()
                            .cardIndex(i)
                            .type("button")
                            .header(WhatsAppMessageDto.HeaderDto.builder()
                                    .type("image")
                                    .image(WhatsAppMessageDto.ImageDto.builder()
                                            .link(imageUrl)
                                            .build())
                                    .build())
                            .body(WhatsAppMessageDto.BodyDto.builder()
                                    .text(String.format("%s\n₹%.2f/kg\n%s",
                                            product.getName(),
                                            product.getPricePerKg(),
                                            product.getDescription() != null ? product.getDescription() : ""))
                                    .build())
                            .action(WhatsAppMessageDto.ActionDto.builder()
                                    .buttons(List.of(
                                            WhatsAppMessageDto.ButtonDto.builder()
                                                    .type("quick_reply")
                                                    .quickReply(WhatsAppMessageDto.ReplyDto.builder()
                                                            .id("SELECT_" + product.getId())
                                                            .title("Add to Cart")
                                                            .build())
                                                    .build()))
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());

        whatsAppService.sendCarouselMessage(customer.getWaPhoneNumber(),
                messageService.getProductCatalogHeader(customer.getName()), cards);
    }

    /**
     * Send product list for products without images
     */
    private void sendProductList(Customer customer, List<FishProduct> products) {
        List<WhatsAppMessageDto.RowDto> rows = products.stream()
                .map(p -> WhatsAppMessageDto.RowDto.builder()
                        .id("FISH_" + p.getId())
                        .title(p.getName())
                        .description(String.format("₹%.2f/kg", p.getPricePerKg()))
                        .build())
                .collect(Collectors.toList());

        whatsAppService.sendInteractiveList(customer.getWaPhoneNumber(), "Products without images:", rows);
    }

    /**
     * 7. Handle product selection from list and cart editing operations
     */
    private void handleListReply(Customer customer, WhatsAppWebhookDto.ListReply listReply) {
        String selectedId = listReply.getId(); // e.g., "FISH_123", "REMOVE_ITEM_123", "EDIT_QTY_123"

        // Handle product catalog selection
        if (selectedId.startsWith("FISH_")) {
            Long fishProductId = Long.parseLong(selectedId.replace("FISH_", ""));

            Optional<FishProduct> fishOpt = fishProductRepository.findById(fishProductId);
            if (fishOpt.isEmpty()) {
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                        messageService.getProductNoLongerAvailable());
                return;
            }

            FishProduct fish = fishOpt.get();

            // Store selected product temporarily
            customer.setTempSelectedProductId(fishProductId);
            customer.setCurrentFlowStage(CustomerFlowStage.AWAITING_QUANTITY);

            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService
                    .getProductSelectedQuantityRequest(customer.getName(), fish.getName(), fish.getPricePerKg()));
        }
        // Handle remove item from cart
        else if (selectedId.startsWith("REMOVE_ITEM_")) {
            handleRemoveItem(customer, selectedId);
        }
        // Handle remove all items from cart
        else if (selectedId.equals("REMOVE_ALL_ITEMS")) {
            handleRemoveAllItems(customer);
        }
        // Handle edit quantity selection
        else if (selectedId.startsWith("EDIT_QTY_")) {
            handleEditQuantitySelection(customer, selectedId);
        }
    }

    /**
     * Handle product selection from carousel
     */
    private void handleCarouselProductSelection(Customer customer, String buttonId) {
        Long fishProductId = Long.parseLong(buttonId.replace("SELECT_", ""));

        Optional<FishProduct> fishOpt = fishProductRepository.findById(fishProductId);
        if (fishOpt.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getProductNoLongerAvailable());
            return;
        }

        FishProduct fish = fishOpt.get();

        // Store selected product temporarily
        customer.setTempSelectedProductId(fishProductId);
        customer.setCurrentFlowStage(CustomerFlowStage.AWAITING_QUANTITY);

        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService
                .getProductSelectedQuantityRequest(customer.getName(), fish.getName(), fish.getPricePerKg()));
    }

    /**
     * 8. Handle quantity input and add to cart or update existing item
     */
    private void handleAwaitingQuantity(Customer customer, String text) {
        try {
            Double quantity = Double.parseDouble(text.trim());

            if (quantity <= 0) {
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                        messageService.getInvalidQuantityZeroOrNegative());
                return;
            }

            Long fishProductId = customer.getTempSelectedProductId();

            // Check if this is an edit or new add
            List<CartItemDto> items = shoppingCartService.getCartItems(customer.getId());
            boolean isEdit = items.stream().anyMatch(i -> i.getFishProductId().equals(fishProductId));

            if (isEdit) {
                // Update existing item
                shoppingCartService.updateQuantity(customer.getId(), fishProductId, quantity);
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getQuantityUpdated());
                showCartSummary(customer);
            } else {
                // Add new item
                shoppingCartService.addToCart(customer.getId(), fishProductId, quantity);
                sendCartOptions(customer);
                customer.setCurrentFlowStage(CustomerFlowStage.ADDING_TO_CART);
            }

        } catch (NumberFormatException e) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getInvalidQuantityFormat());
        }
    }

    /**
     * 9. Send cart action options
     */
    private void sendCartOptions(Customer customer) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("CONTINUE_SHOPPING")
                                .title(messageService.getButtonAddMoreFish())
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("CHECKOUT")
                                .title(messageService.getButtonCheckout())
                                .build())
                        .build());

        whatsAppService.sendCartActionButtons(customer.getWaPhoneNumber(),
                messageService.getItemAddedToCart(customer.getName()), buttons);
    }

    /**
     * 10. Handle button replies (cart actions, order confirmation, delivery
     * confirmation)
     */
    private void handleButtonReply(Customer customer, WhatsAppWebhookDto.ButtonReply buttonReply) {
        String buttonId = buttonReply.getId();

        log.info("Button reply from {}: {}", customer.getWaPhoneNumber(), buttonId);

        if ("CONTINUE_SHOPPING".equals(buttonId)) {
            showProductCatalog(customer);
        } else if ("CHECKOUT".equals(buttonId)) {
            showCartSummary(customer);
        } else if ("CONFIRM_ORDER".equals(buttonId)) {
            placeOrder(customer);
        } else if ("CANCEL_ORDER".equals(buttonId)) {
            handleCancelOrder(customer);
        } else if ("EDIT_ORDER".equals(buttonId)) {
            showEditOrderOptions(customer);
        } else if ("EDIT_PRODUCT".equals(buttonId)) {
            showProductEditOptions(customer);
        } else if ("EDIT_QUANTITY".equals(buttonId)) {
            showQuantityEditOptions(customer);
        } else if ("BACK_TO_CHECKOUT".equals(buttonId)) {
            showCartSummary(customer);
        } else if ("REMOVE_ALL_ITEMS".equals(buttonId)) {
            handleRemoveAllItems(customer);
        } else if (buttonId.startsWith("REMOVE_ITEM_")) {
            handleRemoveItem(customer, buttonId);
        } else if (buttonId.startsWith("EDIT_QTY_")) {
            handleEditQuantitySelection(customer, buttonId);
        } else if (buttonId.startsWith("SELECT_")) {
            handleCarouselProductSelection(customer, buttonId);
        }
    }

    /**
     * 11. Show cart summary before checkout
     */
    private void showCartSummary(Customer customer) {
        List<CartItemDto> items = shoppingCartService.getCartItems(customer.getId());
        Double total = shoppingCartService.calculateCartTotal(customer.getId());

        if (items.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCart());
            customer.setCurrentFlowStage(CustomerFlowStage.REGISTERED);
            return;
        }

        StringBuilder summary = new StringBuilder(messageService.getCartSummaryHeader(customer.getName()));
        for (CartItemDto item : items) {
            summary.append(String.format("• %s - %.2f kg × ₹%.2f = ₹%.2f\n", item.getFishName(), item.getQuantityKg(),
                    item.getPricePerKg(), item.getSubtotal()));
        }
        summary.append(messageService.getCartSummaryFooter(total));

        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("CONFIRM_ORDER")
                                .title(messageService.getButtonConfirmOrder())
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("EDIT_ORDER")
                                .title(messageService.getButtonEditOrder())
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("CANCEL_ORDER")
                                .title(messageService.getButtonCancelOrder())
                                .build())
                        .build());

        whatsAppService.sendCartActionButtons(customer.getWaPhoneNumber(), summary.toString(), buttons);
        customer.setCurrentFlowStage(CustomerFlowStage.CHECKOUT);
    }

    /**
     * 12. Place order and send to delivery group
     */
    @Transactional
    private void placeOrder(Customer customer) {
        try {
            // Delegate logic to OrderService
            CustomerOrder order = orderService.createOrder(customer, "COD");

            // Send confirmation to customer
            whatsAppService.sendOrderConfirmation(customer.getWaPhoneNumber(), order.getId(), order.getTotalAmount());

            // Reset flow stage
            customer.setCurrentFlowStage(CustomerFlowStage.REGISTERED);

        } catch (IllegalStateException e) {
            // Empty cart or validation error
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCartDuringOrder());
        } catch (Exception e) {
            log.error("Order placement failed for customer {}", customer.getId(), e);
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getOrderPlacementFailure());
        }
    }

    /**
     * Handle Cancel Order - Clear cart and reset to registered state
     */
    private void handleCancelOrder(Customer customer) {
        // Clear shopping cart
        shoppingCartService.clearCart(customer.getId());

        // Reset flow stage
        customer.setCurrentFlowStage(CustomerFlowStage.REGISTERED);

        // Send confirmation
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                messageService.getOrderCancelled(customer.getName()));
    }

    /**
     * Show Edit Order options menu
     */
    private void showEditOrderOptions(Customer customer) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("EDIT_PRODUCT")
                                .title(messageService.getButtonEditProduct())
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("EDIT_QUANTITY")
                                .title(messageService.getButtonEditQuantity())
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("BACK_TO_CHECKOUT")
                                .title(messageService.getButtonBackToCheckout())
                                .build())
                        .build());

        whatsAppService.sendCartActionButtons(customer.getWaPhoneNumber(),
                messageService.getEditOrderMenu(customer.getName()), buttons);

        customer.setCurrentFlowStage(CustomerFlowStage.EDITING_ORDER);
    }

    /**
     * Show product edit options - allow removing products from cart
     * Shows ONLY the products that are currently in the customer's cart
     */
    private void showProductEditOptions(Customer customer) {
        List<CartItemDto> items = shoppingCartService.getCartItems(customer.getId());

        if (items.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCart());
            customer.setCurrentFlowStage(CustomerFlowStage.REGISTERED);
            return;
        }

        if (items.size() == 1) {
            // Single item - use buttons
            CartItemDto item = items.get(0);
            List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                    WhatsAppMessageDto.ButtonDto.builder()
                            .type("reply")
                            .reply(WhatsAppMessageDto.ReplyDto.builder()
                                    .id("REMOVE_ITEM_" + item.getFishProductId())
                                    .title(messageService.getButtonRemoveProduct(item.getFishName()))
                                    .build())
                            .build(),
                    WhatsAppMessageDto.ButtonDto.builder()
                            .type("reply")
                            .reply(WhatsAppMessageDto.ReplyDto.builder()
                                    .id("BACK_TO_CHECKOUT")
                                    .title(messageService.getButtonBackToCheckout())
                                    .build())
                            .build());

            whatsAppService.sendCartActionButtons(customer.getWaPhoneNumber(),
                    messageService.getRemoveProductHeaderSingle()
                            + String.format("• %s - %.2f kg\n\n", item.getFishName(), item.getQuantityKg())
                            + messageService.getRemoveProductPrompt(),
                    buttons);
        } else {
            // Multiple items - use interactive list to show ALL cart items
            sendProductRemovalList(customer, items);
        }

        customer.setCurrentFlowStage(CustomerFlowStage.EDITING_PRODUCT);
    }

    /**
     * Send interactive list for product removal
     * Shows ALL products currently in the cart (not limited to 2-3 items)
     */
    private void sendProductRemovalList(Customer customer, List<CartItemDto> items) {
        StringBuilder message = new StringBuilder("🗑️ *Remove Products*\n\n*Current cart:*\n");
        for (CartItemDto item : items) {
            message.append(String.format("• %s - %.2f kg × ₹%.2f = ₹%.2f\n", item.getFishName(), item.getQuantityKg(),
                    item.getPricePerKg(), item.getSubtotal()));
        }
        message.append("\nSelect a product to remove:");

        // Create rows for ALL cart items (not limited by button count)
        List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();

        // Add "Remove All" option as first row
        rows.add(WhatsAppMessageDto.RowDto.builder()
                .id("REMOVE_ALL_ITEMS")
                .title(messageService.getButtonRemoveAll())
                .description("Clear entire cart")
                .build());

        // Add individual cart items
        for (CartItemDto item : items) {
            rows.add(WhatsAppMessageDto.RowDto.builder()
                    .id("REMOVE_ITEM_" + item.getFishProductId())
                    .title(item.getFishName())
                    .description(String.format("%.2f kg - ₹%.2f", item.getQuantityKg(), item.getSubtotal()))
                    .build());
        }

        // Send interactive list with all cart items
        whatsAppService.sendInteractiveList(customer.getWaPhoneNumber(), message.toString(), rows);
    }

    /**
     * Show quantity edit options - allow editing quantities
     * Shows ONLY the products that are currently in the customer's cart
     */
    private void showQuantityEditOptions(Customer customer) {
        List<CartItemDto> items = shoppingCartService.getCartItems(customer.getId());

        if (items.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCart());
            customer.setCurrentFlowStage(CustomerFlowStage.REGISTERED);
            return;
        }

        if (items.size() == 1) {
            // Single item - go directly to quantity input
            CartItemDto item = items.get(0);
            customer.setTempSelectedProductId(item.getFishProductId());

            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getEditQuantityHeader(item.getFishName(), item.getQuantityKg()));

            customer.setCurrentFlowStage(CustomerFlowStage.AWAITING_QUANTITY);
        } else {
            // Multiple items - use interactive list to show ALL cart items
            StringBuilder message = new StringBuilder("✏️ *Edit Quantities*\n\n*Current cart:*\n");
            for (CartItemDto item : items) {
                message.append(String.format("• %s - %.2f kg\n", item.getFishName(), item.getQuantityKg()));
            }
            message.append("\nSelect a product to edit quantity:");

            // Create rows for ALL cart items
            List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();
            for (CartItemDto item : items) {
                rows.add(WhatsAppMessageDto.RowDto.builder()
                        .id("EDIT_QTY_" + item.getFishProductId())
                        .title(item.getFishName())
                        .description(String.format("Current: %.2f kg", item.getQuantityKg()))
                        .build());
            }

            // Send interactive list with all cart items
            whatsAppService.sendInteractiveList(customer.getWaPhoneNumber(), message.toString(), rows);

            customer.setCurrentFlowStage(CustomerFlowStage.EDITING_QUANTITY);
        }
    }

    /**
     * Handle removing all items from cart
     */
    private void handleRemoveAllItems(Customer customer) {
        // Clear entire cart
        shoppingCartService.clearCart(customer.getId());

        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                messageService.getAllProductsRemoved(customer.getName()));
        showProductCatalog(customer);
    }

    /**
     * Handle removing an item from cart
     */
    private void handleRemoveItem(Customer customer, String buttonId) {
        Long fishProductId = Long.parseLong(buttonId.replace("REMOVE_ITEM_", ""));

        // Remove item from cart
        shoppingCartService.removeFromCart(customer.getId(), fishProductId);

        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getProductRemovedSuccessfully());

        // Check if cart is now empty
        List<CartItemDto> remainingItems = shoppingCartService.getCartItems(customer.getId());

        if (remainingItems.isEmpty()) {
            // Cart is empty - show product catalog directly
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getCartEmptyAfterRemoval());
            showProductCatalog(customer);
        } else {
            // Show updated cart summary
            showCartSummary(customer);
        }
    }

    /**
     * Handle selecting a product to edit quantity
     */
    private void handleEditQuantitySelection(Customer customer, String buttonId) {
        Long fishProductId = Long.parseLong(buttonId.replace("EDIT_QTY_", ""));

        // Store the product ID for quantity update
        customer.setTempSelectedProductId(fishProductId);

        // Get current quantity
        List<CartItemDto> items = shoppingCartService.getCartItems(customer.getId());
        CartItemDto item = items.stream().filter(i -> i.getFishProductId().equals(fishProductId)).findFirst()
                .orElse(null);

        if (item != null) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getEditQuantityHeader(item.getFishName(), item.getQuantityKg()));

            customer.setCurrentFlowStage(CustomerFlowStage.AWAITING_QUANTITY);
        }
    }

}
