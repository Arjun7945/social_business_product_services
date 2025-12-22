package com.aps.service;

import com.aps.config.FlowConstants;
import com.aps.domain.BotSession;
import com.aps.domain.TeamMember;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AccountsFlowService {

    private final Logger log = LoggerFactory.getLogger(AccountsFlowService.class);

    private final WhatsAppService whatsAppService;
    private final WhatsAppMediaService mediaService;
    private final BotSessionManager sessionManager;
    private final AccountsMessageService messageService;
    private final ReportService reportService;

    public AccountsFlowService(@Lazy WhatsAppService whatsAppService,
            WhatsAppMediaService mediaService,
            BotSessionManager sessionManager,
            AccountsMessageService messageService,
            ReportService reportService) {
        this.whatsAppService = whatsAppService;
        this.mediaService = mediaService;
        this.sessionManager = sessionManager;
        this.messageService = messageService;
        this.reportService = reportService;
    }

    public void handleAccountsMessage(TeamMember teamMember, WhatsAppWebhookDto.Message message) {
        log.info("Processing accounts team message from: {}", teamMember.getName());

        BotSession session = sessionManager.getSession(teamMember.getWaPhoneNumber());

        if (message.getType().equals("text") && message.getText() != null) {
            handleTextMessage(teamMember, session, message.getText().getBody());
        } else if (message.getType().equals("interactive")) {
            String type = message.getInteractive().getType();
            if ("button_reply".equals(type)) {
                handleButtonReply(teamMember, session, message.getInteractive().getButtonReply());
            } else if ("list_reply".equals(type)) {
                String id = message.getInteractive().getListReply().getId();
                WhatsAppWebhookDto.ButtonReply mockReply = new WhatsAppWebhookDto.ButtonReply();
                mockReply.setId(id);
                handleButtonReply(teamMember, session, mockReply);
            }
        }

        session.setLastActiveAt(Instant.now());
    }

    private void handleTextMessage(TeamMember teamMember, BotSession session, String text) {
        if (text.trim().equalsIgnoreCase("start") ||
                text.trim().equalsIgnoreCase("hi") ||
                text.trim().equalsIgnoreCase("menu")) {
            showMainMenu(teamMember, session);
        } else {
            whatsAppService.sendSimpleText(teamMember.getWaPhoneNumber(),
                    "Send 'start' to see the accounts dashboard.");
        }
    }

    private void showMainMenu(TeamMember teamMember, BotSession session) {
        List<WhatsAppMessageDto.RowDto> rows = List.of(
                WhatsAppMessageDto.RowDto.builder()
                        .id(FlowConstants.BTN_ACCOUNTS_TODAYS_ORDERS)
                        .title(messageService.getButtonTodaysOrders())
                        .description("Generate PDF for today")
                        .build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id(FlowConstants.BTN_ACCOUNTS_COMPLETED_ORDERS)
                        .title(messageService.getButtonCompletedOrders())
                        .description("Delivered orders report")
                        .build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id(FlowConstants.BTN_ACCOUNTS_UNPAID_ORDERS)
                        .title(messageService.getButtonUnpaidOrders())
                        .description("Pending payment report")
                        .build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id(FlowConstants.BTN_ACCOUNTS_CREDIT_REPORT)
                        .title(messageService.getButtonCreditReport())
                        .description("Credit customer details")
                        .build());

        whatsAppService.sendInteractiveList(
                teamMember.getWaPhoneNumber(),
                messageService.getAccountsWelcomeMessage(teamMember.getName()),
                rows);

        sessionManager.updateState(session, "IDLE");
    }

    private void handleButtonReply(TeamMember teamMember, BotSession session,
            WhatsAppWebhookDto.ButtonReply buttonReply) {
        String buttonId = buttonReply.getId();
        log.info("Accounts button reply: {}", buttonId);

        if (buttonId.equals(FlowConstants.BTN_ACCOUNTS_TODAYS_ORDERS)) {
            generateAndSendReport(teamMember, session, "Todays_Orders_" + LocalDate.now(),
                    "📅 Today's Orders Report",
                    "ℹ️ No orders found for today.",
                    reportService::generateTodaysOrdersReport);
        } else if (buttonId.equals(FlowConstants.BTN_ACCOUNTS_COMPLETED_ORDERS)) {
            generateAndSendReport(teamMember, session, "Completed_Orders",
                    "✅ Completed Orders Report",
                    "ℹ️ No completed orders found.",
                    reportService::generateCompletedOrdersReport);
        } else if (buttonId.equals(FlowConstants.BTN_ACCOUNTS_UNPAID_ORDERS)) {
            generateAndSendReport(teamMember, session, "Unpaid_Orders",
                    "💰 Unpaid Orders Report",
                    "ℹ️ No unpaid orders found.",
                    reportService::generateUnpaidOrdersReport);
        } else if (buttonId.equals(FlowConstants.BTN_ACCOUNTS_CREDIT_REPORT)) {
            whatsAppService.sendSimpleText(teamMember.getWaPhoneNumber(),
                    messageService.getFeatureComingSoon());
            showMainMenu(teamMember, session);
        } else {
            showMainMenu(teamMember, session);
        }
    }

    private interface ReportGenerator {
        byte[] generate(String issueToName);
    }

    private void generateAndSendReport(TeamMember member, BotSession session, String baseFilename, String caption,
            String noDataMessage,
            ReportGenerator generator) {
        whatsAppService.sendSimpleText(member.getWaPhoneNumber(), "⏳ Generating report... please wait.");

        try {
            byte[] pdfBytes = generator.generate(member.getName());
            if (pdfBytes != null && pdfBytes.length > 0) {
                String mediaId = mediaService.uploadImage(pdfBytes, "application/pdf");
                if (mediaId != null) {
                    String filename = baseFilename + ".pdf";
                    whatsAppService.sendDocument(member.getWaPhoneNumber(), mediaId, filename, caption);
                } else {
                    whatsAppService.sendSimpleText(member.getWaPhoneNumber(), "❌ Failed to upload report.");
                }
            } else {
                whatsAppService.sendSimpleText(member.getWaPhoneNumber(), noDataMessage);
            }
        } catch (Exception e) {
            log.error("Report generation error", e);
            whatsAppService.sendSimpleText(member.getWaPhoneNumber(), "❌ Error generating report: " + e.getMessage());
        } finally {
            showMainMenu(member, session);
        }
    }
}
