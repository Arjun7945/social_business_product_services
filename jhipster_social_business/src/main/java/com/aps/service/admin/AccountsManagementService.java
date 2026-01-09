package com.aps.service.admin;

import com.aps.domain.BotSession;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AdminFlowStage;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.TeamMemberRepository;
import com.aps.service.BotSessionManager;
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.util.InputValidator;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class AccountsManagementService {

    private final TeamMemberRepository teamMemberRepository;
    private final WhatsAppService whatsAppService;
    private final BotSessionManager sessionManager;
    private final InputValidator inputValidator;

    public AccountsManagementService(
            TeamMemberRepository teamMemberRepository,
            WhatsAppService whatsAppService,
            BotSessionManager sessionManager,
            InputValidator inputValidator) {
        this.teamMemberRepository = teamMemberRepository;
        this.whatsAppService = whatsAppService;
        this.sessionManager = sessionManager;
        this.inputValidator = inputValidator;
    }

    public void showAccountsMenu(TeamMember admin) {
        BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());

        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("ADD_ACCOUNTS").title("➕ Add Member").build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("SHOW_ALL_ACCOUNTS").title("📋 Show All")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("BACK_TO_MAIN").title("⬅️ Back").build())
                        .build());

        whatsAppService.sendCartActionButtons(
                admin.getWaPhoneNumber(),
                "📊 *Accounts Team Management*\n\nWhat would you like to do?",
                buttons);

        sessionManager.updateState(session, AdminFlowStage.ACCOUNTS_MENU.name());
    }

    public void startAddAccountsMember(TeamMember admin, BotSession session) {
        sessionManager.setSessionData(session, "tempEntityType", "ACCOUNTS_TEAM");
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "➕ *Add New Accounts Member*\n\n📝 Please provide the member's name:");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_ACC_NAME.name());
    }

    public void handleAccountsNameInput(TeamMember admin, BotSession session, String name) {
        if (!inputValidator.isValidName(name)) {
            whatsAppService.sendSimpleText(
                    admin.getWaPhoneNumber(),
                    "❌ Invalid name. Use letters/spaces/dots only (min 2 chars). Try again:");
            return;
        }
        sessionManager.setSessionData(session, "tempTeamMemberName", name.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Name: " + name.trim() + "\n\n📞 Please provide the phone number:");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_ACC_PHONE.name());
    }

    public void handleAccountsPhoneInput(TeamMember admin, BotSession session, String phone) {
        if (!inputValidator.isValidPhoneNumber(phone)) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid phone number format. Try again:");
            return;
        }
        sessionManager.setSessionData(session, "tempTeamMemberPhone", phone.trim());
        whatsAppService.sendSimpleText(
                admin.getWaPhoneNumber(),
                "✅ Phone: " + phone.trim() + "\n\n📱 Please provide the WhatsApp number (with country code):");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_ACC_WAPHONE.name());
    }

    public void handleAccountsWaPhoneInput(TeamMember admin, BotSession session, String waPhone) {
        if (!inputValidator.isValidPhoneNumber(waPhone)) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid WhatsApp number format. Try again:");
            return;
        }
        sessionManager.setSessionData(session, "tempTeamMemberWaPhone", waPhone.trim());
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("ACC_ACTIVE_YES").title("Yes").build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("ACC_ACTIVE_NO").title("No").build())
                        .build());

        whatsAppService.sendCartActionButtons(
                admin.getWaPhoneNumber(),
                "✅ WhatsApp: " + waPhone.trim() + "\n\n🔄 Is this member active?",
                buttons);
        sessionManager.updateState(session, AdminFlowStage.AWAITING_ACC_STATUS.name());
    }

    public void handleAccountsStatusInput(TeamMember admin, BotSession session, String status) {
        boolean isActive = status.trim().equalsIgnoreCase("yes") || status.trim().equalsIgnoreCase("y");
        sessionManager.setSessionData(session, "tempTeamMemberIsActive", String.valueOf(isActive));

        String tempName = sessionManager.getSessionDataString(session, "tempTeamMemberName");
        String tempPhone = sessionManager.getSessionDataString(session, "tempTeamMemberPhone");
        String tempWaPhone = sessionManager.getSessionDataString(session, "tempTeamMemberWaPhone");

        String summary = String.format(
                "✅ *Accounts Member Details Summary:*\n\n" +
                        "👤 Name: %s\n" +
                        "📞 Phone: %s\n" +
                        "📱 WhatsApp: %s\n" +
                        "🔄 Status: %s\n\n" +
                        "Confirm to add this member?",
                tempName,
                tempPhone,
                tempWaPhone,
                isActive ? "Active" : "Inactive");

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
        sessionManager.updateState(session, AdminFlowStage.CONFIRMING_ACC_ADD.name());
    }

    @Async
    @Transactional
    public void finalizeAccountsMemberAdd(TeamMember admin, BotSession session) {
        String tempName = sessionManager.getSessionDataString(session, "tempTeamMemberName");
        String tempPhone = sessionManager.getSessionDataString(session, "tempTeamMemberPhone");
        String tempWaPhone = sessionManager.getSessionDataString(session, "tempTeamMemberWaPhone");
        String tempIsActiveStr = sessionManager.getSessionDataString(session, "tempTeamMemberIsActive");
        boolean tempIsActive = Boolean.parseBoolean(tempIsActiveStr);

        try {
            TeamMember newMember = new TeamMember();
            newMember.setName(tempName);
            newMember.setPhoneNumber(tempPhone);
            newMember.setWaPhoneNumber(tempWaPhone);
            newMember.setRole(UserRole.ACCOUNTS_TEAM);
            newMember.setIsActive(tempIsActive);

            teamMemberRepository.saveAndFlush(newMember);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            whatsAppService.sendTeamMemberWelcomeMessage(
                                    newMember.getWaPhoneNumber(),
                                    newMember.getName(),
                                    newMember.getPhoneNumber(),
                                    "Accounts Team",
                                    admin.getName(),
                                    admin.getWaPhoneNumber());

                            whatsAppService.sendSimpleText(
                                    admin.getWaPhoneNumber(),
                                    "✅ *Accounts Member Added Successfully!*\n\n" +
                                            "👤 " +
                                            newMember.getName() +
                                            " has been added to the system.\n\n" +
                                            "A welcome message has been sent to them. 📲");
                            showAccountsMenu(admin);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error adding member: " + e.getMessage());
            showAccountsMenu(admin);
        }
    }

    public void showAllAccountsMembers(TeamMember admin) {
        List<TeamMember> members = teamMemberRepository.findAllByRole(UserRole.ACCOUNTS_TEAM);

        if (members.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "📋 *No accounts team members found.*");
            showAccountsMenu(admin);
            return;
        }

        StringBuilder message = new StringBuilder(
                String.format("📋 *All Accounts Team Members* (Total: %d)\n\n", members.size()));
        int count = 1;
        for (TeamMember member : members) {
            message.append(
                    String.format(
                            "%d. *%s*\n   📞 %s\n   📱 %s\n   🔄 %s\n\n",
                            count++,
                            member.getName(),
                            member.getPhoneNumber(),
                            member.getWaPhoneNumber(),
                            member.getIsActive() != null && member.getIsActive() ? "Active" : "Inactive"));
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        showAccountsMenu(admin);
    }
}
