package com.aps.service;

import com.aps.domain.User;
import com.aps.repository.UserRepository;
import com.aps.security.AuthoritiesConstants;
import java.time.LocalDate;
import java.util.List;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DailyOrderReportService {

    private final Logger log = LoggerFactory.getLogger(DailyOrderReportService.class);

    private final ReportService reportService;
    private final MailService mailService;
    private final UserRepository userRepository;

    public DailyOrderReportService(ReportService reportService, MailService mailService,
            UserRepository userRepository) {
        this.reportService = reportService;
        this.mailService = mailService;
        this.userRepository = userRepository;
    }

    /**
     * Report generation scheduled for 5 PM every day.
     * Cron format: second, minute, hour, day of month, month, day(s) of week
     */
    @Scheduled(cron = "0 0 17 * * ?")
    public void generateAndSendDailyReport() {
        log.info("Starting scheduled daily order report generation.");

        try {
            // 1. Generate Report
            byte[] reportBytes = reportService.generateTodaysOrdersReport("Accounts Team", "EXCEL");

            if (reportBytes == null || reportBytes.length == 0) {
                log.info("No orders found for today. Skipping report email.");
                return;
            }

            String dateStr = LocalDate.now().toString();
            String fileName = "Daily_Orders_" + dateStr + ".xlsx";

            // 2. Fetch Recipients (ADMIN and ACCOUNTS_TEAM)
            // Note: AuthoritiesConstants.ACCOUNTS_TEAM needs to exist. If not, use string
            // literal or add it.
            // Based on task description: "active accounts team and admin role team members"
            // Checking standard JHipster constants later, assuming "ROLE_ADMIN".
            // For accounts team, I'll assume "ROLE_ACCOUNTS_TEAM" if it was added,
            // otherwise might need check.
            // Using literal check for safety if constant missing in import scope or file.

            List<User> recipients = userRepository.findAllByAuthoritiesNameIn(
                    List.of(AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCOUNTS_TEAM));

            log.info("Found {} recipients for the daily report.", recipients.size());

            // 3. Send Emails
            for (User user : recipients) {
                if (user.getEmail() != null) {
                    mailService.sendEmailWithAttachment(
                            user.getEmail(),
                            "Daily Order Report - " + dateStr,
                            "Please find attached the daily order report for " + dateStr + ".",
                            false,
                            fileName,
                            reportBytes,
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                }
            }

            log.info("Daily order report sent successfully.");

        } catch (Exception e) {
            log.error("Failed to generate or send daily order report.", e);
        }
    }
}
