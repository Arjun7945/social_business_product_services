package com.aps.service.customer_flow;

import com.aps.config.FlowConstants;
import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.FishProduct;
import com.aps.domain.enumeration.CustomerFlowStage;
import com.aps.repository.FishProductRepository;
import com.aps.service.CartService;
import com.aps.service.CustomerMessageService;
import com.aps.service.WhatsAppService;
import com.aps.service.dto.CartItemDetailsDTO;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.util.InputValidator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Handles Cart and Checkout logic (Quantity, Add/Remove, Summary).
 * Extracted from CustomerFlowService.
 */
@Service
public class CartHandler {

    private final Logger log = LoggerFactory.getLogger(CartHandler.class);

    private final CartService cartService;
    private final WhatsAppService whatsAppService;
    private final CustomerMessageService messageService;
    private final InputValidator inputValidator;
    private final FishProductRepository fishProductRepository;
    private final FlowStateService flowStateService;
    private final CatalogService catalogService;

    public CartHandler(
            CartService cartService,
            WhatsAppService whatsAppService,
            CustomerMessageService messageService,
            InputValidator inputValidator,
            FishProductRepository fishProductRepository,
            FlowStateService flowStateService,
            CatalogService catalogService) {
        this.cartService = cartService;
        this.whatsAppService = whatsAppService;
        this.messageService = messageService;
        this.inputValidator = inputValidator;
        this.fishProductRepository = fishProductRepository;
        this.flowStateService = flowStateService;
        this.catalogService = catalogService;
    }

    public void handleAwaitingQuantity(Customer customer, BotSession session, String text) {
        Double quantity = inputValidator.cleanQuantityInput(text.trim());

        if (!inputValidator.isValidQuantity(quantity)) {
            whatsAppService.sendSimpleText(
                    customer.getWaPhoneNumber(),
                    messageService.getInvalidQuantityFormat() + "\n(Please enter a value between 0.1 and 100)");
            return;
        }

        Long fishProductId = flowStateService.getSessionDataLong(session, "tempProductId");
        if (fishProductId == null) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    "Session expired. Please browse products again.");
            catalogService.showProductCatalog(customer, session);
            return;
        }

        // 1. Strict Stock Check
        FishProduct product = fishProductRepository.findById(fishProductId).orElse(null);
        if (product == null || !Boolean.TRUE.equals(product.getIsAvailable())) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    "⚠️ Sorry, this product is no longer available.");
            catalogService.showProductCatalog(customer, session);
            return;
        }

        // 2. Mode Check (ADD vs EDIT)
        String mode = flowStateService.getSessionDataString(session, "quantityMode");
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

                flowStateService.setSessionData(session, "quantityMode", null); // Clear mode
                sendCartOptions(customer);
                flowStateService.updateStage(session, CustomerFlowStage.ADDING_TO_CART);
            }
        } catch (Exception e) {
            log.error("Error updating cart", e);
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), "Error updating cart. Please try again.");
        }
    }

    public void sendCartOptions(Customer customer) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(
                                WhatsAppMessageDto.ReplyDto.builder().id("CONTINUE_SHOPPING")
                                        .title(messageService.getButtonAddMoreFishLong()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("CHECKOUT")
                                .title(messageService.getButtonCheckout()).build())
                        .build());
        whatsAppService.sendCartActionButtons(customer.getWaPhoneNumber(),
                messageService.getItemAddedToCart(customer.getName()), buttons);
    }

    public void showCartSummary(Customer customer, BotSession session) {
        List<CartItemDetailsDTO> items = cartService.getCartItems(customer.getId());
        Double total = cartService.calculateCartTotal(customer.getId());

        if (items.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCart());
            flowStateService.updateStage(session, CustomerFlowStage.REGISTERED);
            return;
        }

        StringBuilder summary = new StringBuilder(messageService.getCartSummaryHeader(customer.getName()));
        for (CartItemDetailsDTO item : items) {
            summary.append(
                    String.format(
                            "• %s - %.2f kg × ₹%.2f = ₹%.2f\n",
                            item.getFishName(),
                            item.getQuantityKg(),
                            item.getPricePerKg(),
                            item.getSubtotal()));
        }
        summary.append(messageService.getCartSummaryFooter(total));

        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("CONFIRM_ORDER")
                                .title(messageService.getButtonConfirmOrder()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("EDIT_ORDER")
                                .title(messageService.getButtonEditOrder()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("CANCEL_ORDER")
                                .title(messageService.getButtonCancelOrder()).build())
                        .build());

        whatsAppService.sendCartActionButtons(customer.getWaPhoneNumber(), summary.toString(), buttons);
        flowStateService.updateStage(session, CustomerFlowStage.CHECKOUT);
    }

    public void handleRemoveAllItems(Customer customer, BotSession session) {
        cartService.clearCart(customer.getId());
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                messageService.getAllProductsRemoved(customer.getName()));
        catalogService.showProductCatalog(customer, session);
    }

    public void handleRemoveItem(Customer customer, BotSession session, String buttonId) {
        Long fishProductId = Long.parseLong(buttonId.replace("REMOVE_ITEM_", ""));
        cartService.removeFromCart(customer.getId(), fishProductId);
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getProductRemovedSuccessfully());

        List<CartItemDetailsDTO> remainingItems = cartService.getCartItems(customer.getId());
        if (remainingItems.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getCartEmptyAfterRemoval());
            catalogService.showProductCatalog(customer, session);
        } else {
            showCartSummary(customer, session);
        }
    }

    public void sendProductRemovalList(Customer customer, List<CartItemDetailsDTO> items) {
        StringBuilder message = new StringBuilder(messageService.getRemoveProductsListHeader());
        for (CartItemDetailsDTO item : items) {
            message.append(
                    String.format(
                            "• %s - %.2f kg × ₹%.2f = ₹%.2f\n",
                            item.getFishName(),
                            item.getQuantityKg(),
                            item.getPricePerKg(),
                            item.getSubtotal()));
        }
        message.append("\n" + messageService.getSelectProductToRemove());

        List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();
        rows.add(
                WhatsAppMessageDto.RowDto.builder()
                        .id("REMOVE_ALL_ITEMS")
                        .title(messageService.getButtonRemoveAll())
                        .description("Clear entire cart")
                        .build());

        for (CartItemDetailsDTO item : items) {
            rows.add(
                    WhatsAppMessageDto.RowDto.builder()
                            .id("REMOVE_ITEM_" + item.getFishProductId())
                            .title(item.getFishName())
                            .description(String.format("%.2f kg - ₹%.2f", item.getQuantityKg(), item.getSubtotal()))
                            .build());
        }

        whatsAppService.sendInteractiveList(customer.getWaPhoneNumber(), message.toString(), rows);
    }

    public void showQuantityEditOptions(Customer customer, BotSession session) {
        List<CartItemDetailsDTO> items = cartService.getCartItems(customer.getId());

        if (items.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCart());
            flowStateService.updateStage(session, CustomerFlowStage.REGISTERED);
            return;
        }

        if (items.size() == 1) {
            // Single item - go directly to quantity input
            CartItemDetailsDTO item = items.get(0);
            flowStateService.setSessionData(session, "tempProductId", item.getFishProductId());
            flowStateService.setSessionData(session, "quantityMode", "EDIT");

            whatsAppService.sendSimpleText(
                    customer.getWaPhoneNumber(),
                    messageService.getEditQuantityHeader(item.getFishName(), item.getQuantityKg()));

            flowStateService.updateStage(session, CustomerFlowStage.AWAITING_QUANTITY);
        } else {
            // Multiple items - use interactive list to show ALL cart items
            StringBuilder message = new StringBuilder(
                    messageService.getEditQuantitiesListHeader() + "\n\n" + messageService.getCurrentCartHeader()
                            + "\n");
            for (CartItemDetailsDTO item : items) {
                message.append(String.format("• %s - %.2f kg\n", item.getFishName(), item.getQuantityKg()));
            }
            message.append("\n" + messageService.getSelectProductToEditPrompt());

            List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();
            for (CartItemDetailsDTO item : items) {
                rows.add(
                        WhatsAppMessageDto.RowDto.builder()
                                .id("EDIT_QTY_" + item.getFishProductId())
                                .title(item.getFishName())
                                .description(String.format("Current: %.2f kg", item.getQuantityKg()))
                                .build());
            }

            whatsAppService.sendInteractiveList(customer.getWaPhoneNumber(), message.toString(), rows);
            flowStateService.updateStage(session, CustomerFlowStage.EDITING_QUANTITY);
        }
    }

    public void handleEditQuantitySelection(Customer customer, BotSession session, Long fishProductId) {
        flowStateService.setSessionData(session, "tempProductId", fishProductId);
        flowStateService.setSessionData(session, "quantityMode", "EDIT");

        List<CartItemDetailsDTO> items = cartService.getCartItems(customer.getId());
        CartItemDetailsDTO item = items.stream().filter(i -> i.getFishProductId().equals(fishProductId)).findFirst()
                .orElse(null);

        if (item != null) {
            whatsAppService.sendSimpleText(
                    customer.getWaPhoneNumber(),
                    messageService.getEditQuantityHeader(item.getFishName(), item.getQuantityKg()));
            flowStateService.updateStage(session, CustomerFlowStage.AWAITING_QUANTITY);
        }
    }

    public void showProductEditOptions(Customer customer, BotSession session) {
        List<CartItemDetailsDTO> items = cartService.getCartItems(customer.getId());

        if (items.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCart());
            flowStateService.updateStage(session, CustomerFlowStage.REGISTERED);
            return;
        }

        if (items.size() == 1) {
            CartItemDetailsDTO item = items.get(0);
            List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                    WhatsAppMessageDto.ButtonDto.builder()
                            .type("reply")
                            .reply(
                                    WhatsAppMessageDto.ReplyDto.builder()
                                            .id("REMOVE_ITEM_" + item.getFishProductId())
                                            .title(messageService.getButtonRemoveProduct(item.getFishName()))
                                            .build())
                            .build(),
                    WhatsAppMessageDto.ButtonDto.builder()
                            .type("reply")
                            .reply(
                                    WhatsAppMessageDto.ReplyDto.builder().id("BACK_TO_CHECKOUT")
                                            .title(messageService.getButtonBackToCheckout()).build())
                            .build());

            whatsAppService.sendCartActionButtons(
                    customer.getWaPhoneNumber(),
                    messageService.getRemoveProductHeaderSingle() +
                            String.format("• %s - %.2f kg\n\n", item.getFishName(), item.getQuantityKg()) +
                            messageService.getRemoveProductPrompt(),
                    buttons);
        } else {
            sendProductRemovalList(customer, items);
        }

        flowStateService.updateStage(session, CustomerFlowStage.EDITING_PRODUCT);
    }

    public void handleProductSelection(Customer customer, BotSession session, Long fishProductId) {
        FishProduct fish = fishProductRepository.findById(fishProductId).orElse(null);
        if (fish == null) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getProductNoLongerAvailable());
            return;
        }

        flowStateService.setSessionData(session, "tempProductId", fishProductId);
        // Default mode is ADD (Increment)
        flowStateService.setSessionData(session, "quantityMode", "ADD");
        flowStateService.updateStage(session, CustomerFlowStage.AWAITING_QUANTITY);

        whatsAppService.sendSimpleText(
                customer.getWaPhoneNumber(),
                messageService.getProductSelectedQuantityRequest(customer.getName(), fish.getName(),
                        fish.getPricePerKg().doubleValue()));
    }

    public void recoverAwaitingQuantity(Customer customer, BotSession session) {
        Long fishProductId = flowStateService.getSessionDataLong(session, "tempProductId");
        if (fishProductId != null) {
            fishProductRepository.findById(fishProductId)
                    .ifPresent(fish -> whatsAppService.sendSimpleText(
                            customer.getWaPhoneNumber(),
                            messageService.getProductSelectedQuantityRequest(
                                    customer.getName(),
                                    fish.getName(),
                                    fish.getPricePerKg().doubleValue())));
        } else {
            // Fallback if data lost
            catalogService.showProductCatalog(customer, session);
        }
    }
}
