package com.aps.service.admin;

import com.aps.domain.BotSession;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AdminFlowStage;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.TeamMemberRepository;
import com.aps.service.BotSessionManager;
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppMessageDto;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExecutiveManagementService {

    private final TeamMemberRepository teamMemberRepository;
    private final WhatsAppService whatsAppService;
    private final BotSessionManager sessionManager;

    public ExecutiveManagementService(TeamMemberRepository teamMemberRepository,
            WhatsAppService whatsAppService,
            BotSessionManager sessionManager) {
        this.teamMemberRepository = teamMemberRepository;
        this.whatsAppService = whatsAppService;
        this.sessionManager = sessionManager;
    }

    public void showExecutiveMenu(TeamMember admin) {
        BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());

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

        sessionManager.updateState(session, AdminFlowStage.EXECUTIVE_MENU.name());
    }

    public void startAddExecutive(TeamMember admin, BotSession session) {
        sessionManager.setSessionData(session, "tempEntityType", "EXECUTIVE");
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "➕ *Add New Executive*\n\n📝 Please provide the executive's name:");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_EXEC_NAME.name());
    }

    public void handleExecutiveNameInput(TeamMember admin, BotSession session, String name) {
        sessionManager.setSessionData(session, "tempTeamMemberName", name.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Name: " + name.trim() + "\n\n📞 Please provide the phone number:");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_EXEC_PHONE.name());
    }

    public void handleExecutivePhoneInput(TeamMember admin, BotSession session, String phone) {
        sessionManager.setSessionData(session, "tempTeamMemberPhone", phone.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Phone: " + phone.trim()
                        + "\n\n📱 Please provide the WhatsApp number (with country code):");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_EXEC_WAPHONE.name());
    }

    public void handleExecutiveWaPhoneInput(TeamMember admin, BotSession session, String waPhone) {
        sessionManager.setSessionData(session, "tempTeamMemberWaPhone", waPhone.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ WhatsApp: " + waPhone.trim() + "\n\n🔄 Is this executive active? (yes/no):");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_EXEC_STATUS.name());
    }

    public void handleExecutiveStatusInput(TeamMember admin, BotSession session, String status) {
        boolean isActive = status.trim().equalsIgnoreCase("yes") || status.trim().equalsIgnoreCase("y");
        sessionManager.setSessionData(session, "tempTeamMemberIsActive", String.valueOf(isActive));

        String tempName = sessionManager.getSessionDataString(session, "tempTeamMemberName");
        String tempPhone = sessionManager.getSessionDataString(session, "tempTeamMemberPhone");
        String tempWaPhone = sessionManager.getSessionDataString(session, "tempTeamMemberWaPhone");

        String summary = String.format(
                "✅ *Executive Details Summary:*\n\n" +
                        "👤 Name: %s\n" +
                        "📞 Phone: %s\n" +
                        "📱 WhatsApp: %s\n" +
                        "🔄 Status: %s\n\n" +
                        "Confirm to add this executive?",
                tempName, tempPhone, tempWaPhone,
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
        sessionManager.updateState(session, AdminFlowStage.CONFIRMING_EXEC_ADD.name());
    }

    @Async
    @Transactional
    public void finalizeExecutiveAdd(TeamMember admin, BotSession session) {
        String tempName = sessionManager.getSessionDataString(session, "tempTeamMemberName");
        String tempPhone = sessionManager.getSessionDataString(session, "tempTeamMemberPhone");
        String tempWaPhone = sessionManager.getSessionDataString(session, "tempTeamMemberWaPhone");
        String tempIsActiveStr = sessionManager.getSessionDataString(session, "tempTeamMemberIsActive");
        boolean tempIsActive = Boolean.parseBoolean(tempIsActiveStr);

        try {
            TeamMember newExecutive = new TeamMember();
            newExecutive.setName(tempName);
            newExecutive.setPhoneNumber(tempPhone);
            newExecutive.setWaPhoneNumber(tempWaPhone);
            newExecutive.setRole(UserRole.EXECUTIVE);
            newExecutive.setIsActive(tempIsActive);

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
        } catch (Exception e) {
            e.printStackTrace();
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error adding executive: " + e.getMessage());
            showExecutiveMenu(admin);
        }
    }

    public void showAllExecutives(TeamMember admin) {
        List<TeamMember> executives = teamMemberRepository.findAll().stream()
                .filter(tm -> tm.getRole() == UserRole.EXECUTIVE)
                .toList();

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
                    exec.getIsActive() != null && exec.getIsActive() ? "Active" : "Inactive"));
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        showExecutiveMenu(admin);
    }

    public void startUpdateExecutive(TeamMember admin, BotSession session) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "🚧 *Update Executive*\n\nThis feature is coming soon!");
        showExecutiveMenu(admin);
    }

    public void startDeleteExecutive(TeamMember admin, BotSession session) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "🚧 *Delete Executive*\n\nThis feature is coming soon!");
        showExecutiveMenu(admin);
    }
}
