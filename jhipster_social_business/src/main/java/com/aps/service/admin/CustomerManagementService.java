package com.aps.service.admin;

import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AdminFlowStage;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.CustomerRepository;
import com.aps.service.BotSessionManager;
import com.aps.service.LocationValidationService;
import com.aps.service.UserRemovalService; // Added
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import com.aps.service.util.InputValidator;
import java.time.Instant;
import java.util.List;
import java.util.Optional; // Added
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class CustomerManagementService {

    private final Logger log = LoggerFactory.getLogger(CustomerManagementService.class);

    private final CustomerRepository customerRepository;
    private final WhatsAppService whatsAppService;
    private final LocationValidationService locationValidationService;
    private final BotSessionManager sessionManager;
    private final InputValidator inputValidator;
    private final UserRemovalService userRemovalService; // Added

    public CustomerManagementService(
        CustomerRepository customerRepository,
        WhatsAppService whatsAppService,
        LocationValidationService locationValidationService,
        BotSessionManager sessionManager,
        InputValidator inputValidator,
        UserRemovalService userRemovalService
    ) { // Added
        this.customerRepository = customerRepository;
        this.whatsAppService = whatsAppService;
        this.locationValidationService = locationValidationService;
        this.sessionManager = sessionManager;
        this.inputValidator = inputValidator;
        this.userRemovalService = userRemovalService;
    }

    public void showCustomerMenu(TeamMember admin) {
        // Use List Message instead of Buttons because we have > 3 options
        List<WhatsAppMessageDto.RowDto> rows = List.of(
            WhatsAppMessageDto.RowDto.builder().id("ADD_CUSTOMER").title("➕ Add Customer").description("Register a new customer").build(),
            WhatsAppMessageDto.RowDto.builder()
                .id("SHOW_ALL_CUSTOMERS")
                .title("📋 Show All")
                .description("List all registered customers")
                .build(),
            WhatsAppMessageDto.RowDto.builder()
                .id("DELETE_CUSTOMER_MENU")
                .title("🗑️ Delete Customer")
                .description("Remove a customer")
                .build(),
            WhatsAppMessageDto.RowDto.builder().id("BACK_TO_MAIN").title("⬅️ Back").description("Return to main menu").build()
        );

        whatsAppService.sendInteractiveList(admin.getWaPhoneNumber(), "👥 *Customer Management*\n\nSelect an option:", rows);

        BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());
        sessionManager.updateState(session, AdminFlowStage.CUSTOMER_MENU.name());
    }

    public void startAddCustomer(TeamMember admin, BotSession session) {
        sessionManager.setSessionData(session, "tempEntityType", "CUSTOMER");
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "➕ *Add New Customer*\n\n📝 Please provide the customer's name:");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_CUST_NAME.name());
    }

    public void handleCustomerNameInput(TeamMember admin, BotSession session, String name) {
        if (!inputValidator.isValidName(name)) {
            whatsAppService.sendSimpleText(
                admin.getWaPhoneNumber(),
                "❌ Invalid name. Use letters/spaces/dots only (min 2 chars). Try again:"
            );
            return;
        }
        sessionManager.setSessionData(session, "tempCustomerName", name.trim());
        whatsAppService.sendSimpleText(
            admin.getWaPhoneNumber(),
            "✅ Name: " + name.trim() + "\n\n📞 Please provide the customer's phone number:"
        );
        sessionManager.updateState(session, AdminFlowStage.AWAITING_CUST_PHONE.name());
    }

    public void handleCustomerPhoneInput(TeamMember admin, BotSession session, String phone) {
        if (!inputValidator.isValidPhoneNumber(phone)) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid phone number format. Try again:");
            return;
        }
        sessionManager.setSessionData(session, "tempCustomerPhone", phone.trim());
        whatsAppService.sendSimpleText(
            admin.getWaPhoneNumber(),
            "✅ Phone: " + phone.trim() + "\n\n📱 Please provide the customer's WhatsApp number (with country code, e.g., 919876543210):"
        );
        sessionManager.updateState(session, AdminFlowStage.AWAITING_CUST_WAPHONE.name());
    }

    public void handleCustomerWaPhoneInput(TeamMember admin, BotSession session, String waPhone) {
        if (!inputValidator.isValidPhoneNumber(waPhone)) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid WhatsApp number format. Try again:");
            return;
        }
        sessionManager.setSessionData(session, "tempCustomerWaPhone", waPhone.trim());
        whatsAppService.sendSimpleText(
            admin.getWaPhoneNumber(),
            "✅ WhatsApp: " +
            waPhone.trim() +
            "\n\n📍 Please share the customer's location.\n\n" +
            "📢 *How to share:*\n" +
            "• Tap the attachment icon (📎)\n" +
            "• Select 'Location'\n" +
            "• Send location"
        );
        sessionManager.updateState(session, AdminFlowStage.AWAITING_CUST_LOCATION.name());
    }

    public void handleLocationMessage(TeamMember admin, BotSession session, WhatsAppWebhookDto.Location location) {
        String currentStage = session.getCurrentState();
        if (!AdminFlowStage.AWAITING_CUST_LOCATION.name().equals(currentStage)) {
            whatsAppService.sendSimpleText(
                admin.getWaPhoneNumber(),
                "⚠️ *Unexpected Location Received*\n\n" +
                "I'm not expecting a location right now. Please select an option from the menu or start a new command."
            );
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
            tempName,
            tempPhone,
            tempWaPhone,
            customerLat,
            customerLon,
            distance
        );

        sessionManager.setSessionData(session, "tempLat", String.valueOf(customerLat));
        sessionManager.setSessionData(session, "tempLon", String.valueOf(customerLon));

        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
            WhatsAppMessageDto.ButtonDto.builder()
                .type("reply")
                .reply(WhatsAppMessageDto.ReplyDto.builder().id("CONFIRM_ADD").title("✅ Confirm & Add").build())
                .build(),
            WhatsAppMessageDto.ButtonDto.builder()
                .type("reply")
                .reply(WhatsAppMessageDto.ReplyDto.builder().id("CANCEL_OPERATION").title("❌ Cancel").build())
                .build()
        );

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
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error: Missing location data.");
                return;
            }

            double lat = Double.parseDouble(latStr);
            double lon = Double.parseDouble(lonStr);
            double distance = locationValidationService.getDistanceFromBusiness(lat, lon);

            String tempName = sessionManager.getSessionDataString(session, "tempCustomerName");
            String tempPhone = sessionManager.getSessionDataString(session, "tempCustomerPhone");
            String tempWaPhone = sessionManager.getSessionDataString(session, "tempCustomerWaPhone");

            // 1. Check for duplicates
            if (customerRepository.existsByWaPhoneNumber(tempWaPhone)) {
                whatsAppService.sendSimpleText(
                    admin.getWaPhoneNumber(),
                    "❌ *Failed:* A customer with WhatsApp number " + tempWaPhone + " already exists."
                );
                return;
            }

            Customer newCustomer = new Customer();
            newCustomer.setName(tempName);
            newCustomer.setPhoneNumber(tempPhone);
            newCustomer.setWaPhoneNumber(tempWaPhone);
            newCustomer.setLocationLat(lat);
            newCustomer.setLocationLon(lon);
            newCustomer.setDistanceFromBusinessKm(distance);
            newCustomer.setRole(UserRole.CUSTOMER);
            newCustomer.setJoinedAt(Instant.now());

            boolean isWithinRadius = locationValidationService.isWithinDeliveryRadius(lat, lon);
            newCustomer.setIsPincodeValid(isWithinRadius);
            newCustomer.setAddedBy(admin);

            // 2. Save AND Flush to force DB constraint check immediately
            newCustomer = customerRepository.saveAndFlush(newCustomer);
            log.info(
                "DEBUG: Customer Saved: ID={}, waPhone={}, Name={}",
                newCustomer.getId(),
                newCustomer.getWaPhoneNumber(),
                newCustomer.getName()
            );

            Customer finalNewCustomer = newCustomer;

            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        whatsAppService.sendSimpleText(
                            admin.getWaPhoneNumber(),
                            "✅ *Customer Added Successfully!*\n\n" +
                            "👤 " +
                            finalNewCustomer.getName() +
                            " has been added to the system.\n\n" +
                            "The customer can now start ordering by sending 'Hi' to the business number."
                        );

                        String customerWaPhone = finalNewCustomer.getWaPhoneNumber();
                        if (customerWaPhone != null && !customerWaPhone.isEmpty()) {
                            whatsAppService.sendSimpleText(
                                customerWaPhone,
                                "🎉 *കുടുംബത്തിലേക്ക് സ്വാഗതം!*\n\n" +
                                "👋 ഹായ് " +
                                finalNewCustomer.getName() +
                                ",\n" +
                                "നിങ്ങളെ ഒരു ഉപഭോക്താവായി വിജയകരമായി രജിസ്റ്റർ ചെയ്തിരിക്കുന്നു.\n\n" +
                                "🛍️ *ഓർഡർ ചെയ്യാൻ തുടങ്ങാം:*\n" +
                                "ഞങ്ങളുടെ ഉൽപ്പന്നങ്ങൾ കാണാനും ഓർഡർ ചെയ്യാനും *'Hi'* എന്ന് റിപ്ലൈ ചെയ്യുക.\n\n" +
                                "ഞങ്ങളെ തിരഞ്ഞെടുത്തതിന് നന്ദി!"
                            );
                        }
                        showCustomerMenu(admin);
                    }
                }
            );
        } catch (Exception e) {
            e.printStackTrace();
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error adding customer: " + e.getMessage());
            showCustomerMenu(admin);
        }
    }

    public void showAllCustomers(TeamMember admin) {
        List<Customer> customers = customerRepository.findAll();

        if (customers.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "📋 *No customers found.*\n\nAdd your first customer to get started!");
            showCustomerMenu(admin);
            return;
        }

        StringBuilder message = new StringBuilder(String.format("📋 *All Customers* (Total: %d)\n\n", customers.size()));
        int count = 1;
        for (Customer c : customers) {
            message.append(
                String.format(
                    "%d. *%s* (ID: %d)\n   📞 %s\n   📱 %s\n   📏 %.2f km\n\n",
                    count++,
                    c.getName(),
                    c.getId(),
                    c.getPhoneNumber(),
                    c.getWaPhoneNumber(),
                    c.getDistanceFromBusinessKm() != null ? c.getDistanceFromBusinessKm() : 0.0
                )
            );
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        showCustomerMenu(admin);
    }

    public void showMyCustomers(TeamMember admin) {
        List<Customer> customers = customerRepository.findByAddedBy(admin);

        if (customers.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "📋 *No customers found.*\n\nYou haven't added any customers yet.");
            showCustomerMenu(admin);
            return;
        }

        StringBuilder message = new StringBuilder(String.format("📋 *My Customers* (Total: %d)\n\n", customers.size()));
        int count = 1;
        for (Customer c : customers) {
            message.append(
                String.format(
                    "%d. *%s* (ID: %d)\n   📞 %s\n   📱 %s\n   📏 %.2f km\n\n",
                    count++,
                    c.getName(),
                    c.getId(),
                    c.getPhoneNumber(),
                    c.getWaPhoneNumber(),
                    c.getDistanceFromBusinessKm() != null ? c.getDistanceFromBusinessKm() : 0.0
                )
            );
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
    }

    public void startUpdateCustomer(TeamMember admin, BotSession session) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "🚧 *Update Customer*\n\nThis feature is coming soon!");
        showCustomerMenu(admin);
    }

    // --- Deletion Logic ---

    public void startDeleteCustomer(TeamMember admin, BotSession session) {
        whatsAppService.sendSimpleText(
            admin.getWaPhoneNumber(),
            "🗑️ *Delete Customer*\n\n⚠️ PLEASE READ CAREFULLY:\n" +
            "This will *Archive & Remove* the customer from active lists.\n" +
            "Order history (Placed/Spent) will be preserved in a summary.\n\n" +
            "🆔 Please enter the **Customer ID** you wish to delete:"
        );

        sessionManager.updateState(session, AdminFlowStage.AWAITING_DELETE_CUST_ID.name());
    }

    public void handleDeleteCustomerInput(TeamMember admin, BotSession session, String text) {
        try {
            Long customerId = Long.parseLong(text.trim());
            Optional<Customer> customerOpt = customerRepository.findById(customerId);

            if (customerOpt.isEmpty()) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Customer ID not found. Please try again:");
                return;
            }

            Customer customer = customerOpt.get();
            sessionManager.setSessionData(session, "tempDeleteTitle", customer.getName());
            sessionManager.setSessionData(session, "tempDeleteId", customerId.toString());

            List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                    .type("reply")
                    .reply(WhatsAppMessageDto.ReplyDto.builder().id("CONFIRM_DELETE_" + customerId).title("💥 Yes, DELETE").build())
                    .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                    .type("reply")
                    .reply(WhatsAppMessageDto.ReplyDto.builder().id("CANCEL_OPERATION").title("❌ Cancel").build())
                    .build()
            );

            whatsAppService.sendCartActionButtons(
                admin.getWaPhoneNumber(),
                String.format(
                    "⚠️ *Confirm Deletion*\n\n" +
                    "Are you SURE you want to delete:\n" +
                    "👤 *%s* (ID: %d)?\n\n" +
                    "This action cannot be easily undone via WhatsApp.",
                    customer.getName(),
                    customerId
                ),
                buttons
            );

            // Using Generic PROCESSING or just wait for Button Reply which routes via ID
            // Actually, we don't need a specific CONFIRMING state if button ID carries
            // payload,
            // but AdminFlowService handles button clicks.
            // We need to handle "CONFIRM_DELETE_..." in AdminFlowService?
            // Wait, AdminFlowService.handleButtonReply has a switch case.
            // We should add a case there or use a generic Confirm handler.
            // Let's settle on using ID parsing in AdminFlowService or keep state.
            // AdminFlowService uses `handleConfirmAdd` which checks state.
            // Let's follow that pattern.

            sessionManager.updateState(session, "CONFIRMING_CUST_DELETE"); // Need to add to Enum or use
            // generic?
            // To avoid modifying Enum again just for this, I'll rely on the Button ID
            // carrying logic
            // OR reuse PROCESSING state conceptually? No.
            // I'll add the logic to AdminFlowService.handleButtonReply to catch
            // startsWith("CONFIRM_DELETE_")

        } catch (NumberFormatException e) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid ID format. Please enter a number:");
        }
    }

    public void finalizeCustomerDelete(TeamMember admin, Long customerId) {
        try {
            userRemovalService.removeCustomer(customerId, "Admin " + admin.getName() + " requested via WhatsApp");
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "✅ Customer Deleted Successfully.");
        } catch (Exception e) {
            log.error("Delete failed", e);
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Delete Failed: " + e.getMessage());
        }
        showCustomerMenu(admin);
    }
}
