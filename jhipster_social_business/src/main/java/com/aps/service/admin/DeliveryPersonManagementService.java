package com.aps.service.admin;

import com.aps.domain.BotSession;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AdminFlowStage;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.TeamMemberRepository;
import com.aps.service.BotSessionManager;
import com.aps.service.UserRemovalService; // Added
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.util.InputValidator;
import java.util.List;
import java.util.Optional; // Added
import org.slf4j.Logger; // Added
import org.slf4j.LoggerFactory; // Added
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class DeliveryPersonManagementService {

    private final Logger log = LoggerFactory.getLogger(DeliveryPersonManagementService.class);

    private final TeamMemberRepository teamMemberRepository;
    private final WhatsAppService whatsAppService;
    private final BotSessionManager sessionManager;
    private final InputValidator inputValidator;
    private final UserRemovalService userRemovalService; // Added

    public DeliveryPersonManagementService(
        TeamMemberRepository teamMemberRepository,
        WhatsAppService whatsAppService,
        BotSessionManager sessionManager,
        InputValidator inputValidator,
        UserRemovalService userRemovalService
    ) { // Added
        this.teamMemberRepository = teamMemberRepository;
        this.whatsAppService = whatsAppService;
        this.sessionManager = sessionManager;
        this.inputValidator = inputValidator;
        this.userRemovalService = userRemovalService;
    }

    public void showDeliveryPersonMenu(TeamMember admin) {
        // Fetch session if not passed (following CustomManagementService pattern)
        BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());

        List<WhatsAppMessageDto.RowDto> rows = List.of(
            WhatsAppMessageDto.RowDto.builder()
                .id("ADD_DELIVERY")
                .title("➕ Add Delivery Person")
                .description("Add a new staff member")
                .build(),
            WhatsAppMessageDto.RowDto.builder().id("SHOW_ALL_DELIVERY").title("📋 Show All").description("List all delivery staff").build(),
            WhatsAppMessageDto.RowDto.builder()
                .id("DELETE_DELIVERY_MENU")
                .title("🗑️ Delete")
                .description("Remove a delivery person")
                .build(),
            WhatsAppMessageDto.RowDto.builder().id("BACK_TO_MAIN").title("⬅️ Back").description("Return to main menu").build()
        );

        whatsAppService.sendInteractiveList(admin.getWaPhoneNumber(), "🚚 *Delivery Person Management*\n\nSelect an option:", rows);

        sessionManager.updateState(session, AdminFlowStage.DELIVERY_MENU.name());
    }

    public void startAddDeliveryPerson(TeamMember admin, BotSession session) {
        sessionManager.setSessionData(session, "tempEntityType", "DELIVERY");
        whatsAppService.sendSimpleText(
            admin.getWaPhoneNumber(),
            "➕ *Add New Delivery Person*\n\n📝 Please provide the delivery person's name:"
        );
        sessionManager.updateState(session, AdminFlowStage.AWAITING_DELIVERY_NAME.name());
    }

    public void handleDeliveryPersonNameInput(TeamMember admin, BotSession session, String name) {
        if (!inputValidator.isValidName(name)) {
            whatsAppService.sendSimpleText(
                admin.getWaPhoneNumber(),
                "❌ Invalid name. Use letters/spaces/dots only (min 2 chars). Try again:"
            );
            return;
        }
        sessionManager.setSessionData(session, "tempTeamMemberName", name.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "✅ Name: " + name.trim() + "\n\n📞 Please provide the phone number:");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_DELIVERY_PHONE.name());
    }

    public void handleDeliveryPersonPhoneInput(TeamMember admin, BotSession session, String phone) {
        if (!inputValidator.isValidPhoneNumber(phone)) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid phone number format. Try again:");
            return;
        }
        sessionManager.setSessionData(session, "tempTeamMemberPhone", phone.trim());
        whatsAppService.sendSimpleText(
            admin.getWaPhoneNumber(),
            "✅ Phone: " + phone.trim() + "\n\n📱 Please provide the WhatsApp number (with country code):"
        );
        sessionManager.updateState(session, AdminFlowStage.AWAITING_DELIVERY_WAPHONE.name());
    }

    public void handleDeliveryPersonWaPhoneInput(TeamMember admin, BotSession session, String waPhone) {
        if (!inputValidator.isValidPhoneNumber(waPhone)) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid WhatsApp number format. Try again:");
            return;
        }
        sessionManager.setSessionData(session, "tempTeamMemberWaPhone", waPhone.trim());
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
            WhatsAppMessageDto.ButtonDto.builder()
                .type("reply")
                .reply(WhatsAppMessageDto.ReplyDto.builder().id("DELIVERY_ACTIVE_YES").title("Yes").build())
                .build(),
            WhatsAppMessageDto.ButtonDto.builder()
                .type("reply")
                .reply(WhatsAppMessageDto.ReplyDto.builder().id("DELIVERY_ACTIVE_NO").title("No").build())
                .build()
        );

        whatsAppService.sendCartActionButtons(
            admin.getWaPhoneNumber(),
            "✅ WhatsApp: " + waPhone.trim() + "\n\n🔄 Is this delivery person active?",
            buttons
        );
        sessionManager.updateState(session, AdminFlowStage.AWAITING_DELIVERY_STATUS.name());
    }

    public void handleDeliveryPersonStatusInput(TeamMember admin, BotSession session, String status) {
        boolean isActive = status.trim().equalsIgnoreCase("yes") || status.trim().equalsIgnoreCase("y");
        sessionManager.setSessionData(session, "tempTeamMemberIsActive", String.valueOf(isActive));

        String tempName = sessionManager.getSessionDataString(session, "tempTeamMemberName");
        String tempPhone = sessionManager.getSessionDataString(session, "tempTeamMemberPhone");
        String tempWaPhone = sessionManager.getSessionDataString(session, "tempTeamMemberWaPhone");

        String summary = String.format(
            "✅ *Delivery Person Details Summary:*\n\n" +
            "👤 Name: %s\n" +
            "📞 Phone: %s\n" +
            "📱 WhatsApp: %s\n" +
            "🔄 Status: %s\n\n" +
            "Confirm to add this delivery person?",
            tempName,
            tempPhone,
            tempWaPhone,
            isActive ? "Active" : "Inactive"
        );

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
        sessionManager.updateState(session, AdminFlowStage.CONFIRMING_DELIVERY_ADD.name());
    }

    @Async
    @Transactional
    public void finalizeDeliveryPersonAdd(TeamMember admin, BotSession session) {
        String tempName = sessionManager.getSessionDataString(session, "tempTeamMemberName");
        String tempPhone = sessionManager.getSessionDataString(session, "tempTeamMemberPhone");
        String tempWaPhone = sessionManager.getSessionDataString(session, "tempTeamMemberWaPhone");
        String tempIsActiveStr = sessionManager.getSessionDataString(session, "tempTeamMemberIsActive");
        boolean tempIsActive = Boolean.parseBoolean(tempIsActiveStr);

        try {
            TeamMember newDeliveryPerson = new TeamMember();
            newDeliveryPerson.setName(tempName);
            newDeliveryPerson.setPhoneNumber(tempPhone);
            newDeliveryPerson.setWaPhoneNumber(tempWaPhone);
            newDeliveryPerson.setRole(UserRole.DELIVERY_PERSON);
            newDeliveryPerson.setIsActive(tempIsActive);

            teamMemberRepository.saveAndFlush(newDeliveryPerson);

            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        // Note: sendTeamMemberWelcomeMessage call differs slightly in args from legacy
                        whatsAppService.sendTeamMemberWelcomeMessage(
                            newDeliveryPerson.getWaPhoneNumber(),
                            newDeliveryPerson.getName(),
                            newDeliveryPerson.getPhoneNumber(),
                            "Delivery Person",
                            admin.getName(),
                            admin.getWaPhoneNumber()
                        );

                        whatsAppService.sendSimpleText(
                            admin.getWaPhoneNumber(),
                            "✅ *Delivery Person Added Successfully!*\n\n" +
                            "👤 " +
                            newDeliveryPerson.getName() +
                            " has been added to the system.\n\n" +
                            "A welcome message has been sent to the new delivery person. 📲"
                        );
                        showDeliveryPersonMenu(admin);
                    }
                }
            );
        } catch (Exception e) {
            e.printStackTrace();
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error adding delivery person: " + e.getMessage());
            showDeliveryPersonMenu(admin);
        }
    }

    public void showAllDeliveryPersons(TeamMember admin) {
        List<TeamMember> deliveryPersons = teamMemberRepository
            .findAll()
            .stream()
            .filter(tm -> tm.getRole() == UserRole.DELIVERY_PERSON)
            .toList();

        if (deliveryPersons.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "📋 *No delivery persons found.*");
            showDeliveryPersonMenu(admin);
            return;
        }

        StringBuilder message = new StringBuilder(String.format("📋 *All Delivery Persons* (Total: %d)\n\n", deliveryPersons.size()));
        int count = 1;
        for (TeamMember dp : deliveryPersons) {
            message.append(
                String.format(
                    "%d. *%s* (ID: %d)\n   📞 %s\n   📱 %s\n   🔄 %s\n\n",
                    count++,
                    dp.getName(),
                    dp.getId(),
                    dp.getPhoneNumber(),
                    dp.getWaPhoneNumber(),
                    dp.getIsActive() != null && dp.getIsActive() ? "Active" : "Inactive"
                )
            );
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        showDeliveryPersonMenu(admin);
    }

    public void startUpdateDeliveryPerson(TeamMember admin, BotSession session) {
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "🚧 *Update Delivery Person*\n\nThis feature is coming soon!");
        showDeliveryPersonMenu(admin);
    }

    // --- Deletion Logic ---

    public void startDeleteDeliveryPerson(TeamMember admin, BotSession session) {
        whatsAppService.sendSimpleText(
            admin.getWaPhoneNumber(),
            "🗑️ *Delete Delivery Person*\n\n⚠️ This will Archive & Remove the user.\n" +
            "🆔 Please enter the **Delivery Person ID** you wish to delete:"
        );

        sessionManager.updateState(session, AdminFlowStage.AWAITING_DELETE_DELIVERY_ID.name());
    }

    public void handleDeleteDeliveryPersonInput(TeamMember admin, BotSession session, String text) {
        try {
            Long id = Long.parseLong(text.trim());
            Optional<TeamMember> memberOpt = teamMemberRepository.findById(id);

            if (memberOpt.isEmpty() || memberOpt.get().getRole() != UserRole.DELIVERY_PERSON) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ ID not found or not a Delivery Person. Try again:");
                return;
            }

            TeamMember member = memberOpt.get();

            List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                    .type("reply")
                    .reply(WhatsAppMessageDto.ReplyDto.builder().id("CONFIRM_DELETE_DP_" + id).title("💥 Yes, DELETE").build())
                    .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                    .type("reply")
                    .reply(WhatsAppMessageDto.ReplyDto.builder().id("CANCEL_OPERATION").title("❌ Cancel").build())
                    .build()
            );

            whatsAppService.sendCartActionButtons(
                admin.getWaPhoneNumber(),
                String.format(
                    "⚠️ *Confirm Deletion*\n\n" + "Are you SURE you want to delete:\n" + "👤 *%s* (ID: %d)?",
                    member.getName(),
                    id
                ),
                buttons
            );
            // We rely on button click routing logic in AdminFlowService or add logic there.
            // Wait, AdminFlowService needs to handle `CONFIRM_DELETE_DP_...`
            // I need to update AdminFlowService button handler to genericize handling or
            // add this specific case.

        } catch (NumberFormatException e) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid ID format. Please enter a number:");
        }
    }

    public void finalizeDeliveryPersonDelete(TeamMember admin, Long id) {
        try {
            userRemovalService.removeDeliveryPerson(id, "Admin " + admin.getName() + " requested via WhatsApp");
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "✅ Delivery Person Deleted Successfully.");
        } catch (Exception e) {
            log.error("Delete failed", e);
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Delete Failed: " + e.getMessage());
        }
        showDeliveryPersonMenu(admin);
    }
}
