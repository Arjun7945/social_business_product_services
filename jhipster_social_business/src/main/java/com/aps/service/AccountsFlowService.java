package com.aps.service;

import com.aps.config.FlowConstants;
import com.aps.domain.BotSession;
import com.aps.domain.TeamMember;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountsFlowService {

    private final Logger log = LoggerFactory.getLogger(AccountsFlowService.class);

    private final WhatsAppService whatsAppService;
    private final WhatsAppMediaService mediaService;
    private final BotSessionManager sessionManager;
    private final AccountsMessageService messageService;
    private final ReportService reportService;

    public AccountsFlowService(
            @Lazy WhatsAppService whatsAppService,
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
        if (text.trim().equalsIgnoreCase("start") || text.trim().equalsIgnoreCase("hi")
                || text.trim().equalsIgnoreCase("menu")) {
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
                        .description("Generate daily report")
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
            askForReportFormat(teamMember, session, "TODAYS");
        } else if (buttonId.equals(FlowConstants.BTN_ACCOUNTS_COMPLETED_ORDERS)) {
            askForReportFormat(teamMember, session, "COMPLETED");
        } else if (buttonId.equals(FlowConstants.BTN_ACCOUNTS_UNPAID_ORDERS)) {
            askForReportFormat(teamMember, session, "UNPAID");
        } else if (buttonId.equals(FlowConstants.BTN_ACCOUNTS_CREDIT_REPORT)) {
            askForReportFormat(teamMember, session, "CREDIT");
        } else if (buttonId.equals(FlowConstants.BTN_FMT_PDF) || buttonId.equals(FlowConstants.BTN_FMT_EXCEL)) {
            handleFormatSelection(teamMember, session, buttonId);
        } else {
            showMainMenu(teamMember, session);
        }
    }

    private void askForReportFormat(TeamMember teamMember, BotSession session, String reportType) {
        sessionManager.setSessionData(session, "PENDING_REPORT", reportType);

        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id(FlowConstants.BTN_FMT_PDF)
                                .title("📄 PDF Document").build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id(FlowConstants.BTN_FMT_EXCEL)
                                .title("📊 Excel Sheet").build())
                        .build());

        whatsAppService.sendCartActionButtons(teamMember.getWaPhoneNumber(),
                messageService.getReportFormatSelectionMessage(), buttons);
    }

    private void handleFormatSelection(TeamMember teamMember, BotSession session, String formatButtonId) {
        String reportType = sessionManager.getSessionDataString(session, "PENDING_REPORT");
        if (reportType == null) {
            whatsAppService.sendSimpleText(teamMember.getWaPhoneNumber(),
                    "❌ Session expired or invalid. Please select report again.");
            showMainMenu(teamMember, session);
            return;
        }

        String format = formatButtonId.equals(FlowConstants.BTN_FMT_EXCEL) ? "EXCEL" : "PDF";
        String extension = format.equals("EXCEL") ? ".xlsx" : ".pdf";
        String mimeType = format.equals("EXCEL") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "application/pdf";

        sessionManager.setSessionData(session, "PENDING_REPORT", null); // Clear state

        switch (reportType) {
            case "TODAYS":
                generateAndSendReport(
                        teamMember,
                        session,
                        "Todays_Orders",
                        "📅 Today's Orders Report",
                        "ℹ️ No orders found for today.",
                        name -> reportService.generateTodaysOrdersReport(name, format),
                        extension,
                        mimeType);
                break;
            case "COMPLETED":
                generateAndSendReport(
                        teamMember,
                        session,
                        "Completed_Orders",
                        "✅ Completed Orders Report",
                        "ℹ️ No completed orders found.",
                        name -> reportService.generateCompletedOrdersReport(name, format),
                        extension,
                        mimeType);
                break;
            case "UNPAID":
                generateAndSendReport(
                        teamMember,
                        session,
                        "Unpaid_Orders",
                        "💰 Unpaid Orders Report",
                        "ℹ️ No unpaid orders found.",
                        name -> reportService.generateUnpaidOrdersReport(name, format),
                        extension,
                        mimeType);
                break;
            case "CREDIT":
                generateAndSendReport(
                        teamMember,
                        session,
                        "Credit_Orders",
                        messageService.getCreditReportCaption(),
                        messageService.getNoCreditOrdersMessage(),
                        name -> reportService.generateCreditOrdersReport(name, format),
                        extension,
                        mimeType);
                break;
            default:
                showMainMenu(teamMember, session);
        }
    }

    private interface ReportGenerator {
        byte[] generate(String issueToName);
    }

    private void generateAndSendReport(
            TeamMember member,
            BotSession session,
            String baseFilename,
            String caption,
            String noDataMessage,
            ReportGenerator generator,
            String extension,
            String mimeType) {
        whatsAppService.sendSimpleText(member.getWaPhoneNumber(), "⏳ Generating report... please wait.");

        try {
            byte[] fileBytes = generator.generate(member.getName());
            if (fileBytes != null && fileBytes.length > 0) {
                String mediaId = mediaService.uploadImage(fileBytes, mimeType);
                if (mediaId != null) {
                    String filename = baseFilename + "_" + LocalDate.now() + extension;
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
