package com.aps.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import com.aps.config.FlowConstants;
import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.DeliveryPerson;
import com.aps.repository.CustomerOrderRepository;
import com.aps.repository.CustomerRepository;
import com.aps.repository.DeliveryPersonRepository;
import com.aps.repository.DeliveryZoneRepository;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.payment.PaymentStrategyFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.aps.domain.enumeration.OrderStatus;

@ExtendWith(MockitoExtension.class)
class DeliveryFlowServiceTest {

    @Mock
    private WhatsAppService whatsAppService;
    @Mock
    private BotSessionManager sessionManager;
    @Mock
    private DeliveryPersonMessageService deliveryPersonMessageService;
    @Mock
    private CustomerMessageService customerMessageService;
    @Mock
    private CustomerFlowService customerFlowService;
    @Mock
    private PaymentStrategyFactory paymentStrategyFactory;
    @Mock
    private DeliveryPersonRepository deliveryPersonRepository;
    @Mock
    private CustomerOrderRepository customerOrderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private OrderStatusHistoryService orderStatusHistoryService;
    @Mock
    private DeliveryZoneRepository deliveryZoneRepository;
    @Mock
    private DeliveryPersonService deliveryPersonService;
    @Mock
    private CreditCustomerFlowService creditCustomerFlowService;

    @InjectMocks
    private DeliveryFlowService deliveryFlowService;

    @BeforeEach
    void setUp() {
        // Mocks setup
    }

    @Test
    void handleOrderTakenList_MultipleOrders_ShouldHaveUniqueIds() {
        // Arrange
        DeliveryPerson dp = new DeliveryPerson();
        dp.setWaPhoneNumber("91999");
        dp.setChosenOrder("100,101,102"); // 3 orders

        when(deliveryPersonMessageService.getTitleOfOrderNumber()).thenReturn("Order: ");
        when(deliveryPersonMessageService.getViewDetailsOfOrders()).thenReturn("Details");
        when(deliveryPersonMessageService.getTitleOfOGoBack()).thenReturn("Back");
        when(deliveryPersonMessageService.getBackToTheMainMenu()).thenReturn("Menu");
        when(deliveryPersonMessageService.getOrderTakenListHeader()).thenReturn("Header");
        when(deliveryPersonMessageService.getOrderViewButtonLabal()).thenReturn("View");

        // Mock Session
        com.aps.domain.BotSession session = new com.aps.domain.BotSession();
        session.setWaPhoneNumber("91999");
        when(sessionManager.getSession("91999")).thenReturn(session);

        // Mock Webhook Message
        com.aps.service.dto.WhatsAppWebhookDto.Message message = new com.aps.service.dto.WhatsAppWebhookDto.Message();
        message.setType("interactive");
        com.aps.service.dto.WhatsAppWebhookDto.Interactive interactive = new com.aps.service.dto.WhatsAppWebhookDto.Interactive();
        interactive.setType("button_reply");
        com.aps.service.dto.WhatsAppWebhookDto.ButtonReply reply = new com.aps.service.dto.WhatsAppWebhookDto.ButtonReply();
        reply.setId(FlowConstants.DELIVERY_MENU_ORDER_TAKEN);
        interactive.setButtonReply(reply);
        message.setInteractive(interactive);

        deliveryFlowService.handleDeliveryMessage(dp, message);

        // Assert
        ArgumentCaptor<List<WhatsAppMessageDto.RowDto>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(whatsAppService).sendInteractiveList(eq("91999"), anyString(), anyString(), listCaptor.capture());

        List<WhatsAppMessageDto.RowDto> rows = listCaptor.getValue();
        // 3 orders -> 3 rows + 1 Back row = 4 rows
        long backCount = rows.stream().filter(r -> r.getId().equals(FlowConstants.PREFIX_MAIN_MENU)).count();

        if (rows.size() != 4 || backCount != 1) {
            throw new AssertionError("Expected 4 rows and 1 Back button, found " + rows.size() + " rows and "
                    + backCount + " Back buttons.");
        }
    }

    @Test
    void handleDeliveryMessage_ViewDetails_ShouldSendPaymentOptionsWithUniqueIds() {
        // Arrange
        DeliveryPerson dp = new DeliveryPerson();
        dp.setWaPhoneNumber("91999");
        dp.setName("DP1");

        Customer c = new Customer();
        c.setName("Cust1");
        c.setPhoneNumber("123");
        c.setLocationLat(10.0);
        c.setLocationLon(76.0);

        CustomerOrder order = new CustomerOrder();
        order.setId(100L);
        order.setCustomer(c);
        order.setTotalAmount(BigDecimal.valueOf(500.0));

        when(customerOrderRepository.findById(100L)).thenReturn(Optional.of(order));

        // Mock message service responses
        when(deliveryPersonMessageService.getOrderConfirmationSuccess(any(), any(), any())).thenReturn("Confirmed");
        when(deliveryPersonMessageService.getButtonCod()).thenReturn("COD");
        when(deliveryPersonMessageService.getButtonQr()).thenReturn("QR");
        when(deliveryPersonMessageService.getButtonLink()).thenReturn("Link");
        when(deliveryPersonMessageService.getButtonPaymentResisted()).thenReturn("Resist");
        when(deliveryPersonMessageService.getDescriptionPaymentResisted()).thenReturn("Desc");
        when(deliveryPersonMessageService.getPaymentModeSelectionHeader(any(), anyDouble())).thenReturn("Header");
        when(deliveryPersonMessageService.getButtonName()).thenReturn("SectionTitle");
        when(deliveryPersonMessageService.getButtonCodDescription()).thenReturn("COD Desc");
        when(deliveryPersonMessageService.getButtonQrDescription()).thenReturn("QR Desc");
        when(deliveryPersonMessageService.getButtonLinkDescription()).thenReturn("Link Desc");

        // Mock Session
        com.aps.domain.BotSession session = new com.aps.domain.BotSession();
        session.setWaPhoneNumber("91999");
        when(sessionManager.getSession("91999")).thenReturn(session);

        // Mock Webhook Message
        com.aps.service.dto.WhatsAppWebhookDto.Message message = new com.aps.service.dto.WhatsAppWebhookDto.Message();
        message.setType("interactive");
        com.aps.service.dto.WhatsAppWebhookDto.Interactive interactive = new com.aps.service.dto.WhatsAppWebhookDto.Interactive();
        interactive.setType("list_reply");
        com.aps.service.dto.WhatsAppWebhookDto.ListReply reply = new com.aps.service.dto.WhatsAppWebhookDto.ListReply();
        reply.setId(FlowConstants.PREFIX_DELIVERY_DETAILS + "100");
        interactive.setListReply(reply);
        message.setInteractive(interactive);

        // Act
        deliveryFlowService.handleDeliveryMessage(dp, message);

        // Assert
        ArgumentCaptor<List<WhatsAppMessageDto.RowDto>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(whatsAppService).sendInteractiveList(eq("91999"), anyString(), anyString(), listCaptor.capture());

        List<WhatsAppMessageDto.RowDto> rows = listCaptor.getValue();
        if (rows.size() != 4) {
            throw new AssertionError("Expected 4 Payment Mode rows, found " + rows.size());
        }

        long codCount = rows.stream().filter(r -> r.getId().equals(FlowConstants.PREFIX_PAY_COD + "100")).count();
        long resistedCount = rows.stream()
                .filter(r -> r.getId().equals(CreditCustomerFlowService.PREFIX_PAY_RESISTED + "100")).count();

        if (codCount != 1 || resistedCount != 1) {
            throw new AssertionError("Missing or duplicate payment IDs");
        }
    }

    @Test
    void handlePaymentModeSelection_COD_ShouldTriggerVerification() {
        // Arrange
        DeliveryPerson dp = new DeliveryPerson();
        dp.setWaPhoneNumber("91999");
        dp.setName("DP1");

        CustomerOrder order = new CustomerOrder();
        order.setId(200L);
        order.setTotalAmount(BigDecimal.valueOf(150.0));

        when(customerOrderRepository.findById(200L)).thenReturn(Optional.of(order));
        when(deliveryPersonMessageService.getCodCollectionVerificationQuestion(anyString(), anyDouble()))
                .thenReturn("Verify?");
        when(deliveryPersonMessageService.getButtonIdsCollected()).thenReturn("Yes");
        when(deliveryPersonMessageService.getButtonIdsNotCollected()).thenReturn("No");

        // Mock Session
        com.aps.domain.BotSession session = new com.aps.domain.BotSession();
        when(sessionManager.getSession("91999")).thenReturn(session);

        // Mock Webhook Message
        com.aps.service.dto.WhatsAppWebhookDto.Message message = new com.aps.service.dto.WhatsAppWebhookDto.Message();
        message.setType("interactive");
        com.aps.service.dto.WhatsAppWebhookDto.Interactive interactive = new com.aps.service.dto.WhatsAppWebhookDto.Interactive();
        interactive.setType("list_reply");
        com.aps.service.dto.WhatsAppWebhookDto.ListReply reply = new com.aps.service.dto.WhatsAppWebhookDto.ListReply();
        reply.setId(FlowConstants.PREFIX_PAY_COD + "200");
        interactive.setListReply(reply);
        message.setInteractive(interactive);

        // Act
        deliveryFlowService.handleDeliveryMessage(dp, message);

        // Assert
        // Should verify that Interactive Buttons (Yes/No) are sent
        verify(whatsAppService).sendInteractiveButtons(eq("91999"), eq("Verify?"), any());

        // Should NOT update order status yet
        verify(orderStatusHistoryService, never()).addEvent(any());
    }

    @Test
    void handleButtonReply_CodVerifyYes_ShouldCompleteTransaction() {
        // Arrange
        DeliveryPerson dp = new DeliveryPerson();
        dp.setId(1L);
        dp.setWaPhoneNumber("91999");
        dp.setName("DP1");

        Customer c = new Customer();
        c.setWaPhoneNumber("8888");

        CustomerOrder order = new CustomerOrder();
        order.setId(300L);
        order.setCustomer(c);
        order.setDeliveryPerson(dp);
        order.setTotalAmount(BigDecimal.valueOf(50.0));
        order.setStatus(OrderStatus.DELIVERY_ONWAY);

        when(customerOrderRepository.findById(300L)).thenReturn(Optional.of(order));

        // Mock Messages
        when(deliveryPersonMessageService.getOrderDeliveredSuccess()).thenReturn("Success");
        when(customerMessageService.getOrderDeliveredMessage(any(), anyDouble(), anyString())).thenReturn("Receipt");

        // Mock Session
        com.aps.domain.BotSession session = new com.aps.domain.BotSession();
        when(sessionManager.getSession("91999")).thenReturn(session);

        // Mock Button Reply (YES)
        com.aps.service.dto.WhatsAppWebhookDto.Message message = new com.aps.service.dto.WhatsAppWebhookDto.Message();
        message.setType("interactive");
        com.aps.service.dto.WhatsAppWebhookDto.Interactive interactive = new com.aps.service.dto.WhatsAppWebhookDto.Interactive();
        interactive.setType("button_reply");
        com.aps.service.dto.WhatsAppWebhookDto.ButtonReply reply = new com.aps.service.dto.WhatsAppWebhookDto.ButtonReply();
        reply.setId(FlowConstants.PREFIX_COD_VERIFY_YES + "300");
        interactive.setButtonReply(reply);
        message.setInteractive(interactive);

        // Act
        deliveryFlowService.handleDeliveryMessage(dp, message);

        // Assert
        // 1. Order status should change to DELIVERED_SUCCESSFULLY
        // Note: updateOrderStatus saves the order
        verify(customerOrderRepository, org.mockito.Mockito.times(2)).save(order);

        // 2. Should send success messages
        verify(whatsAppService).sendSimpleText(eq("91999"), eq("Success")); // DP
        verify(whatsAppService).sendSimpleText(eq("8888"), eq("Receipt")); // Customer
    }

    @Test
    void handleButtonReply_CodVerifyNo_ShouldSendWarning() {
        // Arrange
        DeliveryPerson dp = new DeliveryPerson();
        dp.setWaPhoneNumber("91999");

        when(deliveryPersonMessageService.getCodCollectionWarning()).thenReturn("Warning");

        // Mock Session
        com.aps.domain.BotSession session = new com.aps.domain.BotSession();
        when(sessionManager.getSession("91999")).thenReturn(session);

        // Mock Button Reply (NO)
        com.aps.service.dto.WhatsAppWebhookDto.Message message = new com.aps.service.dto.WhatsAppWebhookDto.Message();
        message.setType("interactive");
        com.aps.service.dto.WhatsAppWebhookDto.Interactive interactive = new com.aps.service.dto.WhatsAppWebhookDto.Interactive();
        interactive.setType("button_reply");
        com.aps.service.dto.WhatsAppWebhookDto.ButtonReply reply = new com.aps.service.dto.WhatsAppWebhookDto.ButtonReply();
        reply.setId(FlowConstants.PREFIX_COD_VERIFY_NO + "400");
        interactive.setButtonReply(reply);
        message.setInteractive(interactive);

        // Act
        deliveryFlowService.handleDeliveryMessage(dp, message);

        // Assert
        verify(whatsAppService).sendSimpleText(eq("91999"), eq("Warning"));
        // Ensure status update is NOT called (indirectly by ensuring repo save is not
        // called for status or order)
        verify(customerOrderRepository, never()).save(any());
    }
}
