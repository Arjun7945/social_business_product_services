package com.seller.whatsappservice.service;

import com.seller.whatsappservice.dto.WhatsAppMessageDto;
import com.seller.whatsappservice.dto.WhatsAppWebhookDto;
import com.seller.whatsappservice.model.TeamMember;
import com.seller.whatsappservice.model.enums.AdminFlowStage;
import com.seller.whatsappservice.model.enums.UserRole;
import com.seller.whatsappservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service to handle developer conversation flow via WhatsApp
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeveloperFlowService {

    private final TeamMemberRepository teamMemberRepository;
    private final WhatsAppService whatsAppService;

    /**
     * Process incoming message from a developer
     */
    @Transactional
    public void handleDeveloperMessage(TeamMember developer, WhatsAppWebhookDto.Message message) {
        log.info("Processing developer message from: {} ({})", developer.getName(), developer.getWaPhoneNumber());

        // Handle different message types
        if (message.getType().equals("text") && message.getText() != null) {
            handleTextMessage(developer, message.getText().getBody());
        } else if (message.getType().equals("interactive")) {
            if (message.getInteractive().getType().equals("button_reply")) {
                handleButtonReply(developer, message.getInteractive().getButtonReply());
            }
        }

        teamMemberRepository.save(developer);
    }

    /**
     * Handle text messages based on developer flow stage
     */
    private void handleTextMessage(TeamMember developer, String text) {
        AdminFlowStage stage = developer.getCurrentAdminFlowStage();

        if (stage == null) {
            stage = AdminFlowStage.IDLE;
        }

        log.info("Developer {} in stage {} sent: {}", developer.getName(), stage, text);

        // Handle ABORT command
        if (text.trim().equalsIgnoreCase("ABORT")) {
            developer.setTempTeamMemberName(null);
            developer.setTempTeamMemberPhone(null);
            showMainMenu(developer);
            return;
        }

        switch (stage) {
            case IDLE:
                handleIdleState(developer, text);
                break;
            case AWAITING_ADMIN_NAME:
                handleAwaitingAdminName(developer, text);
                break;
            case AWAITING_ADMIN_PHONE:
                handleAwaitingAdminPhone(developer, text);
                break;
            case AWAITING_ADMIN_WAPHONE:
                handleAwaitingAdminWaPhone(developer, text);
                break;
            default:
                showMainMenu(developer);
        }
    }

    /**
     * Handle idle state - show main menu on "hi" or "hello"
     */
    private void handleIdleState(TeamMember developer, String text) {
        if (text.trim().equalsIgnoreCase("hi") || text.trim().equalsIgnoreCase("hello")) {
            showMainMenu(developer);
        } else {
            whatsAppService.sendSimpleText(developer.getWaPhoneNumber(),
                    "👋 Send 'hi' to see the developer menu.");
        }
    }

    /**
     * Show main menu to developer
     */
    private void showMainMenu(TeamMember developer) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("MANAGE_ADMIN")
                                .title("Manage Admins")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("MANAGE_EXECUTIVE")
                                .title("Manage Executives")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("MANAGE_CUSTOMER")
                                .title("Manage Customers")
                                .build())
                        .build());

        String message = String.format(
                "👨‍💻 *Hello Developer %s!* \n\n🛠️ System Control Panel\n\nSelect a role to manage:\n\n💡 Tip: Send 'ABORT' anytime to return to this menu.",
                developer.getName());
        whatsAppService.sendCartActionButtons(developer.getWaPhoneNumber(), message, buttons);
        developer.setCurrentAdminFlowStage(AdminFlowStage.IDLE);
    }

    /**
     * Handle button replies from developer
     */
    private void handleButtonReply(TeamMember developer, WhatsAppWebhookDto.ButtonReply buttonReply) {
        String buttonId = buttonReply.getId();
        log.info("Developer button reply: {}", buttonId);

        switch (buttonId) {
            case "MANAGE_ADMIN":
                showAdminManagementMenu(developer);
                break;
            case "ADD_ADMIN":
                startAddAdminFlow(developer);
                break;
            case "SHOW_ADMINS":
                showAllAdmins(developer);
                break;
            case "MANAGE_EXECUTIVE":
            case "MANAGE_CUSTOMER":
                whatsAppService.sendSimpleText(developer.getWaPhoneNumber(), "🚧 This feature is coming soon!");
                showMainMenu(developer);
                break;
            default:
                showMainMenu(developer);
        }
    }

    /**
     * Show Admin Management Menu
     */
    private void showAdminManagementMenu(TeamMember developer) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("ADD_ADMIN")
                                .title("Add Admin")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("SHOW_ADMINS")
                                .title("Show Admins")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("BACK_MAIN")
                                .title("Back to Main")
                                .build())
                        .build());

        whatsAppService.sendCartActionButtons(developer.getWaPhoneNumber(), "🛡️ *Admin Management*", buttons);
        developer.setCurrentAdminFlowStage(AdminFlowStage.ADMIN_MENU);
    }

    /**
     * Start Add Admin Flow
     */
    private void startAddAdminFlow(TeamMember developer) {
        whatsAppService.sendSimpleText(developer.getWaPhoneNumber(),
                "📝 *Add New Admin*\n\nPlease provide the Admin's *Name*:");
        developer.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_ADMIN_NAME);
    }

    /**
     * Handle Admin Name Input
     */
    private void handleAwaitingAdminName(TeamMember developer, String text) {
        developer.setTempTeamMemberName(text.trim());
        whatsAppService.sendSimpleText(developer.getWaPhoneNumber(),
                "Great! Now please provide the Admin's *Phone Number*:");
        developer.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_ADMIN_PHONE);
    }

    /**
     * Handle Admin Phone Input
     */
    private void handleAwaitingAdminPhone(TeamMember developer, String text) {
        developer.setTempTeamMemberPhone(text.trim());
        whatsAppService.sendSimpleText(developer.getWaPhoneNumber(),
                "Perfect! Now please provide the Admin's *WhatsApp Number* (with country code):");
        developer.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_ADMIN_WAPHONE);
    }

    /**
     * Handle Admin WhatsApp Phone Input and Create Admin
     */
    private void handleAwaitingAdminWaPhone(TeamMember developer, String text) {
        String waPhone = text.trim();

        // Check if already exists
        if (teamMemberRepository.findByWaPhoneNumber(waPhone).isPresent()) {
            whatsAppService.sendSimpleText(developer.getWaPhoneNumber(),
                    "❌ *Error*: A team member with this WhatsApp number already exists.");
            showAdminManagementMenu(developer);
            return;
        }

        TeamMember newAdmin = TeamMember.builder()
                .name(developer.getTempTeamMemberName())
                .phoneNumber(developer.getTempTeamMemberPhone())
                .waPhoneNumber(waPhone)
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        teamMemberRepository.save(newAdmin);

        // Send welcome message to new admin
        whatsAppService.sendTeamMemberWelcomeMessage(
                newAdmin.getWaPhoneNumber(),
                newAdmin.getName(),
                newAdmin.getPhoneNumber(),
                "Admin",
                developer.getName(),
                developer.getWaPhoneNumber());

        // Clear temp fields
        developer.setTempTeamMemberName(null);
        developer.setTempTeamMemberPhone(null);
        developer.setCurrentAdminFlowStage(AdminFlowStage.ADMIN_MENU);

        whatsAppService.sendSimpleText(developer.getWaPhoneNumber(),
                "✅ *Admin Added Successfully!* 🎉\n\nName: " + newAdmin.getName()
                        + "\n\nA welcome message has been sent to the new admin. 📲");

        showAdminManagementMenu(developer);
    }

    /**
     * Show All Admins
     */
    private void showAllAdmins(TeamMember developer) {
        List<TeamMember> admins = teamMemberRepository.findByRole(UserRole.ADMIN);

        if (admins.isEmpty()) {
            whatsAppService.sendSimpleText(developer.getWaPhoneNumber(), "ℹ️ No Admins found.");
        } else {
            StringBuilder message = new StringBuilder("🛡️ *List of Admins:*\n\n");
            for (int i = 0; i < admins.size(); i++) {
                TeamMember admin = admins.get(i);
                message.append(String.format("%d. *%s*\n   📞 %s\n\n", i + 1, admin.getName(), admin.getPhoneNumber()));
            }
            whatsAppService.sendSimpleText(developer.getWaPhoneNumber(), message.toString());
        }

        showAdminManagementMenu(developer);
    }
}
