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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.aps.service.util.InputValidator;

import java.util.List;

@Service
public class AssistantAdminManagementService {

        private final TeamMemberRepository teamMemberRepository;
        private final WhatsAppService whatsAppService;
        private final BotSessionManager sessionManager;
        private final InputValidator inputValidator;

        public AssistantAdminManagementService(TeamMemberRepository teamMemberRepository,
                        WhatsAppService whatsAppService,
                        BotSessionManager sessionManager,
                        InputValidator inputValidator) {
                this.teamMemberRepository = teamMemberRepository;
                this.whatsAppService = whatsAppService;
                this.sessionManager = sessionManager;
                this.inputValidator = inputValidator;
        }

        public void showAssistantAdminMenu(TeamMember admin) {
                BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());

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

                sessionManager.updateState(session, AdminFlowStage.ASSISTANT_MENU.name());
        }

        public void startAddAssistantAdmin(TeamMember admin, BotSession session) {
                sessionManager.setSessionData(session, "tempEntityType", "ASSISTANT");
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "➕ *Add New Assistant Admin*\n\n📝 Please provide the assistant admin's name:");
                sessionManager.updateState(session, AdminFlowStage.AWAITING_ASSISTANT_NAME.name());
        }

        public void handleAssistantAdminNameInput(TeamMember admin, BotSession session, String name) {
                if (!inputValidator.isValidName(name)) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Invalid name. Use letters/spaces/dots only (min 2 chars). Try again:");
                        return;
                }
                sessionManager.setSessionData(session, "tempTeamMemberName", name.trim());
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "✅ Name: " + name.trim() + "\n\n📞 Please provide the phone number:");
                sessionManager.updateState(session, AdminFlowStage.AWAITING_ASSISTANT_PHONE.name());
        }

        public void handleAssistantAdminPhoneInput(TeamMember admin, BotSession session, String phone) {
                if (!inputValidator.isValidPhoneNumber(phone)) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Invalid phone number format. Try again:");
                        return;
                }
                sessionManager.setSessionData(session, "tempTeamMemberPhone", phone.trim());
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "✅ Phone: " + phone.trim()
                                                + "\n\n📱 Please provide the WhatsApp number (with country code):");
                sessionManager.updateState(session, AdminFlowStage.AWAITING_ASSISTANT_WAPHONE.name());
        }

        public void handleAssistantAdminWaPhoneInput(TeamMember admin, BotSession session, String waPhone) {
                if (!inputValidator.isValidPhoneNumber(waPhone)) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Invalid WhatsApp number format. Try again:");
                        return;
                }
                sessionManager.setSessionData(session, "tempTeamMemberWaPhone", waPhone.trim());
                List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("ASSISTANT_ACTIVE_YES")
                                                                .title("Yes")
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id("ASSISTANT_ACTIVE_NO")
                                                                .title("No")
                                                                .build())
                                                .build());

                whatsAppService.sendCartActionButtons(admin.getWaPhoneNumber(),
                                "✅ WhatsApp: " + waPhone.trim() + "\n\n🔄 Is this assistant admin active?", buttons);
                sessionManager.updateState(session, AdminFlowStage.AWAITING_ASSISTANT_STATUS.name());
        }

        public void handleAssistantAdminStatusInput(TeamMember admin, BotSession session, String status) {
                boolean isActive = status.trim().equalsIgnoreCase("yes") || status.trim().equalsIgnoreCase("y");
                sessionManager.setSessionData(session, "tempTeamMemberIsActive", String.valueOf(isActive));

                String tempName = sessionManager.getSessionDataString(session, "tempTeamMemberName");
                String tempPhone = sessionManager.getSessionDataString(session, "tempTeamMemberPhone");
                String tempWaPhone = sessionManager.getSessionDataString(session, "tempTeamMemberWaPhone");

                String summary = String.format(
                                "✅ *Assistant Admin Details Summary:*\n\n" +
                                                "👤 Name: %s\n" +
                                                "📞 Phone: %s\n" +
                                                "📱 WhatsApp: %s\n" +
                                                "🔄 Status: %s\n\n" +
                                                "Confirm to add this assistant admin?",
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
                sessionManager.updateState(session, AdminFlowStage.CONFIRMING_ASSISTANT_ADD.name());
        }

        @Async
        @Transactional
        public void finalizeAssistantAdminAdd(TeamMember admin, BotSession session) {
                String tempName = sessionManager.getSessionDataString(session, "tempTeamMemberName");
                String tempPhone = sessionManager.getSessionDataString(session, "tempTeamMemberPhone");
                String tempWaPhone = sessionManager.getSessionDataString(session, "tempTeamMemberWaPhone");
                String tempIsActiveStr = sessionManager.getSessionDataString(session, "tempTeamMemberIsActive");
                boolean tempIsActive = Boolean.parseBoolean(tempIsActiveStr);

                try {
                        TeamMember newAssistantAdmin = new TeamMember();
                        newAssistantAdmin.setName(tempName);
                        newAssistantAdmin.setPhoneNumber(tempPhone);
                        newAssistantAdmin.setWaPhoneNumber(tempWaPhone);
                        newAssistantAdmin.setRole(UserRole.ASSISTANT_ADMIN);
                        newAssistantAdmin.setIsActive(tempIsActive);

                        teamMemberRepository.saveAndFlush(newAssistantAdmin);

                        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                                @Override
                                public void afterCommit() {
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
                        });
                } catch (Exception e) {
                        e.printStackTrace();
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Error adding assistant admin: " + e.getMessage());
                        showAssistantAdminMenu(admin);
                }
        }

        public void showAllAssistantAdmins(TeamMember admin) {
                List<TeamMember> assistantAdmins = teamMemberRepository.findAll().stream()
                                .filter(tm -> tm.getRole() == UserRole.ASSISTANT_ADMIN)
                                .toList();

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
                                        aa.getIsActive() != null && aa.getIsActive() ? "Active" : "Inactive"));
                }

                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
                showAssistantAdminMenu(admin);
        }

        public void startUpdateAssistantAdmin(TeamMember admin, BotSession session) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "🚧 *Update Assistant Admin*\n\nThis feature is coming soon!");
                showAssistantAdminMenu(admin);
        }

        public void startDeleteAssistantAdmin(TeamMember admin, BotSession session) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "🚧 *Delete Assistant Admin*\n\nThis feature is coming soon!");
                showAssistantAdminMenu(admin);
        }
}
