package com.seller.whatsappservice.service.admin;

import com.seller.whatsappservice.dto.WhatsAppMessageDto;
import com.seller.whatsappservice.dto.WhatsAppWebhookDto;
import com.seller.whatsappservice.model.Customer;
import com.seller.whatsappservice.model.TeamMember;
import com.seller.whatsappservice.model.enums.AdminFlowStage;
import com.seller.whatsappservice.model.enums.CustomerFlowStage;
import com.seller.whatsappservice.model.enums.UserRole;
import com.seller.whatsappservice.repository.CustomerRepository;
import com.seller.whatsappservice.service.LocationValidationService;
import com.seller.whatsappservice.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerManagementService {

    private final CustomerRepository customerRepository;
    private final WhatsAppService whatsAppService;
    private final LocationValidationService locationValidationService;

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
        admin.setCurrentAdminFlowStage(AdminFlowStage.CUSTOMER_MENU);
    }

    public void startAddCustomer(TeamMember admin) {
        admin.setTempEntityType("CUSTOMER");
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "➕ *Add New Customer*\n\n📝 Please provide the customer's name:");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_CUST_NAME);
    }

    public void handleCustomerNameInput(TeamMember admin, String name) {
        admin.setTempCustomerName(name.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Name: " + name.trim() + "\n\n📞 Please provide the customer's phone number:");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_CUST_PHONE);
    }

    public void handleCustomerPhoneInput(TeamMember admin, String phone) {
        admin.setTempCustomerPhone(phone.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Phone: " + phone.trim()
                        + "\n\n📱 Please provide the customer's WhatsApp number (with country code, e.g., 919876543210):");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_CUST_WAPHONE);
    }

    public void handleCustomerWaPhoneInput(TeamMember admin, String waPhone) {
        admin.setTempCustomerWaPhone(waPhone.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ WhatsApp: " + waPhone.trim() + "\n\n📍 Please share the customer's location.\n\n" +
                        "📢 *How to share:*\n" +
                        "• Tap the attachment icon (📎)\n" +
                        "• Select 'Location'\n" +
                        "• Send location");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_CUST_LOCATION);
    }

    public void handleLocationMessage(TeamMember admin, WhatsAppWebhookDto.Location location) {
        if (admin.getCurrentAdminFlowStage() != AdminFlowStage.AWAITING_CUST_LOCATION) {
            return;
        }

        double customerLat = location.getLatitude();
        double customerLon = location.getLongitude();
        double distance = locationValidationService.getDistanceFromBusiness(customerLat, customerLon);

        String summary = String.format(
                "✅ *Customer Details Summary:*\n\n" +
                        "👤 Name: %s\n" +
                        "📞 Phone: %s\n" +
                        "📱 WhatsApp: %s\n" +
                        "📍 Location: %.6f, %.6f\n" +
                        "📏 Distance: %.2f km\n\n" +
                        "Confirm to add this customer?",
                admin.getTempCustomerName(),
                admin.getTempCustomerPhone(),
                admin.getTempCustomerWaPhone(),
                customerLat, customerLon, distance);

        admin.setTempFieldName(String.valueOf(customerLat));
        admin.setTempFieldValue(String.valueOf(customerLon));

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
        admin.setCurrentAdminFlowStage(AdminFlowStage.CONFIRMING_CUST_ADD);
    }

    public void finalizeCustomerAdd(TeamMember admin) {
        double lat = Double.parseDouble(admin.getTempFieldName());
        double lon = Double.parseDouble(admin.getTempFieldValue());
        double distance = locationValidationService.getDistanceFromBusiness(lat, lon);

        Customer newCustomer = Customer.builder()
                .name(admin.getTempCustomerName())
                .phoneNumber(admin.getTempCustomerPhone())
                .waPhoneNumber(admin.getTempCustomerWaPhone())
                .locationLat(lat)
                .locationLon(lon)
                .distanceFromBusinessKm(distance)
                .role(UserRole.CUSTOMER)
                .currentFlowStage(CustomerFlowStage.REGISTERED)
                .registeredAt(LocalDateTime.now())
                .build();

        customerRepository.save(newCustomer);

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ *Customer Added Successfully!*\n\n" +
                        "👤 " + newCustomer.getName() + " has been added to the system.\n\n" +
                        "The customer can now start ordering by sending 'Hi' to the business number.");

        showCustomerMenu(admin);
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

    public void startUpdateCustomer(TeamMember admin) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "🚧 *Update Customer*\n\nThis feature is coming soon!");
        showCustomerMenu(admin);
    }

    public void startDeleteCustomer(TeamMember admin) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "🚧 *Delete Customer*\n\nThis feature is coming soon!");
        showCustomerMenu(admin);
    }
}
