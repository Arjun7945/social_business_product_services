package com.aps.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.TeamMemberRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DailyOrderReportServiceTest {

    @Mock
    private ReportService reportService;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private WhatsAppService whatsAppService;

    @Mock
    private WhatsAppMediaService whatsAppMediaService;

    private DailyOrderReportService dailyOrderReportService;

    @BeforeEach
    void setUp() {
        dailyOrderReportService = new DailyOrderReportService(
                reportService,
                teamMemberRepository,
                whatsAppService,
                whatsAppMediaService);
    }

    @Test
    void shouldGenerateAndSendReportToActiveAdminAndAccountsTeam() {
        // Given
        byte[] dummyReport = new byte[] { 1, 2, 3 };
        String dummyMediaId = "media-123";

        // Mock Report Generation
        when(reportService.generateTodaysOrdersReport(anyString(), eq("EXCEL")))
                .thenReturn(dummyReport);

        // Mock Media Upload
        when(whatsAppMediaService.uploadDocument(eq(dummyReport), anyString(), anyString()))
                .thenReturn(dummyMediaId);

        // Mock Team Members
        TeamMember admin = new TeamMember().id(1L).name("Admin1").waPhoneNumber("1234567890").role(UserRole.ADMIN)
                .isActive(true);
        TeamMember accounts = new TeamMember().id(2L).name("Acc1").waPhoneNumber("0987654321")
                .role(UserRole.ACCOUNTS_TEAM).isActive(true);
        TeamMember duplicate = new TeamMember().id(1L).name("Admin1").waPhoneNumber("1234567890").role(UserRole.ADMIN)
                .isActive(true); // Same ID

        when(teamMemberRepository.findAllByRoleAndIsActiveTrue(UserRole.ADMIN))
                .thenReturn(List.of(admin, duplicate));
        when(teamMemberRepository.findAllByRoleAndIsActiveTrue(UserRole.ACCOUNTS_TEAM))
                .thenReturn(List.of(accounts));

        // When
        dailyOrderReportService.generateAndSendDailyReport();

        // Then
        // Verify Report Generated
        verify(reportService).generateTodaysOrdersReport("Accounts Team", "EXCEL");

        // Verify Upload
        verify(whatsAppMediaService).uploadDocument(eq(dummyReport), anyString(), anyString());

        // Verify Sent to Admin (once, handled duplicate)
        verify(whatsAppService).sendDocument(
                eq("1234567890"),
                eq(dummyMediaId),
                anyString(),
                anyString());

        // Verify Sent to Accounts
        verify(whatsAppService).sendDocument(
                eq("0987654321"),
                eq(dummyMediaId),
                anyString(),
                anyString());
    }

    @Test
    void shouldNotSendIfReportGenerationFails() {
        // Given
        when(reportService.generateTodaysOrdersReport(anyString(), eq("EXCEL")))
                .thenReturn(null);

        // When
        dailyOrderReportService.generateAndSendDailyReport();

        // Then
        verify(whatsAppMediaService, never()).uploadDocument(any(), anyString(), anyString());
        verify(whatsAppService, never()).sendDocument(anyString(), anyString(), anyString(), anyString());
    }
}
