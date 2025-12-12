package com.seller.whatsappservice.service;

import com.seller.whatsappservice.dto.WhatsAppMessageDto;
import com.seller.whatsappservice.dto.WhatsAppWebhookDto;
import com.seller.whatsappservice.model.TeamMember;
import com.seller.whatsappservice.model.enums.AdminFlowStage;
import com.seller.whatsappservice.repository.TeamMemberRepository;
import com.seller.whatsappservice.service.admin.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Main Admin Flow Service - coordinates between specialized management services
 * Delegates operations to:
 * - CustomerManagementService
 * - DeliveryPersonManagementService
 * - ExecutiveManagementService
 * - AssistantAdminManagementService
 * - ProductManagementService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminFlowService {

        private final TeamMemberRepository teamMemberRepository;
        private final WhatsAppService whatsAppService;

        // Specialized management services
        private final CustomerManagementService customerManagementService;
        private final DeliveryPersonManagementService deliveryPersonManagementService;
        private final ExecutiveManagementService executiveManagementService;
        private final AssistantAdminManagementService assistantAdminManagementService;
        private final ProductManagementService productManagementService;

        /**
         * Process incoming message from an admin or developer
         */
        @Transactional
        public void handleAdminMessage(TeamMember admin, WhatsAppWebhookDto.Message message) {
                log.info("Processing admin message from: {} ({})", admin.getName(), admin.getRole());

                // Check for ABORT command at any stage
                if (message.getType().equals("text") && message.getText() != null) {
                        String text = message.getText().getBody().trim();
                        if (isAbortCommand(text)) {
                                handleAbortCommand(admin);
                                teamMemberRepository.save(admin);
                                return;
                        }
                }

                // Handle different message types
                if (message.getType().equals("text") && message.getText() != null) {
                        handleTextMessage(admin, message.getText().getBody());
                } else if (message.getType().equals("location") && message.getLocation() != null) {
                        handleLocationMessage(admin, message.getLocation());
                } else if (message.getType().equals("interactive")) {
                        if (message.getInteractive().getType().equals("button_reply")) {
                                handleButtonReply(admin, message.getInteractive().getButtonReply());
                        }
                } else if (message.getType().equals("image") && message.getImage() != null) {
                        if (admin.getCurrentAdminFlowStage() == AdminFlowStage.AWAITING_PRODUCT_IMAGES) {
                                productManagementService.handleProductImageMessage(admin, message);
                        }
                }

                teamMemberRepository.save(admin);
        }

        /**
         * Handle text messages based on admin flow stage
         */
        private void handleTextMessage(TeamMember admin, String text) {
                AdminFlowStage stage = admin.getCurrentAdminFlowStage();

                if (stage == null) {
                        stage = AdminFlowStage.IDLE;
                }

                log.info("Admin {} in stage {} sent: {}", admin.getName(), stage, text);

                switch (stage) {
                        case IDLE:
                                handleIdleState(admin, text);
                                break;

                        // Customer Management
                        case AWAITING_CUST_NAME:
                                customerManagementService.handleCustomerNameInput(admin, text);
                                break;
                        case AWAITING_CUST_PHONE:
                                customerManagementService.handleCustomerPhoneInput(admin, text);
                                break;
                        case AWAITING_CUST_WAPHONE:
                                customerManagementService.handleCustomerWaPhoneInput(admin, text);
                                break;

                        // Delivery Person Management
                        case AWAITING_DELIVERY_NAME:
                                deliveryPersonManagementService.handleDeliveryPersonNameInput(admin, text);
                                break;
                        case AWAITING_DELIVERY_PHONE:
                                deliveryPersonManagementService.handleDeliveryPersonPhoneInput(admin, text);
                                break;
                        case AWAITING_DELIVERY_WAPHONE:
                                deliveryPersonManagementService.handleDeliveryPersonWaPhoneInput(admin, text);
                                break;
                        case AWAITING_DELIVERY_STATUS:
                                deliveryPersonManagementService.handleDeliveryPersonStatusInput(admin, text);
                                break;

                        // Executive Management
                        case AWAITING_EXEC_NAME:
                                executiveManagementService.handleExecutiveNameInput(admin, text);
                                break;
                        case AWAITING_EXEC_PHONE:
                                executiveManagementService.handleExecutivePhoneInput(admin, text);
                                break;
                        case AWAITING_EXEC_WAPHONE:
                                executiveManagementService.handleExecutiveWaPhoneInput(admin, text);
                                break;
                        case AWAITING_EXEC_STATUS:
                                executiveManagementService.handleExecutiveStatusInput(admin, text);
                                break;

                        // Assistant Admin Management
                        case AWAITING_ASSISTANT_NAME:
                                assistantAdminManagementService.handleAssistantAdminNameInput(admin, text);
                                break;
                        case AWAITING_ASSISTANT_PHONE:
                                assistantAdminManagementService.handleAssistantAdminPhoneInput(admin, text);
                                break;
                        case AWAITING_ASSISTANT_WAPHONE:
                                assistantAdminManagementService.handleAssistantAdminWaPhoneInput(admin, text);
                                break;
                        case AWAITING_ASSISTANT_STATUS:
                                assistantAdminManagementService.handleAssistantAdminStatusInput(admin, text);
                                break;

                        // Product Management
                        case AWAITING_PRODUCT_NAME:
                                productManagementService.handleProductNameInput(admin, text);
                                break;
                        case AWAITING_PRODUCT_PRICE:
                                productManagementService.handleProductPriceInput(admin, text);
                                break;
                        case AWAITING_PRODUCT_DESCRIPTION:
                                productManagementService.handleProductDescriptionInput(admin, text);
                                break;
                        case AWAITING_PRODUCT_AVAILABILITY:
                                productManagementService.handleProductAvailabilityInput(admin, text);
                                break;
                        case AWAITING_PRODUCT_IMAGES:
                                if (text.equalsIgnoreCase("DONE") || text.equalsIgnoreCase("SKIP")) {
                                        productManagementService.finalizeProductAdd(admin);
                                }
                                break;

                        default:
                                showMainMenu(admin);
                }
        }

        /**
         * Handle idle state - show main menu on "hi" or "hello"
         */
        private void handleIdleState(TeamMember admin, String text) {
                if (text.trim().equalsIgnoreCase("hi") || text.trim().equalsIgnoreCase("hello")) {
                        showMainMenu(admin);
                } else {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "Send 'hi' to see the admin menu.");
                }
        }

        /**
         * Show main menu to admin with personalized greeting
         */
        private void showMainMenu(TeamMember admin) {
                // First set of buttons (max 3)
                List<WhatsAppMessageDto.ButtonDto> buttons1 = List.of(
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("CUSTOMER_SECTION")
                                                                .title("👥 Customers")
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("DELIVERY_SECTION")
                                                                .title("🚚 Delivery")
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("PRODUCT_SECTION")
                                                                .title("🐟 Products")
                                                                .build())
                                                .build());

                String greeting = String.format(
                                "🎉 *Welcome, %s!* 👑\n\n" +
                                                "You are logged in as: *%s*\n\n" +
                                                "Please select a section to manage:\n\n" +
                                                "💡 *Tip:* Send 'ABORT' anytime to return to this menu.",
                                admin.getName(),
                                admin.getRole());

                whatsAppService.sendCartActionButtons(admin.getWaPhoneNumber(), greeting, buttons1);

                // Send second message with remaining options
                List<WhatsAppMessageDto.ButtonDto> buttons2 = List.of(
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("EXECUTIVE_SECTION")
                                                                .title("💼 Executives")
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("ASSISTANT_SECTION")
                                                                .title("🛡️ Assistants")
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("CONTACT_DEVELOPER")
                                                                .title("👨‍💻 Contact Dev")
                                                                .build())
                                                .build());

                whatsAppService.sendCartActionButtons(admin.getWaPhoneNumber(),
                                "*More Options:*", buttons2);

                admin.setCurrentAdminFlowStage(AdminFlowStage.IDLE);
        }

        /**
         * Handle button replies from admin
         */
        private void handleButtonReply(TeamMember admin, WhatsAppWebhookDto.ButtonReply buttonReply) {
                String buttonId = buttonReply.getId();
                log.info("Admin button reply: {}", buttonId);

                switch (buttonId) {
                        // Main menu options
                        case "CUSTOMER_SECTION":
                                customerManagementService.showCustomerMenu(admin);
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
                        case "PRODUCT_SECTION":
                                productManagementService.showProductMenu(admin);
                                break;
                        case "CONTACT_DEVELOPER":
                                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                                "📞 *Contact Developer*\n\nFor technical support, please contact:\n[Developer Contact Info]");
                                break;

                        // Customer menu options
                        case "ADD_CUSTOMER":
                                customerManagementService.startAddCustomer(admin);
                                break;
                        case "UPDATE_CUSTOMER":
                                customerManagementService.startUpdateCustomer(admin);
                                break;
                        case "DELETE_CUSTOMER":
                                customerManagementService.startDeleteCustomer(admin);
                                break;
                        case "SHOW_ALL_CUSTOMERS":
                                customerManagementService.showAllCustomers(admin);
                                break;

                        // Delivery Person menu options
                        case "ADD_DELIVERY":
                                deliveryPersonManagementService.startAddDeliveryPerson(admin);
                                break;
                        case "UPDATE_DELIVERY":
                                deliveryPersonManagementService.startUpdateDeliveryPerson(admin);
                                break;
                        case "DELETE_DELIVERY":
                                deliveryPersonManagementService.startDeleteDeliveryPerson(admin);
                                break;
                        case "SHOW_ALL_DELIVERY":
                                deliveryPersonManagementService.showAllDeliveryPersons(admin);
                                break;

                        // Executive menu options
                        case "ADD_EXECUTIVE":
                                executiveManagementService.startAddExecutive(admin);
                                break;
                        case "UPDATE_EXECUTIVE":
                                executiveManagementService.startUpdateExecutive(admin);
                                break;
                        case "DELETE_EXECUTIVE":
                                executiveManagementService.startDeleteExecutive(admin);
                                break;
                        case "SHOW_ALL_EXECUTIVE":
                                executiveManagementService.showAllExecutives(admin);
                                break;

                        // Assistant Admin menu options
                        case "ADD_ASSISTANT":
                                assistantAdminManagementService.startAddAssistantAdmin(admin);
                                break;
                        case "UPDATE_ASSISTANT":
                                assistantAdminManagementService.startUpdateAssistantAdmin(admin);
                                break;
                        case "DELETE_ASSISTANT":
                                assistantAdminManagementService.startDeleteAssistantAdmin(admin);
                                break;
                        case "SHOW_ALL_ASSISTANT":
                                assistantAdminManagementService.showAllAssistantAdmins(admin);
                                break;

                        // Product menu options
                        case "ADD_PRODUCT":
                                productManagementService.startAddProduct(admin);
                                break;
                        case "SHOW_ALL_PRODUCTS":
                                productManagementService.showAllProducts(admin);
                                break;

                        // Confirmation buttons
                        case "CONFIRM_ADD":
                                handleConfirmAdd(admin);
                                break;
                        case "EDIT_DETAILS":
                                handleEditDetails(admin);
                                break;
                        case "CANCEL_OPERATION":
                                handleAbortCommand(admin);
                                break;

                        // Back to main menu
                        case "BACK_TO_MAIN":
                                showMainMenu(admin);
                                break;

                        default:
                                showMainMenu(admin);
                }
        }

        /**
         * Handle location messages (for customer registration)
         */
        private void handleLocationMessage(TeamMember admin, WhatsAppWebhookDto.Location location) {
                customerManagementService.handleLocationMessage(admin, location);
        }

        /**
         * Handle confirm add button - delegates to appropriate service
         */
        private void handleConfirmAdd(TeamMember admin) {
                AdminFlowStage stage = admin.getCurrentAdminFlowStage();

                if (stage == AdminFlowStage.CONFIRMING_CUST_ADD) {
                        customerManagementService.finalizeCustomerAdd(admin);
                } else if (stage == AdminFlowStage.CONFIRMING_DELIVERY_ADD) {
                        deliveryPersonManagementService.finalizeDeliveryPersonAdd(admin);
                } else if (stage == AdminFlowStage.CONFIRMING_EXEC_ADD) {
                        executiveManagementService.finalizeExecutiveAdd(admin);
                } else if (stage == AdminFlowStage.CONFIRMING_ASSISTANT_ADD) {
                        assistantAdminManagementService.finalizeAssistantAdminAdd(admin);
                }

                clearTempFields(admin);
        }

        /**
         * Check if text is an ABORT command
         */
        private boolean isAbortCommand(String text) {
                return text.trim().equalsIgnoreCase("ABORT");
        }

        /**
         * Handle ABORT command - return to main menu
         */
        private void handleAbortCommand(TeamMember admin) {
                clearTempFields(admin);
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "❌ *Operation Cancelled*\n\nReturning to main menu...");
                showMainMenu(admin);
        }

        /**
         * Handle edit details request
         */
        private void handleEditDetails(TeamMember admin) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "✏️ *Edit Details*\n\nThis feature is coming soon!\n\nFor now, please cancel and start over.");
                handleAbortCommand(admin);
        }

        /**
         * Clear all temporary fields
         */
        private void clearTempFields(TeamMember admin) {
                admin.setTempEntityType(null);
                admin.setTempEntityId(null);
                admin.setTempFieldName(null);
                admin.setTempFieldValue(null);
                admin.setTempCustomerName(null);
                admin.setTempCustomerPhone(null);
                admin.setTempCustomerWaPhone(null);
                admin.setTempTeamMemberName(null);
                admin.setTempTeamMemberPhone(null);
                admin.setTempTeamMemberWaPhone(null);
                admin.setTempTeamMemberIsActive(null);
        }
}
