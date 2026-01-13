package com.aps.service;

import com.aps.config.FlowConstants;
import com.aps.domain.BotSession;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AdminFlowStage;
import com.aps.service.admin.AccountsManagementService;
import com.aps.service.admin.AssistantAdminManagementService;
import com.aps.service.admin.CustomerManagementService;
import com.aps.service.admin.DeliveryPersonManagementService;
import com.aps.service.admin.ExecutiveManagementService;
import com.aps.service.admin.ProductManagementService;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminFlowService {

    private final Logger log = LoggerFactory.getLogger(AdminFlowService.class);

    private final WhatsAppService whatsAppService;
    private final BotSessionManager sessionManager;
    private final CustomerManagementService customerManagementService;
    private final ProductManagementService productManagementService;
    private final DeliveryPersonManagementService deliveryPersonManagementService;
    private final ExecutiveManagementService executiveManagementService;
    private final AssistantAdminManagementService assistantAdminManagementService;
    private final AccountsManagementService accountsManagementService;
    private final CreditCustomerFlowService creditCustomerFlowService;

    public AdminFlowService(
            @Lazy WhatsAppService whatsAppService,
            BotSessionManager sessionManager,
            CustomerManagementService customerManagementService,
            ProductManagementService productManagementService,
            DeliveryPersonManagementService deliveryPersonManagementService,
            ExecutiveManagementService executiveManagementService,
            AssistantAdminManagementService assistantAdminManagementService,
            AccountsManagementService accountsManagementService,
            CreditCustomerFlowService creditCustomerFlowService) {
        this.whatsAppService = whatsAppService;
        this.sessionManager = sessionManager;
        this.customerManagementService = customerManagementService;
        this.productManagementService = productManagementService;
        this.deliveryPersonManagementService = deliveryPersonManagementService;
        this.executiveManagementService = executiveManagementService;
        this.assistantAdminManagementService = assistantAdminManagementService;
        this.accountsManagementService = accountsManagementService;
        this.creditCustomerFlowService = creditCustomerFlowService;
    }

    public void handleAdminMessage(TeamMember admin, WhatsAppWebhookDto.Message message) {
        log.info("Processing admin message from: {} ({})", admin.getName(), admin.getRole());

        BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());

        // Check for ABORT command
        if (message.getType().equals("text") && message.getText() != null) {
            String text = message.getText().getBody().trim();
            if (isAbortCommand(text)) {
                handleAbortCommand(admin, session);
                return;
            }
        }

        if (message.getType().equals("text") && message.getText() != null) {
            handleTextMessage(admin, session, message.getText().getBody());
        } else if (message.getType().equals("location") && message.getLocation() != null) {
            handleLocationMessage(admin, session, message.getLocation());
        } else if (message.getType().equals("interactive")) {
            String type = message.getInteractive().getType();
            if (type.equals("button_reply")) {
                handleButtonReply(admin, session, message.getInteractive().getButtonReply());
            } else if (type.equals("list_reply")) {
                String id = message.getInteractive().getListReply().getId();
                WhatsAppWebhookDto.ButtonReply mockButton = new WhatsAppWebhookDto.ButtonReply();
                mockButton.setId(id);
                handleButtonReply(admin, session, mockButton);
            }
        } else if (message.getType().equals("image") && message.getImage() != null) {
            String currentState = session.getCurrentState();
            if (AdminFlowStage.AWAITING_PRODUCT_IMAGES.name().equals(currentState)) {
                productManagementService.handleProductImageMessage(admin, session, message);
            }
        }

        session.setLastActiveAt(Instant.now());
        // sessionManager.save(session); // Manager updates usually save
    }

    private void handleTextMessage(TeamMember admin, BotSession session, String text) {
        String stageName = session.getCurrentState();
        AdminFlowStage stage;
        try {
            stage = AdminFlowStage.valueOf(stageName);
        } catch (Exception e) {
            stage = AdminFlowStage.IDLE;
        }

        log.info("Admin {} in stage {} sent: {}", admin.getName(), stage, text);

        switch (stage) {
            case IDLE:
                handleIdleState(admin, session, text);
                break;
            // Customer Management
            case AWAITING_CUST_NAME:
                customerManagementService.handleCustomerNameInput(admin, session, text);
                break;
            case AWAITING_CUST_PHONE:
                customerManagementService.handleCustomerPhoneInput(admin, session, text);
                break;
            case AWAITING_CUST_WAPHONE:
                customerManagementService.handleCustomerWaPhoneInput(admin, session, text);
                break;
            case AWAITING_DELETE_CUST_ID:
                customerManagementService.handleDeleteCustomerInput(admin, session, text);
                break;
            case AWAITING_UPDATE_CUST_SEARCH:
                customerManagementService.handleUpdateCustomerSearch(admin, session, text);
                break;
            case AWAITING_UPDATE_CUST_NEW_VALUE:
                customerManagementService.handleUpdateValueInput(admin, session, text);
                break;
            // Product Management
            case AWAITING_PRODUCT_NAME:
                productManagementService.handleProductNameInput(admin, session, text);
                break;
            case AWAITING_PRODUCT_PRICE:
                productManagementService.handleProductPriceInput(admin, session, text);
                break;
            case AWAITING_PRODUCT_DESCRIPTION:
                productManagementService.handleProductDescriptionInput(admin, session, text);
                break; // Missing availability handlers?
            case AWAITING_PRODUCT_AVAILABILITY:
                productManagementService.handleProductAvailabilityInput(admin, session, text);
                break;
            case AWAITING_PRODUCT_IMAGES:
                if (text.equalsIgnoreCase(FlowConstants.CMD_DONE) || text.equalsIgnoreCase(FlowConstants.CMD_SKIP)) {
                    whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "⏳ Finalizing product... please wait.");
                    sessionManager.updateState(session, AdminFlowStage.PROCESSING.name());
                    productManagementService.finalizeProductAdd(admin, session);
                }
                break;
            // Delivery Person Management
            case AWAITING_DELIVERY_NAME:
                deliveryPersonManagementService.handleDeliveryPersonNameInput(admin, session, text);
                break;
            case AWAITING_DELIVERY_PHONE:
                deliveryPersonManagementService.handleDeliveryPersonPhoneInput(admin, session, text);
                break;
            case AWAITING_DELIVERY_WAPHONE:
                deliveryPersonManagementService.handleDeliveryPersonWaPhoneInput(admin, session, text);
                break;
            case AWAITING_DELIVERY_STATUS:
                deliveryPersonManagementService.handleDeliveryPersonStatusInput(admin, session, text);
                break;
            case AWAITING_DELETE_DELIVERY_SELECTION:
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "Please select an option from the list.");
                break;
            // Executive Management
            case AWAITING_EXEC_NAME:
                executiveManagementService.handleExecutiveNameInput(admin, session, text);
                break;
            case AWAITING_EXEC_PHONE:
                executiveManagementService.handleExecutivePhoneInput(admin, session, text);
                break;
            case AWAITING_EXEC_WAPHONE:
                executiveManagementService.handleExecutiveWaPhoneInput(admin, session, text);
                break;
            case AWAITING_EXEC_STATUS:
                executiveManagementService.handleExecutiveStatusInput(admin, session, text);
                break;
            // Assistant Admin Management
            case AWAITING_ASSISTANT_NAME:
                assistantAdminManagementService.handleAssistantAdminNameInput(admin, session, text);
                break;
            case AWAITING_ASSISTANT_PHONE:
                assistantAdminManagementService.handleAssistantAdminPhoneInput(admin, session, text);
                break;
            case AWAITING_ASSISTANT_WAPHONE:
                assistantAdminManagementService.handleAssistantAdminWaPhoneInput(admin, session, text);
                break;
            case AWAITING_ASSISTANT_STATUS:
                assistantAdminManagementService.handleAssistantAdminStatusInput(admin, session, text);
                break;
            // Accounts Team Management
            case AWAITING_ACC_NAME:
                accountsManagementService.handleAccountsNameInput(admin, session, text);
                break;
            case AWAITING_ACC_PHONE:
                accountsManagementService.handleAccountsPhoneInput(admin, session, text);
                break;
            case AWAITING_ACC_WAPHONE:
                accountsManagementService.handleAccountsWaPhoneInput(admin, session, text);
                break;
            case AWAITING_ACC_STATUS:
                accountsManagementService.handleAccountsStatusInput(admin, session, text);
                break;
            default:
                showMainMenu(admin, session);
        }
    }

    private void handleIdleState(TeamMember admin, BotSession session, String text) {
        if (text.trim().equalsIgnoreCase(FlowConstants.CMD_HI)
                || text.trim().equalsIgnoreCase(FlowConstants.CMD_HELLO)) {
            showMainMenu(admin, session);
        } else {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "Send 'hi' to see the admin menu.");
        }
    }

    private void showMainMenu(TeamMember admin, BotSession session) {
        List<WhatsAppMessageDto.RowDto> rows = com.aps.service.util.AdminMenuHelper.getMainMenuRows();

        String greeting = String.format(
                "🎉 *Welcome, %s!* 👑\n\n" + "You are logged in as: *%s*\n\n" + "Please select a section to manage:",
                admin.getName(),
                admin.getRole());

        whatsAppService.sendInteractiveList(admin.getWaPhoneNumber(), greeting, rows);
        sessionManager.updateState(session, AdminFlowStage.IDLE.name());
    }

    private void handleButtonReply(TeamMember admin, BotSession session, WhatsAppWebhookDto.ButtonReply buttonReply) {
        String buttonId = buttonReply.getId();
        log.info("Admin button reply: {}", buttonId);

        switch (buttonId) {
            case "CUSTOMER_SECTION":
                customerManagementService.showCustomerMenu(admin);
                break;
            case "PRODUCT_SECTION":
                productManagementService.showProductMenu(admin);
                break;
            case "DELIVERY_SECTION":
                deliveryPersonManagementService.showDeliveryPersonMenu(admin);
                break;
            case "EXECUTIVE_SECTION":
                executiveManagementService.showExecutiveMenu(admin);
                break;
            case "ASSISTANT_SECTION":
                assistantAdminManagementService.showAssistantAdminMenu(admin);
                break;
            case "ACCOUNTS_SECTION":
                accountsManagementService.showAccountsMenu(admin);
                break;
            case "CONTACT_DEVELOPER":
                whatsAppService.sendSimpleText(
                        admin.getWaPhoneNumber(),
                        "📞 *Contact Developer*\n\nFor technical support, please contact:\n[Developer Contact Info]");
                break;
            case "ADD_CUSTOMER":
                customerManagementService.startAddCustomer(admin, session);
                break;
            case "SHOW_ALL_CUSTOMERS":
                customerManagementService.showAllCustomers(admin);
                break;
            case "UPDATE_CUSTOMER_MENU":
                customerManagementService.startUpdateCustomer(admin, session);
                break;
            case "ADD_DELIVERY":
                deliveryPersonManagementService.startAddDeliveryPerson(admin, session);
                break;
            case "SHOW_ALL_DELIVERY":
                deliveryPersonManagementService.showAllDeliveryPersons(admin);
                break;
            case "DELETE_DELIVERY_MENU":
                deliveryPersonManagementService.startDeleteDeliveryPerson(admin, session);
                break;
            case "ADD_EXECUTIVE":
                executiveManagementService.startAddExecutive(admin, session);
                break;
            case "SHOW_ALL_EXECUTIVE":
                executiveManagementService.showAllExecutives(admin);
                break;
            case "ADD_ASSISTANT":
                assistantAdminManagementService.startAddAssistantAdmin(admin, session);
                break;
            case "SHOW_ALL_ASSISTANT":
                assistantAdminManagementService.showAllAssistantAdmins(admin);
                break;
            case "ADD_ACCOUNTS":
                accountsManagementService.startAddAccountsMember(admin, session);
                break;
            case "SHOW_ALL_ACCOUNTS":
                accountsManagementService.showAllAccountsMembers(admin);
                break;
            case "CONFIRM_ADD":
                handleConfirmAdd(admin, session);
                break;
            case "CANCEL_OPERATION":
                handleAbortCommand(admin, session);
                break;
            case "ADD_PRODUCT":
                productManagementService.startAddProduct(admin, session);
                break;
            case "SHOW_ALL_PRODUCTS":
                productManagementService.showAllProducts(admin);
                break;
            case "AVAIL_YES":
                productManagementService.handleProductAvailabilityInput(admin, session, "yes");
                break;
            case "AVAIL_NO":
                productManagementService.handleProductAvailabilityInput(admin, session, "no");
                break;
            // Executive Status Buttons
            case "EXEC_ACTIVE_YES":
                executiveManagementService.handleExecutiveStatusInput(admin, session, "yes");
                break;
            case "EXEC_ACTIVE_NO":
                executiveManagementService.handleExecutiveStatusInput(admin, session, "no");
                break;
            // Delivery Status Buttons
            case "DELIVERY_ACTIVE_YES":
                deliveryPersonManagementService.handleDeliveryPersonStatusInput(admin, session, "yes");
                break;
            case "DELIVERY_ACTIVE_NO":
                deliveryPersonManagementService.handleDeliveryPersonStatusInput(admin, session, "no");
                break;
            // Assistant Status Buttons
            case "ASSISTANT_ACTIVE_YES":
                assistantAdminManagementService.handleAssistantAdminStatusInput(admin, session, "yes");
                break;
            case "ASSISTANT_ACTIVE_NO":
                assistantAdminManagementService.handleAssistantAdminStatusInput(admin, session, "no");
                break;
            // Accounts Status Buttons
            case "ACC_ACTIVE_YES":
                accountsManagementService.handleAccountsStatusInput(admin, session, "yes");
                break;
            case "ACC_ACTIVE_NO":
                accountsManagementService.handleAccountsStatusInput(admin, session, "no");
                break;
            case "BACK_TO_MAIN":
                showMainMenu(admin, session);
                break;
            case "CREDIT_CUSTOMER_SECTION":
                creditCustomerFlowService.showCreditCustomerMenu(admin);
                break;
            case CreditCustomerFlowService.MENU_CREDIT_OC:
                creditCustomerFlowService.showCreditCustomerOrders(admin);
                break;
            case CreditCustomerFlowService.MENU_CREDIT_CRUD:
                creditCustomerFlowService.showCreditCustomerMenu(admin);
                break;
            case "SHOW_ALL_CREDIT_CUSTOMERS":
                customerManagementService.showAllCreditCustomers(admin);
                break;
            case "ADD_CREDIT_CUSTOMER":
                customerManagementService.startAddCreditCustomer(admin, session);
                break;
            case "UPDATE_CREDIT_CUSTOMER":
                customerManagementService.startUpdateCustomer(admin, session);
                break;
            case "DELETE_CUSTOMER_MENU":
                customerManagementService.startDeleteCustomer(admin, session);
                break;
            default:
                if (buttonId.startsWith(CreditCustomerFlowService.PREFIX_ADMIN_CREDIT_ALLOW) ||
                        buttonId.startsWith(CreditCustomerFlowService.PREFIX_ADMIN_CREDIT_GRANT) ||
                        buttonId.startsWith(CreditCustomerFlowService.PREFIX_ADMIN_CREDIT_DENY)) {
                    creditCustomerFlowService.handleAdminDecision(admin, buttonId);
                } else if (buttonId.startsWith(CreditCustomerFlowService.PREFIX_CREDIT_ORDER_DTL)) {
                    Long orderId = Long
                            .parseLong(buttonId.replace(CreditCustomerFlowService.PREFIX_CREDIT_ORDER_DTL, ""));
                    creditCustomerFlowService.handleCreditOrderSelection(admin, orderId);
                } else if (buttonId.startsWith(CreditCustomerFlowService.PREFIX_CREDIT_PAY_LINK) ||
                        buttonId.startsWith(CreditCustomerFlowService.PREFIX_CREDIT_COD)) {
                    creditCustomerFlowService.handleCreditOrderAction(admin, buttonId);
                } else if (buttonId.startsWith("UPD_CUST_SEL_")) {
                    customerManagementService.handleUpdateCustSelect(admin, session, buttonId);
                } else if (buttonId.startsWith("UPD_FIELD_") || buttonId.equals("CANCEL_UPDATE")) {
                    customerManagementService.handleUpdateFieldSelect(admin, session, buttonId);
                } else if (buttonId.startsWith("ROLE_") || buttonId.startsWith("ZONE_")) {
                    customerManagementService.handleUpdateOptionSelection(admin, session, buttonId);
                } else if (buttonId.startsWith("DELETE_DP_") || buttonId.equals("GO_BACK_TO_LIST")
                        || buttonId.equals("REPORT_ISSUE")) {
                    deliveryPersonManagementService.handleDeleteDeliveryPersonSelection(admin, session, buttonId);
                } else if (buttonId.startsWith("CONFIRM_DELETE_DP_")) {
                    Long id = Long.parseLong(buttonId.replace("CONFIRM_DELETE_DP_", ""));
                    deliveryPersonManagementService.finalizeDeliveryPersonDelete(admin, id);
                } else {
                    showMainMenu(admin, session);
                }
        }
    }

    private void handleConfirmAdd(TeamMember admin, BotSession session) {
        String currentState = session.getCurrentState();
        if (AdminFlowStage.CONFIRMING_CUST_ADD.name().equals(currentState)) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "⏳ Processing customer addition... please wait.");
            sessionManager.updateState(session, AdminFlowStage.PROCESSING.name());
            customerManagementService.finalizeCustomerAdd(admin, session);
        } else if (AdminFlowStage.CONFIRMING_DELIVERY_ADD.name().equals(currentState)) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "⏳ Processing delivery person addition... please wait.");
            sessionManager.updateState(session, AdminFlowStage.PROCESSING.name());
            deliveryPersonManagementService.finalizeDeliveryPersonAdd(admin, session);
        } else if (AdminFlowStage.CONFIRMING_EXEC_ADD.name().equals(currentState)) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "⏳ Processing executive addition... please wait.");
            sessionManager.updateState(session, AdminFlowStage.PROCESSING.name());
            executiveManagementService.finalizeExecutiveAdd(admin, session);
        } else if (AdminFlowStage.CONFIRMING_ASSISTANT_ADD.name().equals(currentState)) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "⏳ Processing assistant addition... please wait.");
            sessionManager.updateState(session, AdminFlowStage.PROCESSING.name());
            assistantAdminManagementService.finalizeAssistantAdminAdd(admin, session);
        } else if (AdminFlowStage.CONFIRMING_ACC_ADD.name().equals(currentState)) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "⏳ Processing accounts member addition... please wait.");
            sessionManager.updateState(session, AdminFlowStage.PROCESSING.name());
            accountsManagementService.finalizeAccountsMemberAdd(admin, session);
        }
    }

    private void handleLocationMessage(TeamMember admin, BotSession session, WhatsAppWebhookDto.Location location) {
        customerManagementService.handleLocationMessage(admin, session, location);
    }

    private boolean isAbortCommand(String text) {
        return text.trim().equalsIgnoreCase(FlowConstants.CMD_ABORT);
    }

    private void handleAbortCommand(TeamMember admin, BotSession session) {
        session.setSessionData("{}"); // Clear data
        sessionManager.updateState(session, AdminFlowStage.IDLE.name());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "❌ *Operation Cancelled*\n\nReturning to main menu...");
        showMainMenu(admin, session);
    }
}
