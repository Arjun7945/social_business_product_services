package com.aps.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.DeliveryPerson;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.OrderStatus;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.CustomerOrderRepository;
import com.aps.repository.CustomerRepository;
import com.aps.repository.DeliveryPersonRepository;
import com.aps.repository.TeamMemberRepository;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.payment.RazorpayService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreditCustomerFlowServiceTest {

        @Mock
        private WhatsAppService whatsAppService;
        @Mock
        private CustomerOrderRepository customerOrderRepository;
        @Mock
        private CustomerRepository customerRepository;
        @Mock
        private TeamMemberRepository teamMemberRepository;
        @Mock
        private DeliveryPersonRepository deliveryPersonRepository;
        @Mock
        private DeliveryPersonMessageService deliveryPersonMessageService;
        @Mock
        private OrderStatusHistoryService orderStatusHistoryService;
        @Mock
        private CustomerMessageService customerMessageService;
        @Mock
        private RazorpayService razorpayService;
        @Mock
        private AdminMessageService adminMessageService;
        @Mock
        private DeliveryPersonService deliveryPersonService;

        private CreditCustomerFlowService creditCustomerFlowService;

        @BeforeEach
        void setUp() {
                creditCustomerFlowService = new CreditCustomerFlowService(
                                whatsAppService,
                                customerOrderRepository,
                                customerRepository,
                                teamMemberRepository,
                                deliveryPersonRepository,
                                deliveryPersonMessageService,
                                orderStatusHistoryService,
                                customerMessageService,
                                razorpayService,
                                adminMessageService,
                                deliveryPersonService);
        }

        @Test
        void testHandlePaymentResisted_OrderNotFound() {
                when(customerOrderRepository.findById(1L)).thenReturn(Optional.empty());
                creditCustomerFlowService.handlePaymentResisted(new DeliveryPerson(), 1L);
                verify(whatsAppService, never()).sendInteractiveButtons(anyString(), anyString(), anyList());
        }

        @Test
        void testHandlePaymentResisted_Success() {
                DeliveryPerson dp = new DeliveryPerson();
                dp.setId(10L);
                dp.setWaPhoneNumber("919999999999");
                dp.setName("Dave");

                Customer cust = new Customer();
                cust.setName("Alice");
                cust.setRole(UserRole.CUSTOMER);

                CustomerOrder order = new CustomerOrder();
                order.setId(1L);
                order.setCustomer(cust);
                order.setTotalAmount(BigDecimal.valueOf(100.0));

                when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(order));

                TeamMember admin = new TeamMember();
                admin.setWaPhoneNumber("918888888888");
                when(teamMemberRepository.findAllByRole(UserRole.ADMIN)).thenReturn(Collections.singletonList(admin));

                when(adminMessageService.getPaymentResistedAdminInfo(anyString(), anyString(), anyString(),
                                anyDouble(), anyLong()))
                                .thenReturn("Admin Info");

                when(adminMessageService.getButtonAllowCreditOnce()).thenReturn("Allow Once");
                when(adminMessageService.getButtonGrantAlways()).thenReturn("Grant Always");
                when(adminMessageService.getButtonDenyCredit()).thenReturn("Deny");
                when(deliveryPersonMessageService.getAdminNotification()).thenReturn("Wait for admin");

                creditCustomerFlowService.handlePaymentResisted(dp, 1L);

                verify(whatsAppService).sendInteractiveButtons(eq("918888888888"), eq("Admin Info"), anyList());
                verify(whatsAppService).sendSimpleText("919999999999", "Wait for admin");
        }

        @Test
        void testShowCreditCustomerOrders_Empty() {
                TeamMember admin = new TeamMember();
                admin.setWaPhoneNumber("918888888888");
                when(customerOrderRepository.findAllByStatus(OrderStatus.ON_CREDIT_PURCHASE))
                                .thenReturn(Collections.emptyList());
                when(adminMessageService.getNoCreditOrdersFound()).thenReturn("No orders found");

                creditCustomerFlowService.showCreditCustomerOrders(admin);

                verify(whatsAppService).sendSimpleText("918888888888", "No orders found");
        }

        @Test
        void testHandleCreditOrderAction_SendLink() {
                TeamMember admin = new TeamMember();
                admin.setWaPhoneNumber("918888888888");
                admin.setName("Super Admin");
                String buttonId = CreditCustomerFlowService.PREFIX_CREDIT_PAY_LINK + "1";

                Customer cust = new Customer();
                cust.setWaPhoneNumber("917777777777");

                CustomerOrder order = new CustomerOrder();
                order.setId(1L);
                order.setCustomer(cust);
                order.setTotalAmount(BigDecimal.valueOf(500.0));

                when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(order));
                when(razorpayService.createPaymentLink(anyLong(), anyDouble(), any(Customer.class)))
                                .thenReturn("http://link");
                when(adminMessageService.getPaymentLinkGeneratedSuccess()).thenReturn("Link Sent");
                when(customerMessageService.getPaymentLinkMessage(anyString(), anyDouble(), anyString(), anyLong()))
                                .thenReturn("Please pay");

                creditCustomerFlowService.handleCreditOrderAction(admin, buttonId);

                verify(razorpayService).createPaymentLink(1L, 500.0, cust);
                verify(whatsAppService).sendSimpleText("918888888888", "Link Sent");
                verify(whatsAppService).sendSimpleText("917777777777", "Please pay");
        }

        @Test
        void testHandleCreditOrderSelection() {
                TeamMember admin = new TeamMember();
                admin.setWaPhoneNumber("918888888888");
                Long orderId = 123L;

                Customer cust = new Customer();
                cust.setName("Bob");
                CustomerOrder order = new CustomerOrder();
                order.setId(orderId);
                order.setCustomer(cust);
                order.setTotalAmount(BigDecimal.valueOf(150.0));
                order.setStatus(OrderStatus.ON_CREDIT_PURCHASE);

                when(customerOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
                when(adminMessageService.getOrderDetails(anyLong(), anyString(), anyDouble(), anyString()))
                                .thenReturn("Order Details");
                // Mock title getters
                when(adminMessageService.getLinkButtonTitle()).thenReturn("Link");
                when(adminMessageService.getQrButtonTitle()).thenReturn("QR");
                when(adminMessageService.getCodButtonTitle()).thenReturn("COD");
                when(adminMessageService.getBackToCreditMenuTitle()).thenReturn("Back");
                when(adminMessageService.getBackToCreditMenuDesc()).thenReturn("Go Back");

                // New Mocks
                when(adminMessageService.getLinkButtonDesc()).thenReturn("Link Desc");
                when(adminMessageService.getQrButtonDesc()).thenReturn("QR Desc");
                when(adminMessageService.getCodButtonDesc()).thenReturn("COD Desc");
                when(adminMessageService.getListOptionsButtonText()).thenReturn("Options");
                when(adminMessageService.getListActionSectionTitle()).thenReturn("Actions");

                creditCustomerFlowService.handleCreditOrderSelection(admin, orderId);

                // Verify that sendInteractiveList is called with our new signature
                // sendInteractiveList(toWaId, bodyText, buttonText, sectionTitle, rows)
                verify(whatsAppService).sendInteractiveList(
                                eq("918888888888"),
                                eq("Order Details"),
                                eq("Options"),
                                eq("Actions"),
                                anyList());
        }
}
