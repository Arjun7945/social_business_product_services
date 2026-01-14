package com.aps.service;

import com.aps.domain.TeamMember;
import com.aps.repository.TeamMemberRepository;
import com.aps.domain.enumeration.UserRole;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DailyOrderReportService {

    private final Logger log = LoggerFactory.getLogger(DailyOrderReportService.class);

    private final ReportService reportService;
    private final TeamMemberRepository teamMemberRepository;
    private final WhatsAppService whatsAppService;
    private final WhatsAppMediaService whatsAppMediaService;

    public DailyOrderReportService(ReportService reportService,
            TeamMemberRepository teamMemberRepository,
            WhatsAppService whatsAppService,
            WhatsAppMediaService whatsAppMediaService) {
        this.reportService = reportService;
        this.teamMemberRepository = teamMemberRepository;
        this.whatsAppService = whatsAppService;
        this.whatsAppMediaService = whatsAppMediaService;
    }

    /**
     * Report generation scheduled for 3 PM (Default) every day.
     * Cron format: second, minute, hour, day of month, month, day(s) of week
     */
    @Scheduled(cron = "0 0 15 * * ?") // 3:00 PM
    // @Scheduled(cron = "0 30 15 * * ?") // 3:30 PM
    // @Scheduled(cron = "0 0 16 * * ?") // 4:00 PM
    // @Scheduled(cron = "0 30 16 * * ?") // 4:30 PM
    // @Scheduled(cron = "0 0 17 * * ?") // 5:00 PM
    // @Scheduled(cron = "0 30 17 * * ?") // 5:30 PM
    // @Scheduled(cron = "0 0 18 * * ?") // 6:00 PM
    // @Scheduled(cron = "0 30 18 * * ?") // 6:30 PM
    // @Scheduled(cron = "0 0 19 * * ?") // 7:00 PM
    // @Scheduled(cron = "0 30 19 * * ?") // 7:30 PM
    // @Scheduled(cron = "0 0 20 * * ?") // 8:00 PM
    public void generateAndSendDailyReport() {
        log.info("Starting scheduled daily order report generation.");

        try {
            // 1. Generate Report
            byte[] reportBytes = reportService.generateTodaysOrdersReport("Accounts Team", "EXCEL");

            if (reportBytes == null || reportBytes.length == 0) {
                log.info("No orders found for today. Skipping report generation.");
                return;
            }

            String dateStr = LocalDate.now().toString();
            String fileName = "Daily_Orders_" + dateStr + ".xlsx";

            // 2. Upload Report to WhatsApp
            String reportMediaId = whatsAppMediaService.uploadDocument(reportBytes,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", fileName);

            if (reportMediaId == null) {
                log.error("Failed to upload report to WhatsApp. Aborting sending.");
                return;
            }

            // 3. Fetch Recipients (Active ADMIN and ACCOUNTS_TEAM)
            List<TeamMember> recipients = new ArrayList<>();
            recipients.addAll(teamMemberRepository.findAllByRoleAndIsActiveTrue(UserRole.ADMIN));
            recipients.addAll(teamMemberRepository.findAllByRoleAndIsActiveTrue(UserRole.ACCOUNTS_TEAM));

            // Remove duplicates if any (e.g., if user has multiple roles, though TeamMember
            // has single role field)
            List<TeamMember> uniqueRecipients = recipients.stream().distinct().collect(Collectors.toList());

            log.info("Found {} recipients for the daily report.", uniqueRecipients.size());

            // 4. Send WhatsApp Messages
            for (TeamMember member : uniqueRecipients) {
                if (member.getWaPhoneNumber() != null && !member.getWaPhoneNumber().isEmpty()) {
                    String caption = "Daily Order Report - " + dateStr;
                    whatsAppService.sendDocument(member.getWaPhoneNumber(), reportMediaId, fileName, caption);
                } else {
                    log.warn("TeamMember {} (ID: {}) has no WhatsApp number. Skipping.", member.getName(),
                            member.getId());
                }
            }

            log.info("Daily order report sent successfully via WhatsApp.");

        } catch (Exception e) {
            log.error("Failed to generate or send daily order report.", e);
        }
    }
}
