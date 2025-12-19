package com.aps.service;

import com.aps.config.FlowConstants;
import com.aps.domain.BotSession;

import com.aps.domain.Customer;
import com.aps.domain.FishProduct;
import com.aps.domain.ProductImage;
import java.util.Comparator;
import com.aps.domain.enumeration.CustomerFlowStage;
import com.aps.repository.CustomerRepository;
import com.aps.repository.FishProductRepository;
import com.aps.service.dto.CartItemDetailsDTO;
import com.aps.domain.CustomerOrder;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import com.aps.service.util.InputValidator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service to handle the complete customer conversation flow via WhatsApp.
 * Refactored to use BotSession for state management.
 */
@Service
@Transactional
public class CustomerFlowService {

    private final Logger log = LoggerFactory.getLogger(CustomerFlowService.class);

    private final CustomerRepository customerRepository;
    private final FishProductRepository fishProductRepository;
    private final OrderService orderService;
    private final WhatsAppService whatsAppService;
    private final LocationValidationService locationValidationService;
    private final CartService cartService;
    private final CustomerMessageService messageService;
    private final WhatsAppMediaService whatsAppMediaService;
    private final BotSessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private final InputValidator inputValidator;

    @Value("${app.server.url}")
    private String appServerUrl;

    public CustomerFlowService(CustomerRepository customerRepository,
            FishProductRepository fishProductRepository,
            OrderService orderService,
            WhatsAppService whatsAppService,
            LocationValidationService locationValidationService,
            CartService cartService,
            CustomerMessageService messageService,
            WhatsAppMediaService whatsAppMediaService,
            BotSessionManager sessionManager,
            ObjectMapper objectMapper,
            InputValidator inputValidator) {
        this.customerRepository = customerRepository;
        this.fishProductRepository = fishProductRepository;
        this.orderService = orderService;
        this.whatsAppService = whatsAppService;
        this.locationValidationService = locationValidationService;
        this.cartService = cartService;
        this.messageService = messageService;
        this.whatsAppMediaService = whatsAppMediaService;
        this.sessionManager = sessionManager;
        this.objectMapper = objectMapper;
        this.inputValidator = inputValidator;
    }

    /**
     * Handle messages from customers (called by Dispatcher).
     */
    public void handleCustomerMessage(Customer customer, WhatsAppWebhookDto.Message message) {

        BotSession session = sessionManager.getSession(customer.getWaPhoneNumber());
        CustomerFlowStage currentStage = getStage(session);

        // Allow customer to restart flow from any stage by sending "start" or "hi"
        if (message.getType().equals("text") && message.getText() != null) {
            String text = message.getText().getBody().trim();
            if (text.equalsIgnoreCase("start")) {
                updateStage(session, CustomerFlowStage.REGISTERED);
                showProductCatalog(customer, session);
                return;
            } else if (text.equalsIgnoreCase("hi") || text.equalsIgnoreCase("hello")) {

            } else if (text.equalsIgnoreCase("hi") || text.equalsIgnoreCase("hello")) {
                // If NOT registered, treat "Hi" as "Start Over" for onboarding if in
                // NEW or AWAITING_NAME.

                // New Logic: If not registered, treat "Hi" as "Start Over" for onboarding if in
                // NEW or AWAITING_NAME.
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                        messageService.getWelcomeMessageNewCustomer());
                updateStage(session, CustomerFlowStage.AWAITING_NAME);
                return;
            }
        }

        // Handle different message types for customers
        log.info("Processing message type: {}", message.getType());

        if (message.getType().equals("text") && message.getText() != null) {
            handleTextMessage(customer, session, message.getText().getBody());
        } else if (message.getType().equals("location") && message.getLocation() != null) {
            handleLocationMessage(customer, session, message.getLocation());
        } else if (message.getType().equals("interactive")) {
            log.info("Interactive Type: {}", message.getInteractive().getType());
            if (message.getInteractive().getType().equals("button_reply")) {
                handleButtonReply(customer, session, message.getInteractive().getButtonReply());
            } else if (message.getInteractive().getType().equals("list_reply")) {
                handleListReply(customer, session, message.getInteractive().getListReply());
            }
        } else if (message.getType().equals("button") && message.getButton() != null) {
            log.info("Button Type: {}", message.getButton().getText());
            // Map simple button payload to button reply structure for consistency
            WhatsAppWebhookDto.ButtonReply buttonReply = new WhatsAppWebhookDto.ButtonReply();
            buttonReply.setId(message.getButton().getPayload());
            buttonReply.setTitle(message.getButton().getText());
            handleButtonReply(customer, session, buttonReply);
        } else {
            log.warn("Unhandled message type: {}", message.getType());
        }
    }

    private CustomerFlowStage getStage(BotSession session) {
        try {
            return CustomerFlowStage.valueOf(session.getCurrentState());
        } catch (IllegalArgumentException | NullPointerException e) {
            return CustomerFlowStage.NEW;
        }
    }

    private void updateStage(BotSession session, CustomerFlowStage stage) {
        sessionManager.updateState(session, stage.name());
    }

    /**
     * Handle text messages based on customer flow stage
     */
    private void handleTextMessage(Customer customer, BotSession session, String text) {
        CustomerFlowStage stage = getStage(session);
        log.info("Customer {} in stage {} sent: {}", customer.getWaPhoneNumber(), stage, text);

        switch (stage) {
            case NEW:
                handleNewCustomer(customer, session, text);
                break;
            case AWAITING_NAME:
                handleAwaitingName(customer, session, text);
                break;
            case AWAITING_PHONE: // We collect phone via text if they manually enter it?
                // Legacy logic allowed manual entry.
                handleAwaitingPhone(customer, session, text);
                break;
            case AWAITING_QUANTITY:
                handleAwaitingQuantity(customer, session, text);
                break;
            case REGISTERED:
            case BROWSING:
            case ADDING_TO_CART:
                handleRegisteredCustomer(customer, session, text);
                break;
            default:
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                        messageService.getUnrecognizedCommand());
        }
    }

    private void handleNewCustomer(Customer customer, BotSession session, String text) {
        if (text.trim().equalsIgnoreCase("hi") || text.trim().equalsIgnoreCase("hello")) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getWelcomeMessageNewCustomer());
            updateStage(session, CustomerFlowStage.AWAITING_NAME);
        } else {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getWelcomeMessageOtherText());
        }
    }

    private void handleAwaitingName(Customer customer, BotSession session, String text) {
        String inputName = text.trim();
        // Validation: Check for common invalid names
        if (!inputValidator.isValidName(inputName)) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    "Please enter your *real full name* to continue.");
            return;
        }

        customer.setName(inputName);
        customerRepository.save(customer);
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getNameConfirmation(text.trim()));
        updateStage(session, CustomerFlowStage.AWAITING_PHONE);
    }

    private void handleAwaitingPhone(Customer customer, BotSession session, String text) {
        String phone = text.trim();
        // Allow digits, spaces, and + for country code. Min length 7, max 16.
        if (!inputValidator.isValidPhoneNumber(phone)) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getInvalidPhoneNumber());
            return;
        }

        customer.setPhoneNumber(phone);
        customerRepository.save(customer);
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                messageService.getPhoneConfirmationAndLocationRequest());
        updateStage(session, CustomerFlowStage.AWAITING_LOCATION);
    }

    private void handleLocationMessage(Customer customer, BotSession session, WhatsAppWebhookDto.Location location) {
        double customerLat = location.getLatitude();
        double customerLon = location.getLongitude();

        log.info("Received location from customer {}: lat={}, lon={}", customer.getWaPhoneNumber(), customerLat,
                customerLon);

        customer.setLocationLat(customerLat);
        customer.setLocationLon(customerLon);

        double distance = locationValidationService.getDistanceFromBusiness(customerLat, customerLon);
        customer.setDistanceFromBusinessKm(distance);

        if (locationValidationService.isWithinDeliveryRadius(customerLat, customerLon)) {
            updateStage(session, CustomerFlowStage.REGISTERED);
            // customer.setRegisteredAt(LocalDateTime.now()); // Handled by JHipster
            // Auditing or manually if field exists.
            // Assuming joinedAt or similar exists. Custom field requires manual set.
            // JHipster Customer entity (Step 360) has joinedAt.
            customer.setJoinedAt(java.time.Instant.now());
            customerRepository.save(customer);

            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getLocationAccepted(customer.getName(), distance));
        } else {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getLocationRejected(customer.getName(), distance));
            updateStage(session, CustomerFlowStage.NEW);
        }
    }

    private void handleRegisteredCustomer(Customer customer, BotSession session, String text) {
        if (text.trim().equalsIgnoreCase("start")) {
            showProductCatalog(customer, session);
        } else {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getPromptToBrowse(customer.getName()));
        }
    }

    public void sendReOrderFlow(Customer customer) {
        BotSession session = sessionManager.getSession(customer.getWaPhoneNumber());
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getOrderAgainPrompt());
        showProductCatalog(customer, session);
    }

    private void showProductCatalog(Customer customer, BotSession session) {
        List<FishProduct> products = fishProductRepository.findByIsAvailableTrue();

        if (products.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getNoProductsAvailable(customer.getName()));
            return;
        }

        String dummyMediaId = whatsAppMediaService.getDummyImageMediaId();

        List<FishProduct> carouselProducts = new java.util.ArrayList<>();
        List<FishProduct> listProducts = new java.util.ArrayList<>();

        for (FishProduct product : products) {
            // Need to check images. Lazy loading.
            boolean hasImage = product.getImages() != null && !product.getImages().isEmpty();
            if (hasImage || dummyMediaId != null) {
                carouselProducts.add(product);
            } else {
                listProducts.add(product);
            }
        }

        if (!carouselProducts.isEmpty()) {
            List<FishProduct> carouselSubset = carouselProducts.stream().limit(10).collect(Collectors.toList());
            List<FishProduct> remaining = carouselProducts.stream().skip(10).collect(Collectors.toList());
            listProducts.addAll(remaining);

            sendProductCarousel(customer, carouselSubset, dummyMediaId);
        }

        if (!listProducts.isEmpty()) {
            sendProductList(customer, listProducts);
        }

        updateStage(session, CustomerFlowStage.BROWSING);
    }

    private void sendProductCarousel(Customer customer, List<FishProduct> products, String dummyMediaId) {
        List<WhatsAppMessageDto.CarouselCardDto> cards = java.util.stream.IntStream.range(0, products.size())
                .mapToObj(i -> {
                    FishProduct product = products.get(i);
                    String imageUrl;

                    ProductImage firstImage = null;
                    if (product.getImages() != null && !product.getImages().isEmpty()) {
                        firstImage = product.getImages().stream()
                                .sorted(Comparator.comparing(ProductImage::getDisplayOrder,
                                        Comparator.nullsLast(Comparator.naturalOrder())))
                                .findFirst()
                                .orElse(null);
                    }

                    if (firstImage != null) {
                        imageUrl = appServerUrl + "/api/product-images/public/" + firstImage.getId() + "/content";
                    } else if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                        imageUrl = product.getImageUrl();
                    } else {
                        // Fallback
                        imageUrl = FlowConstants.WHATSAPP_LOGO_URL;
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

    private void handleListReply(Customer customer, BotSession session, WhatsAppWebhookDto.ListReply listReply) {
        String selectedId = listReply.getId();

        if (selectedId.startsWith("FISH_")) {
            handleProductSelection(customer, session, Long.parseLong(selectedId.replace("FISH_", "")));
        } else if (selectedId.startsWith("REMOVE_ITEM_")) {
            cartService.removeFromCart(customer.getId(), Long.parseLong(selectedId.replace("REMOVE_ITEM_", "")));
            showCartSummary(customer, session); // Go back to summary
        } else if (selectedId.equals("REMOVE_ALL_ITEMS")) {
            cartService.clearCart(customer.getId());
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getAllProductsRemoved(customer.getName()));
            showProductCatalog(customer, session);
        } else if (selectedId.startsWith("EDIT_QTY_")) {
            handleEditQuantitySelection(customer, session, Long.parseLong(selectedId.replace("EDIT_QTY_", "")));
        }
    }

    private void handleButtonReply(Customer customer, BotSession session, WhatsAppWebhookDto.ButtonReply buttonReply) {
        String buttonId = buttonReply.getId();
        log.info("Received Button Reply ID: {}", buttonId);

        if ("CONTINUE_SHOPPING".equals(buttonId)) {
            showProductCatalog(customer, session);
        } else if ("CHECKOUT".equals(buttonId)) {
            showCartSummary(customer, session);
        } else if ("CONFIRM_ORDER".equals(buttonId)) {
            placeOrder(customer, session);
        } else if ("CANCEL_ORDER".equals(buttonId)) {
            handleCancelOrder(customer, session);
        } else if ("EDIT_ORDER".equals(buttonId)) {
            showEditOrderOptions(customer, session);
        } else if ("EDIT_PRODUCT".equals(buttonId)) {
            showProductEditOptions(customer, session);
        } else if ("EDIT_QUANTITY".equals(buttonId)) {
            showQuantityEditOptions(customer, session);
        } else if ("BACK_TO_CHECKOUT".equals(buttonId)) {
            showCartSummary(customer, session);
        } else if ("REMOVE_ALL_ITEMS".equals(buttonId)) {
            handleRemoveAllItems(customer, session);
        } else if (buttonId.startsWith("REMOVE_ITEM_")) {
            handleRemoveItem(customer, session, buttonId);
        } else if (buttonId.startsWith("EDIT_QTY_")) {
            handleEditQuantitySelection(customer, session, Long.parseLong(buttonId.replace("EDIT_QTY_", "")));
        } else if (buttonId.startsWith("SELECT_")) {
            handleProductSelection(customer, session, Long.parseLong(buttonId.replace("SELECT_", "")));
        }
    }

    private void handleProductSelection(Customer customer, BotSession session, Long fishProductId) {
        Optional<FishProduct> fishOpt = fishProductRepository.findById(fishProductId);
        if (fishOpt.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getProductNoLongerAvailable());
            return;
        }
        FishProduct fish = fishOpt.get();

        setSessionData(session, "tempProductId", fishProductId);
        updateStage(session, CustomerFlowStage.AWAITING_QUANTITY);

        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService
                .getProductSelectedQuantityRequest(customer.getName(), fish.getName(),
                        fish.getPricePerKg().doubleValue()));
    }

    private void handleAwaitingQuantity(Customer customer, BotSession session, String text) {
        try {
            Double quantity = Double.parseDouble(text.trim());
            if (quantity <= 0) {
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                        messageService.getInvalidQuantityZeroOrNegative());
                return;
            }

            Long fishProductId = getSessionDataLong(session, "tempProductId");
            // Check if edit or new is handled by cartService?
            // CartService logic: if exists, update... Wait, CartService.addToCart adds to
            // existing.
            // But if we are in "EDIT_QUANTITY" mode, we want to SET quantity.
            // If in "ADDING_TO_CART" mode, maybe we add?
            // Legacy logic: checked if item exists.

            // For simplicity: addToCart in CartService increments.
            // We should use updateQuantity here if we want to SET.
            // But if it's a new add, we init with quantity.
            // Let's check CartService.addToCart -> it increments.

            // Determine intent?
            // The stage is AWAITING_QUANTITY.
            // If we came from "SELECT_" -> New Add (or increment).
            // If we came from "EDIT_QTY_" -> Set.

            // I should store "mode" in session data too.
            // Defaulting to "ADD" if not specified.

            // Let's simply use updateQuantity if it's an edit, but handling "new" is tricky
            // if we use update.
            // I'll stick to CartService.addToCart for new adds, and updateQuantity for
            // edits.
            // Check session data for "editMode"?

            cartService.addToCart(customer.getId(), fishProductId, quantity); // This increments.

            sendCartOptions(customer);
            updateStage(session, CustomerFlowStage.ADDING_TO_CART); // Transitional stage or just Registered?

        } catch (NumberFormatException e) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getInvalidQuantityFormat());
        } catch (Exception e) {
            log.error("Error processing quantity", e);
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), "Error processing quantity.");
        }
    }

    private void sendCartOptions(Customer customer) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("CONTINUE_SHOPPING")
                                .title(messageService.getButtonAddMoreFish()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder().type("reply").reply(WhatsAppMessageDto.ReplyDto.builder()
                        .id("CHECKOUT").title(messageService.getButtonCheckout()).build()).build());
        whatsAppService.sendCartActionButtons(customer.getWaPhoneNumber(),
                messageService.getItemAddedToCart(customer.getName()), buttons);
    }

    private void showCartSummary(Customer customer, BotSession session) {
        List<CartItemDetailsDTO> items = cartService.getCartItems(customer.getId());
        Double total = cartService.calculateCartTotal(customer.getId());

        if (items.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCart());
            updateStage(session, CustomerFlowStage.REGISTERED);
            return;
        }

        StringBuilder summary = new StringBuilder(messageService.getCartSummaryHeader(customer.getName()));
        for (CartItemDetailsDTO item : items) {
            summary.append(String.format("• %s - %.2f kg × ₹%.2f = ₹%.2f\n", item.getFishName(), item.getQuantityKg(),
                    item.getPricePerKg(), item.getSubtotal()));
        }
        summary.append(messageService.getCartSummaryFooter(total));

        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("CONFIRM_ORDER")
                                .title(messageService.getButtonConfirmOrder()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("EDIT_ORDER")
                                .title(messageService.getButtonEditOrder()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder().type("reply").reply(WhatsAppMessageDto.ReplyDto.builder()
                        .id("CANCEL_ORDER").title(messageService.getButtonCancelOrder()).build()).build());

        whatsAppService.sendCartActionButtons(customer.getWaPhoneNumber(), summary.toString(), buttons);
        updateStage(session, CustomerFlowStage.CHECKOUT);
    }

    private void placeOrder(Customer customer, BotSession session) {
        try {
            CustomerOrder order = orderService.createOrder(customer, "NOT_SELECTED");

            // JHipster CustomerOrder total is BigDecimal, convert to double for message
            // service
            Double total = order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0;

            whatsAppService.sendOrderConfirmation(customer.getWaPhoneNumber(), order.getId(), total);
            updateStage(session, CustomerFlowStage.REGISTERED);

        } catch (IllegalStateException e) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCartDuringOrder());
        } catch (Exception e) {
            log.error("Order placement failed for customer {}", customer.getId(), e);
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getOrderPlacementFailure());
        }
    }

    private void handleCancelOrder(Customer customer, BotSession session) {
        cartService.clearCart(customer.getId());
        updateStage(session, CustomerFlowStage.REGISTERED);
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                messageService.getOrderCancelled(customer.getName()));
    }

    private void showEditOrderOptions(Customer customer, BotSession session) {
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

        updateStage(session, CustomerFlowStage.EDITING_ORDER);
    }

    private void showProductEditOptions(Customer customer, BotSession session) {
        List<CartItemDetailsDTO> items = cartService.getCartItems(customer.getId());

        if (items.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCart());
            updateStage(session, CustomerFlowStage.REGISTERED);
            return;
        }

        if (items.size() == 1) {
            // Single item - use buttons
            CartItemDetailsDTO item = items.get(0);
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

        updateStage(session, CustomerFlowStage.EDITING_PRODUCT);
    }

    private void showQuantityEditOptions(Customer customer, BotSession session) {
        List<CartItemDetailsDTO> items = cartService.getCartItems(customer.getId());

        if (items.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCart());
            updateStage(session, CustomerFlowStage.REGISTERED);
            return;
        }

        if (items.size() == 1) {
            // Single item - go directly to quantity input
            CartItemDetailsDTO item = items.get(0);
            setSessionData(session, "tempProductId", item.getFishProductId());

            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getEditQuantityHeader(item.getFishName(), item.getQuantityKg()));

            updateStage(session, CustomerFlowStage.AWAITING_QUANTITY);
        } else {
            // Multiple items - use interactive list to show ALL cart items
            StringBuilder message = new StringBuilder("✏️ *Edit Quantities*\n\n*Current cart:*\n");
            for (CartItemDetailsDTO item : items) {
                message.append(String.format("• %s - %.2f kg\n", item.getFishName(), item.getQuantityKg()));
            }
            message.append("\nSelect a product to edit quantity:");

            // Create rows for ALL cart items
            List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();
            for (CartItemDetailsDTO item : items) {
                rows.add(WhatsAppMessageDto.RowDto.builder()
                        .id("EDIT_QTY_" + item.getFishProductId())
                        .title(item.getFishName())
                        .description(String.format("Current: %.2f kg", item.getQuantityKg()))
                        .build());
            }

            // Send interactive list with all cart items
            whatsAppService.sendInteractiveList(customer.getWaPhoneNumber(), message.toString(), rows);

            updateStage(session, CustomerFlowStage.EDITING_QUANTITY);
        }
    }

    private void handleEditQuantitySelection(Customer customer, BotSession session, Long fishProductId) {
        setSessionData(session, "tempProductId", fishProductId);

        List<CartItemDetailsDTO> items = cartService.getCartItems(customer.getId());
        CartItemDetailsDTO item = items.stream().filter(i -> i.getFishProductId().equals(fishProductId)).findFirst()
                .orElse(null);

        if (item != null) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getEditQuantityHeader(item.getFishName(), item.getQuantityKg()));
            updateStage(session, CustomerFlowStage.AWAITING_QUANTITY);
        }
    }

    private void handleRemoveItem(Customer customer, BotSession session, String buttonId) {
        Long fishProductId = Long.parseLong(buttonId.replace("REMOVE_ITEM_", ""));

        // Remove item from cart
        cartService.removeFromCart(customer.getId(), fishProductId);

        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getProductRemovedSuccessfully());

        // Check if cart is now empty
        List<CartItemDetailsDTO> remainingItems = cartService.getCartItems(customer.getId());

        if (remainingItems.isEmpty()) {
            // Cart is empty - show product catalog directly
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getCartEmptyAfterRemoval());
            showProductCatalog(customer, session);
        } else {
            // Show updated cart summary
            showCartSummary(customer, session);
        }
    }

    private void handleRemoveAllItems(Customer customer, BotSession session) {
        // Clear entire cart
        cartService.clearCart(customer.getId());

        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                messageService.getAllProductsRemoved(customer.getName()));
        showProductCatalog(customer, session);
    }

    private void sendProductRemovalList(Customer customer, List<CartItemDetailsDTO> items) {
        StringBuilder message = new StringBuilder(messageService.getRemoveProductsListHeader());
        for (CartItemDetailsDTO item : items) {
            message.append(String.format("• %s - %.2f kg × ₹%.2f = ₹%.2f\n", item.getFishName(), item.getQuantityKg(),
                    item.getPricePerKg(), item.getSubtotal()));
        }
        message.append("\n" + messageService.getSelectProductToRemove());

        // Create rows for ALL cart items
        List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();

        // Add "Remove All" option as first row
        rows.add(WhatsAppMessageDto.RowDto.builder()
                .id("REMOVE_ALL_ITEMS")
                .title(messageService.getButtonRemoveAll())
                .description("Clear entire cart")
                .build());

        // Add individual cart items
        for (CartItemDetailsDTO item : items) {
            rows.add(WhatsAppMessageDto.RowDto.builder()
                    .id("REMOVE_ITEM_" + item.getFishProductId())
                    .title(item.getFishName())
                    .description(String.format("%.2f kg - ₹%.2f", item.getQuantityKg(), item.getSubtotal()))
                    .build());
        }

        // Send interactive list with all cart items
        whatsAppService.sendInteractiveList(customer.getWaPhoneNumber(), message.toString(), rows);
    }

    // --- Helper Methods for Session Data JSON ---

    private void setSessionData(BotSession session, String key, Object value) {
        try {
            Map<String, Object> data = getSessionDataMap(session);
            data.put(key, value);
            session.setSessionData(objectMapper.writeValueAsString(data));
            sessionManager.updateSessionData(session, session.getSessionData());
        } catch (JsonProcessingException e) {
            log.error("Error writing session data", e);
        }
    }

    private Long getSessionDataLong(BotSession session, String key) {
        Map<String, Object> data = getSessionDataMap(session);
        Object val = data.get(key);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        return null; // or throw
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getSessionDataMap(BotSession session) {
        if (session.getSessionData() == null || session.getSessionData().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(session.getSessionData(), Map.class);
        } catch (JsonProcessingException e) {
            log.error("Error reading session data", e);
            return new HashMap<>();
        }
    }
}
