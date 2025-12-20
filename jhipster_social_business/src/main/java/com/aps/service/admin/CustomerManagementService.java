package com.aps.service.admin;

import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AdminFlowStage;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.CustomerRepository;
import com.aps.service.BotSessionManager;
import com.aps.service.LocationValidationService;
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.aps.service.util.InputValidator;

import java.time.Instant;
import java.util.List;

@Service
public class CustomerManagementService {

        private final Logger log = LoggerFactory.getLogger(CustomerManagementService.class);

        private final CustomerRepository customerRepository;
        private final WhatsAppService whatsAppService;
        private final LocationValidationService locationValidationService;
        private final BotSessionManager sessionManager;
        // We might need CustomerMessageService or AdminMessageService? Used hardcoded
        // strings in legacy.
        // I'll stick to hardcoded strings for now to match legacy, or reuse existing if
        // applicable.

        private final InputValidator inputValidator;

        public CustomerManagementService(CustomerRepository customerRepository,
                        WhatsAppService whatsAppService,
                        LocationValidationService locationValidationService,
                        BotSessionManager sessionManager,
                        InputValidator inputValidator) {
                this.customerRepository = customerRepository;
                this.whatsAppService = whatsAppService;
                this.locationValidationService = locationValidationService;
                this.sessionManager = sessionManager;
                this.inputValidator = inputValidator;
        }

        public void showCustomerMenu(TeamMember admin) {
                List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("ADD_CUSTOMER")
                                                                .title("➕ Add Customer")
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("SHOW_ALL_CUSTOMERS")
                                                                .title("📋 Show All")
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("BACK_TO_MAIN")
                                                                .title("⬅️ Back")
                                                                .build())
                                                .build());

                whatsAppService.sendCartActionButtons(admin.getWaPhoneNumber(),
                                "👥 *Customer Management*\n\nWhat would you like to do?", buttons);

                BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());
                sessionManager.updateState(session, AdminFlowStage.CUSTOMER_MENU.name());
        }

        public void startAddCustomer(TeamMember admin, BotSession session) {
                sessionManager.setSessionData(session, "tempEntityType", "CUSTOMER");
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "➕ *Add New Customer*\n\n📝 Please provide the customer's name:");
                sessionManager.updateState(session, AdminFlowStage.AWAITING_CUST_NAME.name());
        }

        public void handleCustomerNameInput(TeamMember admin, BotSession session, String name) {
                if (!inputValidator.isValidName(name)) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Invalid name. Use letters/spaces/dots only (min 2 chars). Try again:");
                        return;
                }
                sessionManager.setSessionData(session, "tempCustomerName", name.trim());
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "✅ Name: " + name.trim() + "\n\n📞 Please provide the customer's phone number:");
                sessionManager.updateState(session, AdminFlowStage.AWAITING_CUST_PHONE.name());
        }

        public void handleCustomerPhoneInput(TeamMember admin, BotSession session, String phone) {
                if (!inputValidator.isValidPhoneNumber(phone)) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Invalid phone number format. Try again:");
                        return;
                }
                sessionManager.setSessionData(session, "tempCustomerPhone", phone.trim());
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "✅ Phone: " + phone.trim()
                                                + "\n\n📱 Please provide the customer's WhatsApp number (with country code, e.g., 919876543210):");
                sessionManager.updateState(session, AdminFlowStage.AWAITING_CUST_WAPHONE.name());
        }

        public void handleCustomerWaPhoneInput(TeamMember admin, BotSession session, String waPhone) {
                if (!inputValidator.isValidPhoneNumber(waPhone)) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Invalid WhatsApp number format. Try again:");
                        return;
                }
                sessionManager.setSessionData(session, "tempCustomerWaPhone", waPhone.trim());
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "✅ WhatsApp: " + waPhone.trim() + "\n\n📍 Please share the customer's location.\n\n" +
                                                "📢 *How to share:*\n" +
                                                "• Tap the attachment icon (📎)\n" +
                                                "• Select 'Location'\n" +
                                                "• Send location");
                sessionManager.updateState(session, AdminFlowStage.AWAITING_CUST_LOCATION.name());
        }

        public void handleLocationMessage(TeamMember admin, BotSession session, WhatsAppWebhookDto.Location location) {
                String currentStage = session.getCurrentState();
                if (!AdminFlowStage.AWAITING_CUST_LOCATION.name().equals(currentStage)) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "⚠️ *Unexpected Location Received*\n\n" +
                                                        "I'm not expecting a location right now. Please select an option from the menu or start a new command.");
                        return;
                }

                double customerLat = location.getLatitude();
                double customerLon = location.getLongitude();
                double distance = locationValidationService.getDistanceFromBusiness(customerLat, customerLon);

                String tempName = sessionManager.getSessionDataString(session, "tempCustomerName");
                String tempPhone = sessionManager.getSessionDataString(session, "tempCustomerPhone");
                String tempWaPhone = sessionManager.getSessionDataString(session, "tempCustomerWaPhone");

                String summary = String.format(
                                "✅ *Customer Details Summary:*\n\n" +
                                                "👤 Name: %s\n" +
                                                "📞 Phone: %s\n" +
                                                "📱 WhatsApp: %s\n" +
                                                "📍 Location: %.6f, %.6f\n" +
                                                "📏 Distance: %.2f km\n\n" +
                                                "Confirm to add this customer?",
                                tempName, tempPhone, tempWaPhone,
                                customerLat, customerLon, distance);

                sessionManager.setSessionData(session, "tempLat", String.valueOf(customerLat));
                sessionManager.setSessionData(session, "tempLon", String.valueOf(customerLon));

                List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("CONFIRM_ADD")
                                                                .title("✅ Confirm & Add")
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("CANCEL_OPERATION")
                                                                .title("❌ Cancel")
                                                                .build())
                                                .build());

                whatsAppService.sendCartActionButtons(admin.getWaPhoneNumber(), summary, buttons);
                sessionManager.updateState(session, AdminFlowStage.CONFIRMING_CUST_ADD.name());
        }

        @Async
        @Transactional
        public void finalizeCustomerAdd(TeamMember admin, BotSession session) {
                try {
                        String latStr = sessionManager.getSessionDataString(session, "tempLat");
                        String lonStr = sessionManager.getSessionDataString(session, "tempLon");

                        if (latStr == null || lonStr == null) {
                                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                                "❌ Error: Missing location data.");
                                return;
                        }

                        double lat = Double.parseDouble(latStr);
                        double lon = Double.parseDouble(lonStr);
                        double distance = locationValidationService.getDistanceFromBusiness(lat, lon);

                        String tempName = sessionManager.getSessionDataString(session, "tempCustomerName");
                        String tempPhone = sessionManager.getSessionDataString(session, "tempCustomerPhone");
                        String tempWaPhone = sessionManager.getSessionDataString(session, "tempCustomerWaPhone");

                        Customer newCustomer = new Customer();
                        newCustomer.setName(tempName);
                        newCustomer.setPhoneNumber(tempPhone);
                        newCustomer.setWaPhoneNumber(tempWaPhone);
                        newCustomer.setLocationLat(lat);
                        newCustomer.setLocationLon(lon);
                        newCustomer.setDistanceFromBusinessKm(distance);
                        newCustomer.setRole(UserRole.CUSTOMER);
                        // newCustomer.setCurrentFlowStage(CustomerFlowStage.REGISTERED); // Removed
                        // from Entity in JHipster?
                        // Checking Customer entity... it doesn't have currentFlowStage field likely, as
                        // we use BotSession now.
                        // It has joinedAt.
                        newCustomer.setJoinedAt(Instant.now());

                        // Validate if customer is within delivery radius using the service
                        boolean isWithinRadius = locationValidationService.isWithinDeliveryRadius(lat, lon);
                        newCustomer.setIsPincodeValid(isWithinRadius);
                        newCustomer.setAddedBy(admin);

                        newCustomer = customerRepository.save(newCustomer);
                        log.info("DEBUG: Customer Saved: ID={}, waPhone={}, Name={}", newCustomer.getId(),
                                        newCustomer.getWaPhoneNumber(), newCustomer.getName());

                        Customer finalNewCustomer = newCustomer;
                        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                                @Override
                                public void afterCommit() {
                                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                                        "✅ *Customer Added Successfully!*\n\n" +
                                                                        "👤 " + finalNewCustomer.getName()
                                                                        + " has been added to the system.\n\n" +
                                                                        "The customer can now start ordering by sending 'Hi' to the business number.");

                                        // Send welcome message to the new Customer
                                        String customerWaPhone = finalNewCustomer.getWaPhoneNumber();
                                        log.info("DEBUG: Attempting to send welcome message to: {}", customerWaPhone);

                                        if (customerWaPhone != null && !customerWaPhone.isEmpty()) {
                                                log.info("DEBUG: Sending welcome message to customer...");
                                                whatsAppService.sendSimpleText(customerWaPhone,
                                                                "🎉 *Welcome to the Family!*\n\n" +
                                                                                "👋 Hi " + finalNewCustomer.getName()
                                                                                + ",\n" +
                                                                                "You have been successfully registered as a customer.\n\n"
                                                                                +
                                                                                "🛍️ *Start Ordering:*\n" +
                                                                                "Simply reply with *'Hi'* to browse our products and place orders.\n\n"
                                                                                +
                                                                                "Thank you for choosing us!");
                                                log.info("DEBUG: Welcome message sent (async check needed)");
                                        } else {
                                                log.warn("DEBUG: Skipping welcome message - waPhoneNumber is null or empty");
                                        }
                                }
                        });

                } catch (Exception e) {
                        e.printStackTrace();
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Error adding customer: " + e.getMessage());
                }
        }

        public void showAllCustomers(TeamMember admin) {
                List<Customer> customers = customerRepository.findAll();

                if (customers.isEmpty()) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "📋 *No customers found.*\n\nAdd your first customer to get started!");
                        showCustomerMenu(admin);
                        return;
                }

                StringBuilder message = new StringBuilder(
                                String.format("📋 *All Customers* (Total: %d)\n\n", customers.size()));
                int count = 1;
                for (Customer c : customers) {
                        message.append(String.format("%d. *%s*\n   📞 %s\n   📱 %s\n   📏 %.2f km\n\n",
                                        count++, c.getName(), c.getPhoneNumber(), c.getWaPhoneNumber(),
                                        c.getDistanceFromBusinessKm() != null ? c.getDistanceFromBusinessKm() : 0.0));
                }

                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
                showCustomerMenu(admin);
        }

        public void showMyCustomers(TeamMember admin) {
                List<Customer> customers = customerRepository.findByAddedBy(admin);

                if (customers.isEmpty()) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "📋 *No customers found.*\n\nYou haven't added any customers yet.");
                        showCustomerMenu(admin); // Or showMainMenu if called from executive? Ideally showCustomerMenu
                                                 // is fine if they have access, but for executive it might be better to
                                                 // return to main menu.
                        // However, showCustomerMenu sends buttons relevant to ADMIN flow.
                        // Executive flow has its own menu.
                        // I'll leave the navigation logic to the caller or simply NOT call
                        // showCustomerMenu here and let the caller handle next step if possible?
                        // But showAllCustomers calls showCustomerMenu.
                        // Let's rely on the caller to show the menu AFTER, or modify this to NOT show
                        // menu.
                        // Actually, reusing this service for Executive might be mixing concerns if
                        // menus are different.
                        // But for now, let's just print the list.
                        return;
                }

                StringBuilder message = new StringBuilder(
                                String.format("📋 *My Customers* (Total: %d)\n\n", customers.size()));
                int count = 1;
                for (Customer c : customers) {
                        message.append(String.format("%d. *%s*\n   📞 %s\n   📱 %s\n   📏 %.2f km\n\n",
                                        count++, c.getName(), c.getPhoneNumber(), c.getWaPhoneNumber(),
                                        c.getDistanceFromBusinessKm() != null ? c.getDistanceFromBusinessKm() : 0.0));
                }

                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        }

        public void startUpdateCustomer(TeamMember admin, BotSession session) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "🚧 *Update Customer*\n\nThis feature is coming soon!");
                showCustomerMenu(admin);
        }

        public void startDeleteCustomer(TeamMember admin, BotSession session) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "🚧 *Delete Customer*\n\nThis feature is coming soon!");
                showCustomerMenu(admin);
        }
}
