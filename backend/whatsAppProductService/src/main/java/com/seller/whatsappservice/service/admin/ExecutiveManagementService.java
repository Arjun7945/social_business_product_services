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
public class ExecutiveManagementService {

    private final TeamMemberRepository teamMemberRepository;
    private final WhatsAppService whatsAppService;

    public void showExecutiveMenu(TeamMember admin) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("ADD_EXECUTIVE")
                                .title("➕ Add Executive")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("SHOW_ALL_EXECUTIVE")
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
                "💼 *Executive Management*\n\nWhat would you like to do?", buttons);
        admin.setCurrentAdminFlowStage(AdminFlowStage.EXECUTIVE_MENU);
    }

    public void startAddExecutive(TeamMember admin) {
        admin.setTempEntityType("EXECUTIVE");
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "➕ *Add New Executive*\n\n📝 Please provide the executive's name:");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_EXEC_NAME);
    }

    public void handleExecutiveNameInput(TeamMember admin, String name) {
        admin.setTempTeamMemberName(name.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Name: " + name.trim() + "\n\n📞 Please provide the phone number:");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_EXEC_PHONE);
    }

    public void handleExecutivePhoneInput(TeamMember admin, String phone) {
        admin.setTempTeamMemberPhone(phone.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Phone: " + phone.trim()
                        + "\n\n📱 Please provide the WhatsApp number (with country code):");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_EXEC_WAPHONE);
    }

    public void handleExecutiveWaPhoneInput(TeamMember admin, String waPhone) {
        admin.setTempTeamMemberWaPhone(waPhone.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ WhatsApp: " + waPhone.trim() + "\n\n🔄 Is this executive active? (yes/no):");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_EXEC_STATUS);
    }

    public void handleExecutiveStatusInput(TeamMember admin, String status) {
        boolean isActive = status.trim().equalsIgnoreCase("yes") || status.trim().equalsIgnoreCase("y");
        admin.setTempTeamMemberIsActive(isActive);

        String summary = String.format(
                "✅ *Executive Details Summary:*\n\n" +
                        "👤 Name: %s\n" +
                        "📞 Phone: %s\n" +
                        "📱 WhatsApp: %s\n" +
                        "🔄 Status: %s\n\n" +
                        "Confirm to add this executive?",
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
        admin.setCurrentAdminFlowStage(AdminFlowStage.CONFIRMING_EXEC_ADD);
    }

    public void finalizeExecutiveAdd(TeamMember admin) {
        TeamMember newExecutive = TeamMember.builder()
                .name(admin.getTempTeamMemberName())
                .phoneNumber(admin.getTempTeamMemberPhone())
                .waPhoneNumber(admin.getTempTeamMemberWaPhone())
                .role(UserRole.EXECUTIVE)
                .isActive(admin.getTempTeamMemberIsActive())
                .build();

        teamMemberRepository.save(newExecutive);

        whatsAppService.sendTeamMemberWelcomeMessage(
                newExecutive.getWaPhoneNumber(),
                newExecutive.getName(),
                newExecutive.getPhoneNumber(),
                "Executive",
                admin.getName(),
                admin.getWaPhoneNumber());

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ *Executive Added Successfully!*\n\n" +
                        "👤 " + newExecutive.getName()
                        + " has been added to the system.\n\n" +
                        "A welcome message has been sent to the new executive. 📲");

        showExecutiveMenu(admin);
    }

    public void showAllExecutives(TeamMember admin) {
        List<TeamMember> executives = teamMemberRepository.findByRole(UserRole.EXECUTIVE);

        if (executives.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "📋 *No executives found.*");
            showExecutiveMenu(admin);
            return;
        }

        StringBuilder message = new StringBuilder(
                String.format("📋 *All Executives* (Total: %d)\n\n", executives.size()));
        int count = 1;
        for (TeamMember exec : executives) {
            message.append(String.format("%d. *%s*\n   📞 %s\n   📱 %s\n   🔄 %s\n\n",
                    count++, exec.getName(), exec.getPhoneNumber(), exec.getWaPhoneNumber(),
                    exec.isActive() ? "Active" : "Inactive"));
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        showExecutiveMenu(admin);
    }

    public void startUpdateExecutive(TeamMember admin) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "🚧 *Update Executive*\n\nThis feature is coming soon!");
        showExecutiveMenu(admin);
    }

    public void startDeleteExecutive(TeamMember admin) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "🚧 *Delete Executive*\n\nThis feature is coming soon!");
        showExecutiveMenu(admin);
    }
}
