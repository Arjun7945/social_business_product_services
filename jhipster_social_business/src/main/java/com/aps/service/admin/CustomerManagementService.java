package com.aps.service.admin;

import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AdminFlowStage;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.CustomerRepository;
import com.aps.service.BotSessionManager;
import com.aps.service.LocationValidationService;
import com.aps.service.UserRemovalService;
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import com.aps.service.util.InputValidator;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
    private final UserRemovalService userRemovalService;
    private final com.aps.repository.DeliveryZoneRepository deliveryZoneRepository;
    private final com.aps.service.CreditCustomerFlowService creditCustomerFlowService;
    private final com.aps.service.ExecutiveFlowService executiveFlowService;
    private final com.aps.service.GeocodingService geocodingService;

    public CustomerManagementService(
            CustomerRepository customerRepository,
            WhatsAppService whatsAppService,
            LocationValidationService locationValidationService,
            BotSessionManager sessionManager,
            InputValidator inputValidator,
            UserRemovalService userRemovalService,
            com.aps.repository.DeliveryZoneRepository deliveryZoneRepository,
            @org.springframework.context.annotation.Lazy com.aps.service.CreditCustomerFlowService creditCustomerFlowService,
            @org.springframework.context.annotation.Lazy com.aps.service.ExecutiveFlowService executiveFlowService,
            com.aps.service.GeocodingService geocodingService) {
        this.customerRepository = customerRepository;
        this.whatsAppService = whatsAppService;
        this.locationValidationService = locationValidationService;
        this.sessionManager = sessionManager;
        this.inputValidator = inputValidator;
        this.userRemovalService = userRemovalService;
        this.deliveryZoneRepository = deliveryZoneRepository;
        this.creditCustomerFlowService = creditCustomerFlowService;
        this.executiveFlowService = executiveFlowService;
        this.geocodingService = geocodingService;
    }

    public void showCustomerMenu(TeamMember admin) {
        // Use List Message instead of Buttons because we have > 3 options
        List<WhatsAppMessageDto.RowDto> rows = com.aps.service.util.CustomerMenuHelper.getCustomerMenuRows();

        whatsAppService.sendInteractiveList(admin.getWaPhoneNumber(), "👥 *Customer Management*\n\nSelect an option:",
                rows);

        BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());
        sessionManager.updateState(session, AdminFlowStage.CUSTOMER_MENU.name());
    }

    // ... (keeping other methods same until showCustomersByRole)

    public void startAddCustomer(TeamMember admin, BotSession session) {
        sessionManager.setSessionData(session, "tempEntityType", "CUSTOMER");
        sessionManager.setSessionData(session, "targetRole", UserRole.CUSTOMER.name());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "➕ *Add New Customer*\n\n📝 Please provide the customer's name:");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_CUST_NAME.name());
    }

    public void startAddCreditCustomer(TeamMember admin, BotSession session) {
        sessionManager.setSessionData(session, "tempEntityType", "CUSTOMER");
        sessionManager.setSessionData(session, "targetRole", UserRole.CREDIT_CUSTOMER.name());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "➕ *Add New Credit Customer*\n\n📝 Please provide the customer's name:");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_CUST_NAME.name());
    }

    public void handleCustomerNameInput(TeamMember admin, BotSession session, String name) {
        if (!inputValidator.isValidName(name)) {
            whatsAppService.sendSimpleText(
                    admin.getWaPhoneNumber(),
                    "❌ Invalid name. Use letters/spaces/dots only (min 2 chars). Try again:");
            return;
        }
        sessionManager.setSessionData(session, "tempCustomerName", name.trim());
        whatsAppService.sendSimpleText(
                admin.getWaPhoneNumber(),
                "✅ Name: " + name.trim() + "\n\n📞 Please provide the customer's phone number:");
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
                "✅ Phone: " + phone.trim()
                        + "\n\n📱 Please provide the customer's WhatsApp number (with country code, e.g., 919876543210):");
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
                        "• Send location");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_CUST_LOCATION.name());
    }

    public void handleLocationMessage(TeamMember admin, BotSession session, WhatsAppWebhookDto.Location location) {
        String currentStage = session.getCurrentState();
        if (!AdminFlowStage.AWAITING_CUST_LOCATION.name().equals(currentStage)) {
            whatsAppService.sendSimpleText(
                    admin.getWaPhoneNumber(),
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
                tempName,
                tempPhone,
                tempWaPhone,
                customerLat,
                customerLon,
                distance);

        // --- Address Resolution Logic (Refactored) ---
        String resolvedAddress = geocodingService.resolveAddress(customerLat, customerLon, location.getAddress());

        sessionManager.setSessionData(session, "tempAddress", resolvedAddress);

        summary += "\n\n📍 Address: " + resolvedAddress;

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
                        "❌ *Failed:* A customer with WhatsApp number " + tempWaPhone + " already exists.");
                return;
            }

            Customer newCustomer = new Customer();
            newCustomer.setName(tempName);
            newCustomer.setPhoneNumber(tempPhone);
            newCustomer.setWaPhoneNumber(tempWaPhone);
            newCustomer.setLocationLat(lat);
            newCustomer.setLocationLon(lon);
            newCustomer.setLocationLat(lat);
            newCustomer.setLocationLon(lon);
            String tempAddress = sessionManager.getSessionDataString(session, "tempAddress");
            if (tempAddress == null) {
                tempAddress = String.format("Location shared via WhatsApp: %.6f, %.6f", lat, lon);
            }
            newCustomer.setAddress(tempAddress);
            newCustomer.setDistanceFromBusinessKm(distance);
            newCustomer.setRole(UserRole.CUSTOMER);
            String targetRole = sessionManager.getSessionDataString(session, "targetRole");
            if (targetRole != null) {
                try {
                    newCustomer.setRole(UserRole.valueOf(targetRole));
                } catch (IllegalArgumentException e) {
                    // default to CUSTOMER
                }
            }
            newCustomer.setJoinedAt(Instant.now());

            boolean isWithinRadius = locationValidationService.isWithinDeliveryRadius(lat, lon);
            newCustomer.setIsPincodeValid(isWithinRadius);

            // Default Zone assignment if none selected (logic for manual add usually
            // doesn't select zone)
            List<com.aps.domain.DeliveryZone> allZones = deliveryZoneRepository.findAll();
            if (!allZones.isEmpty()) {
                newCustomer.setZone(allZones.get(0));
            }

            newCustomer.setAddedBy(admin);

            // 2. Save AND Flush to force DB constraint check immediately
            newCustomer = customerRepository.saveAndFlush(newCustomer);
            log.info(
                    "DEBUG: Customer Saved: ID={}, waPhone={}, Name={}",
                    newCustomer.getId(),
                    newCustomer.getWaPhoneNumber(),
                    newCustomer.getName());

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
                                            "The customer can now start ordering by sending 'Hi' to the business number.");

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
                                                "ഞങ്ങളുടെ ഉൽപ്പന്നങ്ങൾ കാണാനും ഓർഡർ ചെയ്യാനും *'Hi'* എന്ന് റിപ്ലൈ ചെയ്യുക.\n\n"
                                                +
                                                "ഞങ്ങളെ തിരഞ്ഞെടുത്തതിന് നന്ദി!");
                            }
                            // Redirect to Main Menu or Specific Menu based on role?
                            // For now, standard customer flow goes to Customer Menu.
                            // If it was Credit Customer, we should ideally go there.
                            // But here we don't easily know strictly which flow started it without checking
                            // role again.
                            // Redirect to Main Menu or Specific Menu based on role
                            if (finalNewCustomer.getRole() == UserRole.CREDIT_CUSTOMER) {
                                creditCustomerFlowService.showCreditCustomerMenu(admin);
                            } else if (UserRole.EXECUTIVE.equals(admin.getRole())) {
                                BotSession refreshedSession = sessionManager.getSession(admin.getWaPhoneNumber());
                                executiveFlowService.showMainMenu(admin, refreshedSession);
                            } else {
                                showCustomerMenu(admin);
                            }
                        }
                    });
        } catch (Exception e) {
            log.error("Error adding customer", e);
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error adding customer: " + e.getMessage());
            showCustomerMenu(admin);
        }
    }

    // ...

    public void showAllCustomers(TeamMember admin) {
        showCustomersByRole(admin, UserRole.CUSTOMER);
    }

    public void showAllCreditCustomers(TeamMember admin) {
        showCustomersByRole(admin, UserRole.CREDIT_CUSTOMER);
    }

    public void showCustomersByRole(TeamMember admin, UserRole role) {
        List<Customer> customers = customerRepository.findAllByRole(role);

        if (customers.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "📋 *No " + role + "s found.*");
            // Also redirect back to appropriate menu
            if (role == UserRole.CREDIT_CUSTOMER) {
                creditCustomerFlowService.showCreditCustomerMenu(admin);
            } else {
                showCustomerMenu(admin);
            }
            return;
        }

        StringBuilder message = new StringBuilder(
                String.format("📋 *All %ss* (Total: %d)\n\n", role, customers.size()));
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
                            c.getDistanceFromBusinessKm() != null ? c.getDistanceFromBusinessKm() : 0.0));
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        if (role == UserRole.CREDIT_CUSTOMER) {
            creditCustomerFlowService.showCreditCustomerMenu(admin);
        } else {
            showCustomerMenu(admin);
        }
    }

    public void showMyCustomers(TeamMember admin) {
        List<Customer> customers = customerRepository.findByAddedBy(admin);

        if (customers.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "📋 *No customers found.*\n\nYou haven't added any customers yet.");

            if (UserRole.EXECUTIVE.equals(admin.getRole())) {
                BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());
                executiveFlowService.showMainMenu(admin, session);
            } else {
                showCustomerMenu(admin);
            }
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
                            c.getDistanceFromBusinessKm() != null ? c.getDistanceFromBusinessKm() : 0.0));
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());

        if (UserRole.EXECUTIVE.equals(admin.getRole())) {
            BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());
            executiveFlowService.showMainMenu(admin, session);
        } else {
            showCustomerMenu(admin);
        }
    }

    public void startUpdateCustomer(TeamMember admin, BotSession session) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "📝 *Update Customer*\n\nPlease enter the *Name* or *Phone Number* of the customer to search:");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_UPDATE_CUST_SEARCH.name());
    }

    public void handleUpdateCustomerSearch(TeamMember admin, BotSession session, String query) {
        List<Customer> customers = customerRepository
                .findByNameContainingIgnoreCaseOrPhoneNumberContaining(query.trim(), query.trim());

        if (customers.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "❌ No customers found matching '" + query + "'. Try again:");
            return;
        }

        if (customers.size() == 1) {
            startUpdateSelection(admin, session, customers.get(0));
        } else {
            List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();
            for (Customer c : customers) {
                if (rows.size() >= 10)
                    break;
                rows.add(WhatsAppMessageDto.RowDto.builder()
                        .id("UPD_CUST_SEL_" + c.getId())
                        .title(c.getName())
                        .description(c.getWaPhoneNumber())
                        .build());
            }
            whatsAppService.sendInteractiveList(admin.getWaPhoneNumber(), "� Multiple matches found. Select one:",
                    rows);
        }
    }

    public void startUpdateSelection(TeamMember admin, BotSession session, Customer customer) {
        sessionManager.setSessionData(session, "updateCustId", customer.getId().toString());

        String details = String.format(
                "👤 *%s*\n📞 %s\n📱 %s\nRole: %s\nZone: %s",
                customer.getName(), customer.getPhoneNumber(), customer.getWaPhoneNumber(),
                customer.getRole(),
                customer.getZone() != null ? customer.getZone().getZoneName() : "N/A");

        List<WhatsAppMessageDto.RowDto> rows = List.of(
                WhatsAppMessageDto.RowDto.builder().id("UPD_FIELD_NAME").title("Name").description("Edit Name").build(),
                WhatsAppMessageDto.RowDto.builder().id("UPD_FIELD_PHONE").title("Phone").description("Edit Phone")
                        .build(),
                WhatsAppMessageDto.RowDto.builder().id("UPD_FIELD_WAPHONE").title("WhatsApp")
                        .description("Edit WA Number").build(),
                WhatsAppMessageDto.RowDto.builder().id("UPD_FIELD_ROLE").title("Role").description("Edit Role").build(),
                WhatsAppMessageDto.RowDto.builder().id("UPD_FIELD_ZONE").title("Zone").description("Edit Delivery Zone")
                        .build(),
                WhatsAppMessageDto.RowDto.builder().id("CANCEL_UPDATE").title("❌ Cancel").description("Cancel Update")
                        .build());

        whatsAppService.sendInteractiveList(admin.getWaPhoneNumber(), details + "\n\nSelect field to update:", rows);
        sessionManager.updateState(session, AdminFlowStage.AWAITING_UPDATE_CUST_SELECT_FIELD.name());
    }

    public void handleUpdateCustSelect(TeamMember admin, BotSession session, String listId) {
        if (listId.startsWith("UPD_CUST_SEL_")) {
            Long id = Long.parseLong(listId.replace("UPD_CUST_SEL_", ""));
            Customer c = customerRepository.findById(id).orElse(null);
            if (c != null) {
                startUpdateSelection(admin, session, c);
            }
        }
    }

    public void handleUpdateFieldSelect(TeamMember admin, BotSession session, String fieldId) {
        if (fieldId.equals("CANCEL_UPDATE")) {
            showCustomerMenu(admin);
            return;
        }

        sessionManager.setSessionData(session, "updateField", fieldId);

        if (fieldId.equals("UPD_FIELD_ROLE")) {
            List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                    WhatsAppMessageDto.ButtonDto.builder().type("reply")
                            .reply(WhatsAppMessageDto.ReplyDto.builder().id("ROLE_CUSTOMER").title("CUSTOMER").build())
                            .build(),
                    WhatsAppMessageDto.ButtonDto.builder().type("reply").reply(WhatsAppMessageDto.ReplyDto.builder()
                            .id("ROLE_CREDIT_CUSTOMER").title("CREDIT_CUSTOMER").build()).build());
            whatsAppService.sendInteractiveButtons(admin.getWaPhoneNumber(), "Select new Role:", buttons);
            sessionManager.updateState(session, AdminFlowStage.AWAITING_UPDATE_CUST_NEW_VALUE.name());
        } else if (fieldId.equals("UPD_FIELD_ZONE")) {
            List<com.aps.domain.DeliveryZone> zones = deliveryZoneRepository.findAll();
            List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();
            for (com.aps.domain.DeliveryZone z : zones) {
                rows.add(WhatsAppMessageDto.RowDto.builder().id("ZONE_" + z.getId()).title(z.getZoneName())
                        .description("Select this zone").build());
            }
            whatsAppService.sendInteractiveList(admin.getWaPhoneNumber(), "Select new Zone:", rows);
            sessionManager.updateState(session, AdminFlowStage.AWAITING_UPDATE_CUST_NEW_VALUE.name());
        } else {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "Enter new value:");
            sessionManager.updateState(session, AdminFlowStage.AWAITING_UPDATE_CUST_NEW_VALUE.name());
        }
    }

    public void handleUpdateValueInput(TeamMember admin, BotSession session, String value) {
        try {
            String field = sessionManager.getSessionDataString(session, "updateField");
            String custIdStr = sessionManager.getSessionDataString(session, "updateCustId");
            if (custIdStr == null) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Session expired. Start again.");
                showCustomerMenu(admin);
                return;
            }
            Long custId = Long.parseLong(custIdStr);
            Customer c = customerRepository.findById(custId).orElse(null);

            if (c == null) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error: Customer not found.");
                return;
            }

            if (field.equals("UPD_FIELD_NAME")) {
                if (!inputValidator.isValidName(value)) {
                    whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid Name. Try again:");
                    return;
                }
                c.setName(value);
            } else if (field.equals("UPD_FIELD_PHONE")) {
                if (!inputValidator.isValidPhoneNumber(value)) {
                    whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid Phone. Try again:");
                    return;
                }
                c.setPhoneNumber(value);
            } else if (field.equals("UPD_FIELD_WAPHONE")) {
                if (!inputValidator.isValidPhoneNumber(value)) {
                    whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid WhatsApp. Try again:");
                    return;
                }
                c.setWaPhoneNumber(value);
                // Send Welcome Message to new number
                if (value != null && !value.isEmpty()) {
                    whatsAppService.sendSimpleText(
                            value,
                            "🎉 *കുടുംബത്തിലേക്ക് സ്വാഗതം!*\n\n" +
                                    "👋 ഹായ് " +
                                    c.getName() +
                                    ",\n" +
                                    "നിങ്ങളുടെ വാട്ട്‌സ്ആപ്പ് നമ്പർ അപ്‌ഡേറ്റ് ചെയ്തിരിക്കുന്നു.\n\n" +
                                    "🛍️ *ഓർഡർ ചെയ്യാൻ തുടങ്ങാം:*\n" +
                                    "ഞങ്ങളുടെ ഉൽപ്പന്നങ്ങൾ കാണാനും ഓർഡർ ചെയ്യാനും *'Hi'* എന്ന് റിപ്ലൈ ചെയ്യുക.\n\n"
                                    +
                                    "ഞങ്ങളെ തിരഞ്ഞെടുത്തതിന് നന്ദി!");
                }
            }
            // Role and Zone are handled via buttons/list, but if they send text?
            // Usually they trigger handleButtonReply, but we need to route those replies to
            // here?
            // Or handle them in handleUpdateValueInput if passed as text?
            // Button replies come as text? NO. They come as interactive/button_reply.
            // Delegate updates to appropriate logic

            customerRepository.save(c);
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "✅ Updated!");
            // Redirect to Main Menu
            if (c.getRole() == UserRole.CREDIT_CUSTOMER) {
                creditCustomerFlowService.showCreditCustomerMenu(admin);
            } else {
                showCustomerMenu(admin);
            }
        } catch (Exception e) {
            log.error("Update error", e);
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error: " + e.getMessage());
        }
    }

    public void handleUpdateOptionSelection(TeamMember admin, BotSession session, String optionId) {
        try {
            String custIdStr = sessionManager.getSessionDataString(session, "updateCustId");
            if (custIdStr == null)
                return;
            Long custId = Long.parseLong(custIdStr);
            Customer c = customerRepository.findById(custId).orElse(null);
            if (c == null)
                return;

            if (optionId.equals("ROLE_CUSTOMER"))
                c.setRole(UserRole.CUSTOMER);
            else if (optionId.equals("ROLE_CREDIT_CUSTOMER"))
                c.setRole(UserRole.CREDIT_CUSTOMER);
            else if (optionId.startsWith("ZONE_")) {
                Long zoneId = Long.parseLong(optionId.replace("ZONE_", ""));
                deliveryZoneRepository.findById(zoneId).ifPresent(c::setZone);
            }

            customerRepository.save(c);
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "✅ Updated!");
            // Redirect to Main Menu
            if (c.getRole() == UserRole.CREDIT_CUSTOMER) {
                creditCustomerFlowService.showCreditCustomerMenu(admin);
            } else {
                showCustomerMenu(admin);
            }

        } catch (Exception e) {
            log.error("Update option error", e);
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error updating option: " + e.getMessage());
        }
    }

    // --- Deletion Logic ---

    public void startDeleteCustomer(TeamMember admin, BotSession session) {
        whatsAppService.sendSimpleText(
                admin.getWaPhoneNumber(),
                "🗑️ *Delete Customer*\n\n⚠️ PLEASE READ CAREFULLY:\n" +
                        "This will *Archive & Remove* the customer from active lists.\n" +
                        "Order history (Placed/Spent) will be preserved in a summary.\n\n" +
                        "🆔 Please enter the **Customer ID** you wish to delete:");

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
                            .reply(WhatsAppMessageDto.ReplyDto.builder().id("CONFIRM_DELETE_" + customerId)
                                    .title("💥 Yes, DELETE").build())
                            .build(),
                    WhatsAppMessageDto.ButtonDto.builder()
                            .type("reply")
                            .reply(WhatsAppMessageDto.ReplyDto.builder().id("CANCEL_OPERATION").title("❌ Cancel")
                                    .build())
                            .build());

            whatsAppService.sendCartActionButtons(
                    admin.getWaPhoneNumber(),
                    String.format(
                            "⚠️ *Confirm Deletion*\n\n" +
                                    "Are you SURE you want to delete:\n" +
                                    "👤 *%s* (ID: %d)?\n\n" +
                                    "This action cannot be easily undone via WhatsApp.",
                            customer.getName(),
                            customerId),
                    buttons);

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
