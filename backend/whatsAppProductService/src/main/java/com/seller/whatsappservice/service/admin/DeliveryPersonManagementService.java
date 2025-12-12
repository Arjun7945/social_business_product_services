package com.seller.whatsappservice.service.admin;

import com.seller.whatsappservice.dto.WhatsAppMessageDto;
import com.seller.whatsappservice.model.TeamMember;
import com.seller.whatsappservice.model.enums.AdminFlowStage;
import com.seller.whatsappservice.model.enums.UserRole;
import com.seller.whatsappservice.repository.TeamMemberRepository;
import com.seller.whatsappservice.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryPersonManagementService {

    private final TeamMemberRepository teamMemberRepository;
    private final WhatsAppService whatsAppService;

    public void showDeliveryPersonMenu(TeamMember admin) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("ADD_DELIVERY")
                                .title("➕ Add")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("SHOW_ALL_DELIVERY")
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
                "🚚 *Delivery Person Management*\n\nWhat would you like to do?", buttons);
        admin.setCurrentAdminFlowStage(AdminFlowStage.DELIVERY_MENU);
    }

    public void startAddDeliveryPerson(TeamMember admin) {
        admin.setTempEntityType("DELIVERY");
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "➕ *Add New Delivery Person*\n\n📝 Please provide the delivery person's name:");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_DELIVERY_NAME);
    }

    public void handleDeliveryPersonNameInput(TeamMember admin, String name) {
        admin.setTempTeamMemberName(name.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Name: " + name.trim() + "\n\n📞 Please provide the phone number:");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_DELIVERY_PHONE);
    }

    public void handleDeliveryPersonPhoneInput(TeamMember admin, String phone) {
        admin.setTempTeamMemberPhone(phone.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Phone: " + phone.trim()
                        + "\n\n📱 Please provide the WhatsApp number (with country code):");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_DELIVERY_WAPHONE);
    }

    public void handleDeliveryPersonWaPhoneInput(TeamMember admin, String waPhone) {
        admin.setTempTeamMemberWaPhone(waPhone.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ WhatsApp: " + waPhone.trim() + "\n\n🔄 Is this delivery person active? (yes/no):");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_DELIVERY_STATUS);
    }

    public void handleDeliveryPersonStatusInput(TeamMember admin, String status) {
        boolean isActive = status.trim().equalsIgnoreCase("yes") || status.trim().equalsIgnoreCase("y");
        admin.setTempTeamMemberIsActive(isActive);

        String summary = String.format(
                "✅ *Delivery Person Details Summary:*\n\n" +
                        "👤 Name: %s\n" +
                        "📞 Phone: %s\n" +
                        "📱 WhatsApp: %s\n" +
                        "🔄 Status: %s\n\n" +
                        "Confirm to add this delivery person?",
                admin.getTempTeamMemberName(),
                admin.getTempTeamMemberPhone(),
                admin.getTempTeamMemberWaPhone(),
                isActive ? "Active" : "Inactive");

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
        admin.setCurrentAdminFlowStage(AdminFlowStage.CONFIRMING_DELIVERY_ADD);
    }

    public void finalizeDeliveryPersonAdd(TeamMember admin) {
        TeamMember newDeliveryPerson = TeamMember.builder()
                .name(admin.getTempTeamMemberName())
                .phoneNumber(admin.getTempTeamMemberPhone())
                .waPhoneNumber(admin.getTempTeamMemberWaPhone())
                .role(UserRole.DELIVERY_PERSON)
                .isActive(admin.getTempTeamMemberIsActive())
                .build();

        teamMemberRepository.save(newDeliveryPerson);

        whatsAppService.sendTeamMemberWelcomeMessage(
                newDeliveryPerson.getWaPhoneNumber(),
                newDeliveryPerson.getName(),
                newDeliveryPerson.getPhoneNumber(),
                "Delivery Person",
                admin.getName(),
                admin.getWaPhoneNumber());

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ *Delivery Person Added Successfully!*\n\n" +
                        "👤 " + newDeliveryPerson.getName()
                        + " has been added to the system.\n\n" +
                        "A welcome message has been sent to the new delivery person. 📲");

        showDeliveryPersonMenu(admin);
    }

    public void showAllDeliveryPersons(TeamMember admin) {
        List<TeamMember> deliveryPersons = teamMemberRepository.findByRole(UserRole.DELIVERY_PERSON);

        if (deliveryPersons.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "📋 *No delivery persons found.*");
            showDeliveryPersonMenu(admin);
            return;
        }

        StringBuilder message = new StringBuilder(
                String.format("📋 *All Delivery Persons* (Total: %d)\n\n", deliveryPersons.size()));
        int count = 1;
        for (TeamMember dp : deliveryPersons) {
            message.append(String.format("%d. *%s*\n   📞 %s\n   📱 %s\n   🔄 %s\n\n",
                    count++, dp.getName(), dp.getPhoneNumber(), dp.getWaPhoneNumber(),
                    dp.isActive() ? "Active" : "Inactive"));
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        showDeliveryPersonMenu(admin);
    }

    public void startUpdateDeliveryPerson(TeamMember admin) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "🚧 *Update Delivery Person*\n\nThis feature is coming soon!");
        showDeliveryPersonMenu(admin);
    }

    public void startDeleteDeliveryPerson(TeamMember admin) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "🚧 *Delete Delivery Person*\n\nThis feature is coming soon!");
        showDeliveryPersonMenu(admin);
    }
}
