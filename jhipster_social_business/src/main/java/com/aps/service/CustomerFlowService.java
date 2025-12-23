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
import com.aps.service.errors.ProductUnavailableException;
import com.aps.service.util.InputValidator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
            if (handleGlobalCommands(customer, session, text)) {
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

    private boolean handleGlobalCommands(Customer customer, BotSession session, String text) {
        if (text.equalsIgnoreCase("start") || text.equalsIgnoreCase("hi") || text.equalsIgnoreCase("hello")) {
            // Check if there is an ACTIVE session before just restarting
            if (isActiveSession(getStage(session))) {
                sendSessionResumptionPrompt(customer);
                return true;
            }
            updateStage(session, CustomerFlowStage.REGISTERED);
            showProductCatalog(customer, session);
            return true;
        }

        return false;
    }

    private boolean isActiveSession(CustomerFlowStage stage) {
        // Define what counts as "Active" where we shouldn't just restart
        return stage == CustomerFlowStage.BROWSING ||
                stage == CustomerFlowStage.ADDING_TO_CART ||
                stage == CustomerFlowStage.AWAITING_QUANTITY ||
                stage == CustomerFlowStage.CHECKOUT ||
                stage == CustomerFlowStage.CONFIRMING_ORDER ||
                stage == CustomerFlowStage.EDITING_ORDER ||
                stage == CustomerFlowStage.EDITING_PRODUCT ||
                stage == CustomerFlowStage.EDITING_QUANTITY;
    }

    private void sendSessionResumptionPrompt(Customer customer) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("RESUME_SESSION")
                                .title(messageService.getButtonResume()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("RESTART_SESSION")
                                .title(messageService.getButtonStartNew()).build())
                        .build());
        whatsAppService.sendCartActionButtons(customer.getWaPhoneNumber(),
                messageService.getSessionResumptionPrompt(customer.getName()), buttons);
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
            case CHECKOUT: // Should respond to text in checkout? Maybe.
            case CONFIRMING_ORDER:
            case EDITING_ORDER:
            case EDITING_PRODUCT:
            case EDITING_QUANTITY:
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

        // Send the Main Header as a separate Text Message FIRST
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                messageService.getProductCatalogHeader(customer.getName()));

        // --- Logic Update: Carousel Chunking & Rebalancing ---

        // Rule: Carousel must have at least 3 cards (Customer Requirement).
        // If total items < 3, we cannot use carousel. Move them to list.
        if (carouselProducts.size() < 3) {
            listProducts.addAll(0, carouselProducts);
            carouselProducts.clear();
        } else {
            // 1. Create Chunks of Max 10
            List<List<FishProduct>> chunks = new java.util.ArrayList<>();
            int chunkSize = 10;
            for (int i = 0; i < carouselProducts.size(); i += chunkSize) {
                int end = Math.min(carouselProducts.size(), i + chunkSize);
                chunks.add(new java.util.ArrayList<>(carouselProducts.subList(i, end)));
            }

            // 2. Rebalance Logic
            // If the last chunk has fewer than 3 items, borrow from the previous chunk.
            if (chunks.size() > 1) {
                List<FishProduct> lastChunk = chunks.get(chunks.size() - 1);

                if (lastChunk.size() < 3) {
                    List<FishProduct> prevChunk = chunks.get(chunks.size() - 2);
                    int needed = 3 - lastChunk.size(); // e.g., if sizes are 10, 1 -> needed=2.

                    // Safety check: ensure prevChunk has enough items to give while staying >= 3?
                    // Previous chunk must be 10 (since it wasn't the last). 10 - 2 = 8. Safe.

                    for (int k = 0; k < needed; k++) {
                        // Remove from end of previous
                        FishProduct movedItem = prevChunk.remove(prevChunk.size() - 1);
                        // Add to start of last
                        lastChunk.add(0, movedItem);
                    }
                }
            }

            // 3. Send Carousels
            int part = 1;
            int totalParts = chunks.size();
            for (List<FishProduct> chunk : chunks) {
                String bodyText = totalParts > 1 ? messageService.getCarouselTitle(part, totalParts)
                        : messageService.getCarouselTitleSingle();
                sendProductCarousel(customer, chunk, dummyMediaId, bodyText);
                part++;
            }
        }

        if (!listProducts.isEmpty()) {
            String title = !carouselProducts.isEmpty() ? messageService.getProductsWithoutImagesTitle()
                    : messageService.getAvailableFishTitle();
            sendProductList(customer, listProducts, title);
        }

        updateStage(session, CustomerFlowStage.BROWSING);
    }

    private void sendProductCarousel(Customer customer, List<FishProduct> products, String dummyMediaId,
            String bodyText) {
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
                bodyText != null ? bodyText : messageService.getCarouselBodyDefault(), cards);
    }

    private void sendProductList(Customer customer, List<FishProduct> products, String title) {
        List<WhatsAppMessageDto.RowDto> rows = products.stream()
                .map(p -> WhatsAppMessageDto.RowDto.builder()
                        .id("FISH_" + p.getId())
                        .title(p.getName())
                        .description(String.format("₹%.2f/kg", p.getPricePerKg()))
                        .build())
                .collect(Collectors.toList());

        whatsAppService.sendInteractiveList(customer.getWaPhoneNumber(), title, rows);
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
        } else if ("RESUME_SESSION".equals(buttonId)) {
            // Do nothing, just acknowledge? Or re-show current state?
            // Since we don't know exactly what the *last* message was, easiest is to Just
            // say "Resumed"
            // Or, smarter: look at stage and show relevant menu.
            CustomerFlowStage stage = getStage(session);
            if (stage == CustomerFlowStage.CHECKOUT)
                showCartSummary(customer, session);
            else if (stage == CustomerFlowStage.BROWSING)
                showProductCatalog(customer, session);
            else
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), "✅ *Resuming...* Please continue.");
        } else if ("RESTART_SESSION".equals(buttonId)) {
            cartService.clearCart(customer.getId()); // Optional: Clear cart on hard restart? No, maybe just show
                                                     // catalog.
            // Actually user said "Start New", implying they want to browse.
            updateStage(session, CustomerFlowStage.REGISTERED);
            showProductCatalog(customer, session);
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
        // Default mode is ADD (Increment)
        setSessionData(session, "quantityMode", "ADD");
        updateStage(session, CustomerFlowStage.AWAITING_QUANTITY);

        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService
                .getProductSelectedQuantityRequest(customer.getName(), fish.getName(),
                        fish.getPricePerKg().doubleValue()));
    }

    private void handleAwaitingQuantity(Customer customer, BotSession session, String text) {
        Double quantity = inputValidator.cleanQuantityInput(text.trim());

        if (!inputValidator.isValidQuantity(quantity)) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getInvalidQuantityFormat() + "\n(Please enter a value between 0.1 and 100)");
            return;
        }

        Long fishProductId = getSessionDataLong(session, "tempProductId");
        if (fishProductId == null) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    "Session expired. Please browse products again.");
            showProductCatalog(customer, session);
            return;
        }

        // 1. Strict Stock Check
        FishProduct product = fishProductRepository.findById(fishProductId).orElse(null);
        if (product == null || !Boolean.TRUE.equals(product.getIsAvailable())) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    "⚠️ Sorry, this product is no longer available.");
            showProductCatalog(customer, session);
            return;
        }

        // 2. Mode Check (ADD vs EDIT)
        String mode = getSessionDataString(session, "quantityMode");
        if (mode == null)
            mode = "ADD"; // Default

        try {
            if ("EDIT".equals(mode)) {
                // EDIT Mode: Replace/Set quantity
                cartService.updateQuantity(customer.getId(), fishProductId, quantity);
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                        messageService.getQuantityUpdatedMessage(quantity));

                // Return to appropriate menu
                showCartSummary(customer, session);
            } else {
                // ADD Mode: Increment quantity
                cartService.addToCart(customer.getId(), fishProductId, quantity);

                setSessionData(session, "quantityMode", null); // Clear mode
                sendCartOptions(customer);
                updateStage(session, CustomerFlowStage.ADDING_TO_CART);
            }
        } catch (Exception e) {
            log.error("Error updating cart", e);
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), "Error updating cart. Please try again.");
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

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    whatsAppService.sendOrderConfirmation(customer.getWaPhoneNumber(), order.getId(), total);
                }
            });

            updateStage(session, CustomerFlowStage.REGISTERED);

        } catch (ProductUnavailableException e) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    "🚫 " + e.getMessage() + "\nPlease remove the unavailable item from your cart.");
            showCartSummary(customer, session);
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
            setSessionData(session, "quantityMode", "EDIT");

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
        setSessionData(session, "quantityMode", "EDIT");

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

    /**
     * Resends the last state/menu to the customer.
     * Used when a customer clicks an expired/stale button.
     */
    public void recoverLastState(String phoneNumber) {
        BotSession session = sessionManager.getSession(phoneNumber);
        Customer customer = customerRepository.findByWaPhoneNumber(phoneNumber).orElse(null);

        if (customer == null || session == null) {
            log.warn("Cannot recover state for unknown customer: {}", phoneNumber);
            return;
        }

        CustomerFlowStage stage = getStage(session);
        log.info("Recovering state for customer {}: {}", phoneNumber, stage);

        switch (stage) {
            case AWAITING_NAME:
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                        messageService.getWelcomeMessageNewCustomer());
                break;
            case AWAITING_PHONE:
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                        messageService.getNameConfirmation(customer.getName()));
                break;
            case AWAITING_LOCATION:
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                        messageService.getPhoneConfirmationAndLocationRequest());
                break;
            case REGISTERED:
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                        messageService.getPromptToBrowse(customer.getName()));
                break;
            case BROWSING:
                showProductCatalog(customer, session);
                break;
            case ADDING_TO_CART:
                sendCartOptions(customer);
                break;
            case AWAITING_QUANTITY:
                // Need to know WHICH product they were adding/editing to show prompt.
                // Session data might have it.
                Long fishProductId = getSessionDataLong(session, "tempProductId");
                if (fishProductId != null) {
                    fishProductRepository.findById(fishProductId)
                            .ifPresent(fish -> whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                                    messageService
                                            .getProductSelectedQuantityRequest(customer.getName(), fish.getName(),
                                                    fish.getPricePerKg().doubleValue())));
                } else {
                    // Fallback if data lost
                    showProductCatalog(customer, session);
                }
                break;
            case CHECKOUT:
                showCartSummary(customer, session);
                break;
            case CONFIRMING_ORDER:
                showCartSummary(customer, session); // Go back to summary summary
                break;
            case EDITING_ORDER:
                showEditOrderOptions(customer, session);
                break;
            case EDITING_PRODUCT:
                showProductEditOptions(customer, session);
                break;
            case EDITING_QUANTITY:
                showQuantityEditOptions(customer, session);
                break;
            default:
                // For NEW or undefined, just show welcome or catalog
                if (stage == CustomerFlowStage.NEW) {
                    whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                            messageService.getWelcomeMessageNewCustomer());
                } else {
                    showProductCatalog(customer, session);
                }
                break;
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

    private String getSessionDataString(BotSession session, String key) {
        Map<String, Object> data = getSessionDataMap(session);
        Object val = data.get(key);
        if (val instanceof String) {
            return (String) val;
        }
        return null;
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

    /**
     * Retrieves the current session data as a JSON string for archival purposes.
     */
    public String getSessionDataForArchival(String waPhoneNumber) {
        BotSession session = sessionManager.getSession(waPhoneNumber);
        if (session == null) {
            return null;
        }
        return session.getSessionData();
    }

    /**
     * Restores session data from a JSON string.
     */
    public void restoreSessionData(String waPhoneNumber, String jsonData) {
        if (jsonData == null) {
            return;
        }
        BotSession session = sessionManager.getSession(waPhoneNumber);
        session.setSessionData(jsonData);
        sessionManager.updateState(session, session.getCurrentState()); // Ensure saved
    }

}
