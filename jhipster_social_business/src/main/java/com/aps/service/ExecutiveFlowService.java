package com.aps.service;

import com.aps.domain.BotSession;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.ExecutiveFlowStage;
import com.aps.service.admin.CustomerManagementService;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class ExecutiveFlowService {

    private final Logger log = LoggerFactory.getLogger(ExecutiveFlowService.class);

    private final WhatsAppService whatsAppService;
    private final BotSessionManager sessionManager;
    private final CustomerManagementService customerManagementService;

    public ExecutiveFlowService(@Lazy WhatsAppService whatsAppService,
            BotSessionManager sessionManager,
            CustomerManagementService customerManagementService) {
        this.whatsAppService = whatsAppService;
        this.sessionManager = sessionManager;
        this.customerManagementService = customerManagementService;
    }

    public void handleExecutiveMessage(TeamMember executive, WhatsAppWebhookDto.Message message) {
        log.info("Processing executive message from: {}", executive.getName());

        BotSession session = sessionManager.getSession(executive.getWaPhoneNumber());

        // Check for ABORT command
        if (message.getType().equals("text") && message.getText() != null) {
            String text = message.getText().getBody().trim();
            if (isAbortCommand(text)) {
                handleAbortCommand(executive, session);
                return;
            }
        }

        if (message.getType().equals("text") && message.getText() != null) {
            handleTextMessage(executive, session, message.getText().getBody());
        } else if (message.getType().equals("location") && message.getLocation() != null) {
            handleLocationMessage(executive, session, message.getLocation());
        } else if (message.getType().equals("interactive")) {
            if (message.getInteractive().getType().equals("button_reply")) {
                handleButtonReply(executive, session, message.getInteractive().getButtonReply());
            }
        }

        session.setLastActiveAt(Instant.now());
    }

    private void handleTextMessage(TeamMember executive, BotSession session, String text) {
        String stageName = session.getCurrentState();
        ExecutiveFlowStage stage;
        try {
            stage = ExecutiveFlowStage.valueOf(stageName);
        } catch (Exception e) {
            stage = ExecutiveFlowStage.IDLE;
        }

        switch (stage) {
            case IDLE:
                handleIdleState(executive, session, text);
                break;
            case AWAITING_CUST_NAME:
                customerManagementService.handleCustomerNameInput(executive, session, text);
                break;
            case AWAITING_CUST_PHONE:
                customerManagementService.handleCustomerPhoneInput(executive, session, text);
                break;
            case AWAITING_CUST_WAPHONE:
                customerManagementService.handleCustomerWaPhoneInput(executive, session, text);
                break;
            default:
                showMainMenu(executive, session);
        }
    }

    private void handleIdleState(TeamMember executive, BotSession session, String text) {
        if (text.trim().equalsIgnoreCase("hi") || text.trim().equalsIgnoreCase("hello")) {
            showMainMenu(executive, session);
        } else if (text.trim().equalsIgnoreCase("show all") || text.trim().equalsIgnoreCase("my customers")) {
            customerManagementService.showMyCustomers(executive);
            showMainMenu(executive, session);
        } else {
            whatsAppService.sendSimpleText(executive.getWaPhoneNumber(),
                    "Send 'hi' to see the executive menu.");
        }
    }

    private void showMainMenu(TeamMember executive, BotSession session) {
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
                                .id("SHOW_MY_CUSTOMERS")
                                .title("📋 My Customers")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("MY_PROFILE")
                                .title("👤 My Profile")
                                .build())
                        .build());

        whatsAppService.sendCartActionButtons(executive.getWaPhoneNumber(),
                "👋 *Welcome Executive " + executive.getName() + "*\n\nWhat would you like to do?", buttons);
        sessionManager.updateState(session, ExecutiveFlowStage.IDLE.name());
    }

    private void handleButtonReply(TeamMember executive, BotSession session,
            WhatsAppWebhookDto.ButtonReply buttonReply) {
        String buttonId = buttonReply.getId();
        switch (buttonId) {
            case "ADD_CUSTOMER":
                customerManagementService.startAddCustomer(executive, session);
                break;
            case "CONFIRM_ADD": // Re-using ID from CustomerManagementService
                if (session.getCurrentState().equals(ExecutiveFlowStage.CONFIRMING_CUST_ADD.name())) {
                    customerManagementService.finalizeCustomerAdd(executive, session);
                    showMainMenu(executive, session);
                }
                break;
            case "CANCEL_OPERATION":
                handleAbortCommand(executive, session);
                break;
            case "MY_PROFILE":
                whatsAppService.sendSimpleText(executive.getWaPhoneNumber(),
                        "👤 *Profile*\nName: " + executive.getName() + "\nRole: " + executive.getRole());
                showMainMenu(executive, session);
                break;
            case "SHOW_MY_CUSTOMERS":
                customerManagementService.showMyCustomers(executive);
                showMainMenu(executive, session);
                break;
            default:
                showMainMenu(executive, session);
        }
    }

    private void handleLocationMessage(TeamMember executive, BotSession session, WhatsAppWebhookDto.Location location) {
        // Delegate only if in correct state? CustomerManagementService checks state
        // internally but uses AdminFlowStage enum names.
        // We verified names match.
        customerManagementService.handleLocationMessage(executive, session, location);
    }

    private boolean isAbortCommand(String text) {
        return text.trim().equalsIgnoreCase("ABORT");
    }

    private void handleAbortCommand(TeamMember executive, BotSession session) {
        session.setSessionData("{}");
        sessionManager.updateState(session, ExecutiveFlowStage.IDLE.name());
        whatsAppService.sendSimpleText(executive.getWaPhoneNumber(), "❌ Operation Cancelled.");
        showMainMenu(executive, session);
    }
}
