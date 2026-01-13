package com.aps.service.admin;

import com.aps.domain.BotSession;
import com.aps.domain.DeliveryPerson;
import com.aps.domain.DeliveryZone;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AdminFlowStage;
import com.aps.domain.enumeration.DeliveryStatus;
import com.aps.repository.DeliveryPersonRepository;
import com.aps.repository.DeliveryZoneRepository;
import com.aps.service.BotSessionManager;
import com.aps.service.UserRemovalService;
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppMessageDto;
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
public class DeliveryPersonManagementService {

        private final Logger log = LoggerFactory.getLogger(DeliveryPersonManagementService.class);

        private final DeliveryPersonRepository deliveryPersonRepository;
        private final DeliveryZoneRepository deliveryZoneRepository;
        private final WhatsAppService whatsAppService;
        private final BotSessionManager sessionManager;
        private final InputValidator inputValidator;
        private final UserRemovalService userRemovalService;
        private final com.aps.service.AdminMessageService adminMessageService;
        private final com.aps.repository.CustomerOrderRepository customerOrderRepository;

        public DeliveryPersonManagementService(
                        DeliveryPersonRepository deliveryPersonRepository,
                        DeliveryZoneRepository deliveryZoneRepository,
                        WhatsAppService whatsAppService,
                        BotSessionManager sessionManager,
                        InputValidator inputValidator,
                        UserRemovalService userRemovalService,
                        com.aps.service.AdminMessageService adminMessageService,
                        com.aps.repository.CustomerOrderRepository customerOrderRepository) {
                this.deliveryPersonRepository = deliveryPersonRepository;
                this.deliveryZoneRepository = deliveryZoneRepository;
                this.whatsAppService = whatsAppService;
                this.sessionManager = sessionManager;
                this.inputValidator = inputValidator;
                this.userRemovalService = userRemovalService;
                this.adminMessageService = adminMessageService;
                this.customerOrderRepository = customerOrderRepository;
        }

        public void showDeliveryPersonMenu(TeamMember admin) {
                BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());

                List<WhatsAppMessageDto.RowDto> rows = com.aps.service.util.DeliveryMenuHelper
                                .getDeliveryPersonMenuRows();

                whatsAppService.sendInteractiveList(admin.getWaPhoneNumber(),
                                "🚚 *Delivery Person Management*\n\nSelect an option:", rows);

                sessionManager.updateState(session, AdminFlowStage.DELIVERY_MENU.name());
        }

        public void startAddDeliveryPerson(TeamMember admin, BotSession session) {
                sessionManager.setSessionData(session, "tempEntityType", "DELIVERY");
                whatsAppService.sendSimpleText(
                                admin.getWaPhoneNumber(),
                                "➕ *Add New Delivery Person*\n\n📝 Please provide the delivery person's name:");
                sessionManager.updateState(session, AdminFlowStage.AWAITING_DELIVERY_NAME.name());
        }

        public void handleDeliveryPersonNameInput(TeamMember admin, BotSession session, String name) {
                if (!inputValidator.isValidName(name)) {
                        whatsAppService.sendSimpleText(
                                        admin.getWaPhoneNumber(),
                                        "❌ Invalid name. Use letters/spaces/dots only (min 2 chars). Try again:");
                        return;
                }
                sessionManager.setSessionData(session, "tempTeamMemberName", name.trim());
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "✅ Name: " + name.trim() + "\n\n📞 Please provide the phone number:");
                sessionManager.updateState(session, AdminFlowStage.AWAITING_DELIVERY_PHONE.name());
        }

        public void handleDeliveryPersonPhoneInput(TeamMember admin, BotSession session, String phone) {
                if (!inputValidator.isValidPhoneNumber(phone)) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Invalid phone number format. Try again:");
                        return;
                }
                sessionManager.setSessionData(session, "tempTeamMemberPhone", phone.trim());
                whatsAppService.sendSimpleText(
                                admin.getWaPhoneNumber(),
                                "✅ Phone: " + phone.trim()
                                                + "\n\n📱 Please provide the WhatsApp number (with country code):");
                sessionManager.updateState(session, AdminFlowStage.AWAITING_DELIVERY_WAPHONE.name());
        }

        public void handleDeliveryPersonWaPhoneInput(TeamMember admin, BotSession session, String waPhone) {
                if (!inputValidator.isValidPhoneNumber(waPhone)) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Invalid WhatsApp number format. Try again:");
                        return;
                }
                sessionManager.setSessionData(session, "tempTeamMemberWaPhone", waPhone.trim());
                List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder().id("DELIVERY_ACTIVE_YES")
                                                                .title("Yes").build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder().id("DELIVERY_ACTIVE_NO")
                                                                .title("No").build())
                                                .build());

                whatsAppService.sendCartActionButtons(
                                admin.getWaPhoneNumber(),
                                "✅ WhatsApp: " + waPhone.trim() + "\n\n🔄 Is this delivery person active?",
                                buttons);
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
                                isActive ? "Active" : "Inactive");

                List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder().id("CONFIRM_ADD")
                                                                .title("✅ Confirm & Add").build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder().id("CANCEL_OPERATION")
                                                                .title("❌ Cancel").build())
                                                .build());

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
                        DeliveryPerson newDeliveryPerson = new DeliveryPerson();
                        newDeliveryPerson.setName(tempName);
                        newDeliveryPerson.setPhoneNumber(tempPhone);
                        newDeliveryPerson.setWaPhoneNumber(tempWaPhone);
                        newDeliveryPerson.setIsActive(tempIsActive);
                        newDeliveryPerson.setAddedBy(admin); // Link addedBy field
                        newDeliveryPerson.setStatus(DeliveryStatus.FREE); // Default status
                        newDeliveryPerson.setJoinedAt(Instant.now());

                        // TODO: Zone assignment - for now, get the first available zone or create a
                        // default one
                        // This should be collected from admin during the add flow
                        DeliveryZone defaultZone = deliveryZoneRepository.findAll().stream().findFirst()
                                        .orElseThrow(() -> new RuntimeException(
                                                        "No delivery zones available. Please create a delivery zone first."));
                        newDeliveryPerson.setZone(defaultZone);

                        deliveryPersonRepository.saveAndFlush(newDeliveryPerson);

                        TransactionSynchronizationManager.registerSynchronization(
                                        new TransactionSynchronization() {
                                                @Override
                                                public void afterCommit() {
                                                        whatsAppService.sendTeamMemberWelcomeMessage(
                                                                        newDeliveryPerson.getWaPhoneNumber(),
                                                                        newDeliveryPerson.getName(),
                                                                        newDeliveryPerson.getPhoneNumber(),
                                                                        "Delivery Person",
                                                                        admin.getName(),
                                                                        admin.getWaPhoneNumber());

                                                        whatsAppService.sendSimpleText(
                                                                        admin.getWaPhoneNumber(),
                                                                        "✅ *Delivery Person Added Successfully!*\n\n" +
                                                                                        "👤 " +
                                                                                        newDeliveryPerson.getName() +
                                                                                        " has been added to the system.\n\n"
                                                                                        +
                                                                                        "A welcome message has been sent to the new delivery person. 📲");
                                                        showDeliveryPersonMenu(admin);
                                                }
                                        });
                } catch (Exception e) {
                        log.error("Error adding delivery person", e);
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Error adding delivery person: " + e.getMessage());
                        showDeliveryPersonMenu(admin);
                }
        }

        public void showAllDeliveryPersons(TeamMember admin) {
                List<DeliveryPerson> deliveryPersons = deliveryPersonRepository.findAll();

                if (deliveryPersons.isEmpty()) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "📋 *No delivery persons found.*");
                        showDeliveryPersonMenu(admin);
                        return;
                }

                StringBuilder message = new StringBuilder(
                                String.format("📋 *All Delivery Persons* (Total: %d)\n\n", deliveryPersons.size()));
                int count = 1;
                for (DeliveryPerson dp : deliveryPersons) {
                        message.append(
                                        String.format(
                                                        "%d. *%s* (ID: %d)\n   📞 %s\n   📱 %s\n   🔄 %s\n\n",
                                                        count++,
                                                        dp.getName(),
                                                        dp.getId(),
                                                        dp.getPhoneNumber(),
                                                        dp.getWaPhoneNumber(),
                                                        dp.getIsActive() != null && dp.getIsActive() ? "Active"
                                                                        : "Inactive"));
                }

                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
                showDeliveryPersonMenu(admin);
        }

        public void startUpdateDeliveryPerson(TeamMember admin, BotSession session) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                "🚧 *Update Delivery Person*\n\nThis feature is coming soon!");
                showDeliveryPersonMenu(admin);
        }

        // --- Deletion Logic ---

        // --- Deletion Logic ---

        public void startDeleteDeliveryPerson(TeamMember admin, BotSession session) {
                // Fix N+1 problem by fetching eager relationships
                List<DeliveryPerson> deliveryPersons = deliveryPersonRepository.findAllWithEagerRelationships();

                if (deliveryPersons.isEmpty()) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        adminMessageService.getNoDeliveryPersonsFound());
                        showDeliveryPersonMenu(admin);
                        return;
                }

                // Prepare list of pending statuses to check
                List<com.aps.domain.enumeration.OrderStatus> pendingStatuses = java.util.Arrays
                                .stream(com.aps.domain.enumeration.OrderStatus.values())
                                .filter(s -> s != com.aps.domain.enumeration.OrderStatus.ORDER_DELIVERED_SUCESSFULLY
                                                && s != com.aps.domain.enumeration.OrderStatus.ORDER_FAILED
                                                && s != com.aps.domain.enumeration.OrderStatus.ORDER_NOT_TAKEN)
                                .collect(java.util.stream.Collectors.toList());

                List<WhatsAppMessageDto.RowDto> rows = deliveryPersons.stream()
                                .map(dp -> {
                                        Long pendingOrders = customerOrderRepository
                                                        .countByDeliveryPersonIdAndStatusIn(dp.getId(),
                                                                        pendingStatuses);
                                        return WhatsAppMessageDto.RowDto.builder()
                                                        .id("DELETE_DP_" + dp.getId())
                                                        .title(dp.getName())
                                                        .description(String.format(
                                                                        "ID: %d | Zone: %s | Pending Orders: %d",
                                                                        dp.getId(),
                                                                        dp.getZone() != null
                                                                                        ? dp.getZone().getZoneName()
                                                                                        : "N/A",
                                                                        pendingOrders))
                                                        .build();
                                })
                                .collect(java.util.stream.Collectors.toList());

                whatsAppService.sendInteractiveList(
                                admin.getWaPhoneNumber(),
                                adminMessageService.getDeleteDeliveryPersonHeader(),
                                "View List",
                                rows);

                sessionManager.updateState(session, AdminFlowStage.AWAITING_DELETE_DELIVERY_SELECTION.name());
        }

        public void handleDeleteDeliveryPersonSelection(TeamMember admin, BotSession session, String selectionId) {
                if (selectionId.equals("CANCEL_OPERATION")) {
                        showDeliveryPersonMenu(admin);
                        return;
                }

                if (selectionId.equals("REPORT_ISSUE")) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        adminMessageService.getReportFeatureLocked());
                        showDeliveryPersonMenu(admin);
                        return;
                }

                if (selectionId.equals("GO_BACK_TO_LIST")) {
                        startDeleteDeliveryPerson(admin, session);
                        return;
                }

                try {
                        // Expected format "DELETE_DP_{id}"
                        String idStr = selectionId.replace("DELETE_DP_", "");
                        Long id = Long.parseLong(idStr);

                        Optional<DeliveryPerson> memberOpt = deliveryPersonRepository.findOneWithToOneRelationships(id);

                        if (memberOpt.isEmpty()) {
                                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                                "❌ Selected Delivery Person not found.");
                                showDeliveryPersonMenu(admin);
                                return;
                        }

                        DeliveryPerson member = memberOpt.get();

                        // Check for pending orders logic
                        List<com.aps.domain.enumeration.OrderStatus> pendingStatuses = java.util.Arrays
                                        .stream(com.aps.domain.enumeration.OrderStatus.values())
                                        .filter(s -> s != com.aps.domain.enumeration.OrderStatus.ORDER_DELIVERED_SUCESSFULLY
                                                        && s != com.aps.domain.enumeration.OrderStatus.ORDER_FAILED
                                                        && s != com.aps.domain.enumeration.OrderStatus.ORDER_NOT_TAKEN)
                                        .collect(java.util.stream.Collectors.toList());

                        Long pendingCount = customerOrderRepository.countByDeliveryPersonIdAndStatusIn(id,
                                        pendingStatuses);

                        if (pendingCount > 0) {
                                // Block Deletion
                                List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                                                WhatsAppMessageDto.ButtonDto.builder()
                                                                .type("reply")
                                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                                .id("GO_BACK_TO_LIST")
                                                                                .title(adminMessageService
                                                                                                .getButtonGoBack())
                                                                                .build())
                                                                .build(),
                                                WhatsAppMessageDto.ButtonDto.builder()
                                                                .type("reply")
                                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                                .id("REPORT_ISSUE")
                                                                                .title(adminMessageService
                                                                                                .getButtonReport())
                                                                                .build())
                                                                .build());

                                whatsAppService.sendCartActionButtons(
                                                admin.getWaPhoneNumber(),
                                                adminMessageService.getPendingOrdersWarning(member.getName(),
                                                                pendingCount),
                                                buttons);
                                return;
                        }

                        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                                        WhatsAppMessageDto.ButtonDto.builder()
                                                        .type("reply")
                                                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                        .id("CONFIRM_DELETE_DP_" + id)
                                                                        .title(adminMessageService
                                                                                        .getButtonConfirmDelete())
                                                                        .build())
                                                        .build(),
                                        WhatsAppMessageDto.ButtonDto.builder()
                                                        .type("reply")
                                                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                        .id("CANCEL_OPERATION")
                                                                        .title(adminMessageService.getButtonCancel())
                                                                        .build())
                                                        .build());

                        String summary = adminMessageService.getConfirmDeletionHeader(
                                        member.getName(),
                                        member.getId(),
                                        member.getZone() != null ? member.getZone().getZoneName() : "N/A",
                                        member.getWaPhoneNumber());

                        whatsAppService.sendCartActionButtons(
                                        admin.getWaPhoneNumber(),
                                        summary,
                                        buttons);

                } catch (NumberFormatException e) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Invalid Selection.");
                        showDeliveryPersonMenu(admin);
                }
        }

        public void finalizeDeliveryPersonDelete(TeamMember admin, Long id) {
                try {
                        userRemovalService.removeDeliveryPerson(id,
                                        "Admin " + admin.getName() + " requested via WhatsApp");
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        adminMessageService.getDeliveryPersonDeletedSuccess());
                } catch (Exception e) {
                        log.error("Delete failed", e);
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        adminMessageService.getDeleteFailed(e.getMessage()));
                }
                showDeliveryPersonMenu(admin);
        }
}
