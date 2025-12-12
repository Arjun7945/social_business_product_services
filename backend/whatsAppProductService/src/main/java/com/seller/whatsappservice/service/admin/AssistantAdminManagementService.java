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
public class AssistantAdminManagementService {

    private final TeamMemberRepository teamMemberRepository;
    private final WhatsAppService whatsAppService;

    public void showAssistantAdminMenu(TeamMember admin) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("ADD_ASSISTANT")
                                .title("➕ Add")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("SHOW_ALL_ASSISTANT")
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
                "🛡️ *Assistant Admin Management*\n\nWhat would you like to do?", buttons);
        admin.setCurrentAdminFlowStage(AdminFlowStage.ASSISTANT_MENU);
    }

    public void startAddAssistantAdmin(TeamMember admin) {
        admin.setTempEntityType("ASSISTANT");
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "➕ *Add New Assistant Admin*\n\n📝 Please provide the assistant admin's name:");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_ASSISTANT_NAME);
    }

    public void handleAssistantAdminNameInput(TeamMember admin, String name) {
        admin.setTempTeamMemberName(name.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Name: " + name.trim() + "\n\n📞 Please provide the phone number:");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_ASSISTANT_PHONE);
    }

    public void handleAssistantAdminPhoneInput(TeamMember admin, String phone) {
        admin.setTempTeamMemberPhone(phone.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Phone: " + phone.trim()
                        + "\n\n📱 Please provide the WhatsApp number (with country code):");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_ASSISTANT_WAPHONE);
    }

    public void handleAssistantAdminWaPhoneInput(TeamMember admin, String waPhone) {
        admin.setTempTeamMemberWaPhone(waPhone.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ WhatsApp: " + waPhone.trim() + "\n\n🔄 Is this assistant admin active? (yes/no):");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_ASSISTANT_STATUS);
    }

    public void handleAssistantAdminStatusInput(TeamMember admin, String status) {
        boolean isActive = status.trim().equalsIgnoreCase("yes") || status.trim().equalsIgnoreCase("y");
        admin.setTempTeamMemberIsActive(isActive);

        String summary = String.format(
                "✅ *Assistant Admin Details Summary:*\n\n" +
                        "👤 Name: %s\n" +
                        "📞 Phone: %s\n" +
                        "📱 WhatsApp: %s\n" +
                        "🔄 Status: %s\n\n" +
                        "Confirm to add this assistant admin?",
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
        admin.setCurrentAdminFlowStage(AdminFlowStage.CONFIRMING_ASSISTANT_ADD);
    }

    public void finalizeAssistantAdminAdd(TeamMember admin) {
        TeamMember newAssistantAdmin = TeamMember.builder()
                .name(admin.getTempTeamMemberName())
                .phoneNumber(admin.getTempTeamMemberPhone())
                .waPhoneNumber(admin.getTempTeamMemberWaPhone())
                .role(UserRole.ASSISTANT_ADMIN)
                .isActive(admin.getTempTeamMemberIsActive())
                .build();

        teamMemberRepository.save(newAssistantAdmin);

        whatsAppService.sendTeamMemberWelcomeMessage(
                newAssistantAdmin.getWaPhoneNumber(),
                newAssistantAdmin.getName(),
                newAssistantAdmin.getPhoneNumber(),
                "Assistant Admin",
                admin.getName(),
                admin.getWaPhoneNumber());

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ *Assistant Admin Added Successfully!*\n\n" +
                        "👤 " + newAssistantAdmin.getName()
                        + " has been added to the system.\n\n" +
                        "A welcome message has been sent to the new assistant admin. 📲");

        showAssistantAdminMenu(admin);
    }

    public void showAllAssistantAdmins(TeamMember admin) {
        List<TeamMember> assistantAdmins = teamMemberRepository.findByRole(UserRole.ASSISTANT_ADMIN);

        if (assistantAdmins.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "📋 *No assistant admins found.*");
            showAssistantAdminMenu(admin);
            return;
        }

        StringBuilder message = new StringBuilder(
                String.format("📋 *All Assistant Admins* (Total: %d)\n\n", assistantAdmins.size()));
        int count = 1;
        for (TeamMember aa : assistantAdmins) {
            message.append(String.format("%d. *%s*\n   📞 %s\n   📱 %s\n   🔄 %s\n\n",
                    count++, aa.getName(), aa.getPhoneNumber(), aa.getWaPhoneNumber(),
                    aa.isActive() ? "Active" : "Inactive"));
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        showAssistantAdminMenu(admin);
    }

    public void startUpdateAssistantAdmin(TeamMember admin) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "🚧 *Update Assistant Admin*\n\nThis feature is coming soon!");
        showAssistantAdminMenu(admin);
    }

    public void startDeleteAssistantAdmin(TeamMember admin) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "🚧 *Delete Assistant Admin*\n\nThis feature is coming soon!");
        showAssistantAdminMenu(admin);
    }
}
