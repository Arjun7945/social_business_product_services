package com.aps.service;

import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.enumeration.CustomerFlowStage;
import com.aps.repository.CustomerRepository;
import com.aps.service.BotSessionManager;
import com.aps.service.customer_flow.CartHandler;
import com.aps.service.customer_flow.CatalogService;
import com.aps.service.customer_flow.CustomerInputHandler;
import com.aps.service.customer_flow.FlowStateService;
import com.aps.service.customer_flow.OrderFlowHandler;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service to handle the complete customer conversation flow via WhatsApp.
 * REFACTORED: Now delegates logic to specialized handlers in
 * com.aps.service.customer_flow
 */
@Service
public class CustomerFlowService {

    private final Logger log = LoggerFactory.getLogger(CustomerFlowService.class);

    private final BotSessionManager sessionManager;
    private final CustomerMessageService messageService;
    private final WhatsAppService whatsAppService;
    private final CustomerRepository customerRepository;

    // Component Services
    private final FlowStateService flowStateService;
    private final CatalogService catalogService;
    private final CustomerInputHandler inputHandler;
    private final CartHandler cartHandler;
    private final OrderFlowHandler orderFlowHandler;
    private final CartService cartService;

    public CustomerFlowService(
            BotSessionManager sessionManager,
            CustomerMessageService messageService,
            WhatsAppService whatsAppService,
            CustomerRepository customerRepository,
            FlowStateService flowStateService,
            CatalogService catalogService,
            CustomerInputHandler inputHandler,
            CartHandler cartHandler,
            OrderFlowHandler orderFlowHandler,
            CartService cartService) {
        this.sessionManager = sessionManager;
        this.messageService = messageService;
        this.whatsAppService = whatsAppService;
        this.customerRepository = customerRepository;
        this.flowStateService = flowStateService;
        this.catalogService = catalogService;
        this.inputHandler = inputHandler;
        this.cartHandler = cartHandler;
        this.orderFlowHandler = orderFlowHandler;
        this.cartService = cartService;
    }

    /**
     * Handle messages from customers (called by Dispatcher).
     */
    public void handleCustomerMessage(Customer customer, WhatsAppWebhookDto.Message message) {
        BotSession session = sessionManager.getSession(customer.getWaPhoneNumber());

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
            inputHandler.handleLocationMessage(customer, session, message.getLocation());
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
            if (flowStateService.isActiveSession(flowStateService.getStage(session))) {
                sendSessionResumptionPrompt(customer);
                return true;
            }
            flowStateService.updateStage(session, CustomerFlowStage.REGISTERED);
            catalogService.showProductCatalog(customer, session);
            return true;
        }
        return false;
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
        whatsAppService.sendCartActionButtons(
                customer.getWaPhoneNumber(),
                messageService.getSessionResumptionPrompt(customer.getName()),
                buttons);
    }

    private void handleTextMessage(Customer customer, BotSession session, String text) {
        CustomerFlowStage stage = flowStateService.getStage(session);
        log.info("Customer {} in stage {} sent: {}", customer.getWaPhoneNumber(), stage, text);

        switch (stage) {
            case NEW:
                if (text.trim().equalsIgnoreCase("hi") || text.trim().equalsIgnoreCase("hello")) {
                    whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                            messageService.getWelcomeMessageNewCustomer());
                    flowStateService.updateStage(session, CustomerFlowStage.AWAITING_NAME);
                } else {
                    whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                            messageService.getWelcomeMessageOtherText());
                }
                break;
            case AWAITING_NAME:
                inputHandler.handleAwaitingName(customer, session, text);
                break;
            case AWAITING_PHONE:
                inputHandler.handleAwaitingPhone(customer, session, text);
                break;
            case AWAITING_QUANTITY:
                cartHandler.handleAwaitingQuantity(customer, session, text);
                break;
            case REGISTERED:
            case BROWSING:
            case ADDING_TO_CART:
            case CHECKOUT:
            case CONFIRMING_ORDER:
            case EDITING_ORDER:
            case EDITING_PRODUCT:
            case EDITING_QUANTITY:
                if (text.trim().equalsIgnoreCase("start")) {
                    catalogService.showProductCatalog(customer, session);
                } else {
                    whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                            messageService.getPromptToBrowse(customer.getName()));
                }
                break;
            default:
                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getUnrecognizedCommand());
        }
    }

    private void handleListReply(Customer customer, BotSession session, WhatsAppWebhookDto.ListReply listReply) {
        String selectedId = listReply.getId();

        if (selectedId.startsWith("FISH_")) {
            cartHandler.handleProductSelection(customer, session, Long.parseLong(selectedId.replace("FISH_", "")));
        } else if (selectedId.startsWith("REMOVE_ITEM_")) {
            cartHandler.handleRemoveItem(customer, session, selectedId);
        } else if (selectedId.equals("REMOVE_ALL_ITEMS")) {
            cartHandler.handleRemoveAllItems(customer, session);
        } else if (selectedId.startsWith("EDIT_QTY_")) {
            cartHandler.handleEditQuantitySelection(customer, session,
                    Long.parseLong(selectedId.replace("EDIT_QTY_", "")));
        } else if (selectedId.equals("EDIT_PRODUCT")) {
            cartHandler.showProductEditOptions(customer, session);
        } else if (selectedId.equals("EDIT_QUANTITY")) {
            cartHandler.showQuantityEditOptions(customer, session);
        } else if (selectedId.equals("CONTINUE_SHOPPING")) {
            catalogService.showProductCatalog(customer, session);
        } else if (selectedId.equals("BACK_TO_CHECKOUT")) {
            cartHandler.showCartSummary(customer, session);
        }
    }

    private void handleButtonReply(Customer customer, BotSession session, WhatsAppWebhookDto.ButtonReply buttonReply) {
        String buttonId = buttonReply.getId();
        log.info("Received Button Reply ID: {}", buttonId);

        if ("CONTINUE_SHOPPING".equals(buttonId)) {
            catalogService.showProductCatalog(customer, session);
        } else if ("CHECKOUT".equals(buttonId)) {
            cartHandler.showCartSummary(customer, session);
        } else if ("CONFIRM_ORDER".equals(buttonId)) {
            orderFlowHandler.placeOrder(customer, session);
        } else if ("CANCEL_ORDER".equals(buttonId)) {
            orderFlowHandler.handleCancelOrder(customer, session);
        } else if ("EDIT_ORDER".equals(buttonId)) {
            orderFlowHandler.showEditOrderOptions(customer, session);
        } else if ("EDIT_PRODUCT".equals(buttonId)) {
            cartHandler.showProductEditOptions(customer, session);
        } else if ("EDIT_QUANTITY".equals(buttonId)) {
            cartHandler.showQuantityEditOptions(customer, session);
        } else if ("BACK_TO_CHECKOUT".equals(buttonId)) {
            cartHandler.showCartSummary(customer, session);
        } else if ("REMOVE_ALL_ITEMS".equals(buttonId)) {
            cartHandler.handleRemoveAllItems(customer, session);
        } else if (buttonId.startsWith("REMOVE_ITEM_")) {
            cartHandler.handleRemoveItem(customer, session, buttonId);
        } else if (buttonId.startsWith("EDIT_QTY_")) {
            cartHandler.handleEditQuantitySelection(customer, session,
                    Long.parseLong(buttonId.replace("EDIT_QTY_", "")));
        } else if ("RESUME_SESSION".equals(buttonId)) {
            recoverLastState(customer.getWaPhoneNumber());
        } else if ("RESTART_SESSION".equals(buttonId)) {
            // cartService.clearCart(customer.getId()); // Optional
            flowStateService.updateStage(session, CustomerFlowStage.REGISTERED);
            catalogService.showProductCatalog(customer, session);
        } else if (buttonId.startsWith("SELECT_")) {
            cartHandler.handleProductSelection(customer, session, Long.parseLong(buttonId.replace("SELECT_", "")));
        }
    }

    public void sendReOrderFlow(Customer customer) {
        BotSession session = sessionManager.getSession(customer.getWaPhoneNumber());
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getOrderAgainPrompt());
        catalogService.showProductCatalog(customer, session);
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

        CustomerFlowStage stage = flowStateService.getStage(session);
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
                catalogService.showProductCatalog(customer, session);
                break;
            case ADDING_TO_CART:
                cartHandler.sendCartOptions(customer);
                break;
            case AWAITING_QUANTITY:
                cartHandler.recoverAwaitingQuantity(customer, session);
                break;
            case CHECKOUT:
                cartHandler.showCartSummary(customer, session);
                break;
            case CONFIRMING_ORDER:
                cartHandler.showCartSummary(customer, session);
                break;
            case EDITING_ORDER:
                orderFlowHandler.showEditOrderOptions(customer, session);
                break;
            case EDITING_PRODUCT:
                cartHandler.showProductEditOptions(customer, session);
                break;
            case EDITING_QUANTITY:
                cartHandler.showQuantityEditOptions(customer, session);
                break;
            default:
                if (stage == CustomerFlowStage.NEW) {
                    whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                            messageService.getWelcomeMessageNewCustomer());
                } else {
                    catalogService.showProductCatalog(customer, session);
                }
                break;
        }
    }

    /**
     * Retrieves the current session data as a JSON string for archival purposes.
     * Delegated to CustomerFlowService for compatibility with existing calls.
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
        // Ensure state is updated/saved? sessionManager.updateState updates state, but
        // this updates data.
        // Logic from original file line 1061:
        sessionManager.updateState(session, session.getCurrentState()); // Ensure saved
    }
}
