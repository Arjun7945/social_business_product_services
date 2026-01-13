package com.aps.service;

import com.aps.domain.BotSession;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AdminFlowStage;
import com.aps.service.admin.*;
import com.aps.service.dto.WhatsAppWebhookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminFlowServiceTest {

    @Mock
    private WhatsAppService whatsAppService;
    @Mock
    private BotSessionManager sessionManager;
    @Mock
    private CustomerManagementService customerManagementService;
    @Mock
    private ProductManagementService productManagementService;
    @Mock
    private DeliveryPersonManagementService deliveryPersonManagementService;
    @Mock
    private ExecutiveManagementService executiveManagementService;
    @Mock
    private AssistantAdminManagementService assistantAdminManagementService;
    @Mock
    private AccountsManagementService accountsManagementService;
    @Mock
    private CreditCustomerFlowService creditCustomerFlowService;

    @InjectMocks
    private AdminFlowService adminFlowService;

    private TeamMember admin;
    private BotSession session;

    @BeforeEach
    void setUp() {
        admin = new TeamMember();
        admin.setName("Admin");
        admin.setWaPhoneNumber("1234567890");
        admin.setRole(com.aps.domain.enumeration.UserRole.ADMIN);

        session = new BotSession();
        session.setWaPhoneNumber("1234567890");
        session.setCurrentState(AdminFlowStage.IDLE.name());
    }

    @Test
    void handleAdminMessage_DeleteDeliveryMenuButton_CallsStartDelete() {
        // Arrange
        WhatsAppWebhookDto.Message message = new WhatsAppWebhookDto.Message();
        message.setType("interactive");
        WhatsAppWebhookDto.Interactive interactive = new WhatsAppWebhookDto.Interactive();
        interactive.setType("button_reply");
        WhatsAppWebhookDto.ButtonReply buttonReply = new WhatsAppWebhookDto.ButtonReply();
        buttonReply.setId("DELETE_DELIVERY_MENU");
        interactive.setButtonReply(buttonReply);
        message.setInteractive(interactive);

        when(sessionManager.getSession("1234567890")).thenReturn(session);

        // Act
        adminFlowService.handleAdminMessage(admin, message);

        // Assert
        verify(deliveryPersonManagementService).startDeleteDeliveryPerson(admin, session);
    }
}
