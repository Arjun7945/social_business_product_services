package com.aps.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.aps.domain.*;
import com.aps.domain.enumeration.*;
import com.aps.repository.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.aps.service.dto.WhatsAppMessageDto;

@ExtendWith(MockitoExtension.class)
class CreditCustomerFlowServiceTest {

        @Mock
        private CustomerOrderRepository customerOrderRepository;
        @Mock
        private CustomerRepository customerRepository;
        @Mock
        private DeliveryPersonRepository deliveryPersonRepository;
        @Mock
        private TeamMemberRepository teamMemberRepository;
        @Mock
        private WhatsAppService whatsAppService;
        @Mock
        private DeliveryPersonMessageService deliveryPersonMessageService;
        @Mock
        private CustomerMessageService customerMessageService;
        @Mock
        private OrderStatusHistoryService orderStatusHistoryService;
        @Mock
        private com.aps.service.payment.RazorpayService razorpayService;

        @InjectMocks
        private CreditCustomerFlowService creditCustomerFlowService;

        @Test
        void handlePaymentResisted_shouldNotifyAdmins() {
                DeliveryPerson dp = new DeliveryPerson();
                dp.setId(1L);
                dp.setName("DP1");
                dp.setWaPhoneNumber("91999");

                Customer c = new Customer();
                c.setId(10L);
                c.setName("Cust1");
                c.setRole(UserRole.CUSTOMER);

                CustomerOrder order = new CustomerOrder();
                order.setId(100L);
                order.setCustomer(c);
                order.setTotalAmount(java.math.BigDecimal.valueOf(500));

                when(customerOrderRepository.findById(100L)).thenReturn(Optional.of(order));

                TeamMember admin = new TeamMember();
                admin.setWaPhoneNumber("91888");
                when(teamMemberRepository.findAllByRole(UserRole.ADMIN)).thenReturn(List.of(admin));

                when(deliveryPersonMessageService.getPaymentResistedAdminInfo(anyString(), anyString(), anyString(),
                                anyDouble(), anyLong())).thenReturn("Admin Msg");
                when(deliveryPersonMessageService.getAdminNotification()).thenReturn("Admin notified");
                lenient().when(deliveryPersonMessageService.getButtonAllowCreditOnce()).thenReturn("Allow Once");
                lenient().when(deliveryPersonMessageService.getButtonGrantAlways()).thenReturn("Grant Always");
                lenient().when(deliveryPersonMessageService.getButtonDenyCredit()).thenReturn("Deny");

                creditCustomerFlowService.handlePaymentResisted(dp, 100L);

                verify(whatsAppService).sendInteractiveButtons(eq("91888"), eq("Admin Msg"), anyList());
                verify(whatsAppService).sendSimpleText(eq("91999"), contains("Admin notified"));
        }

        @Test
        void handleAdminDecision_grantAlways_shouldUpdateRoleAndOrder() {
                TeamMember admin = new TeamMember();

                Customer c = new Customer();
                c.setId(10L);
                c.setRole(UserRole.CUSTOMER);
                c.setWaPhoneNumber("91777");
                c.setName("Cust1");

                CustomerOrder order = new CustomerOrder();
                order.setId(100L);
                order.setCustomer(c);
                order.setStatus(OrderStatus.DELIVERY_ONWAY);
                order.setTotalAmount(java.math.BigDecimal.valueOf(500));

                when(customerOrderRepository.findById(100L)).thenReturn(Optional.of(order));

                DeliveryPerson dp = new DeliveryPerson();
                dp.setId(5L);
                dp.setWaPhoneNumber("91999");
                when(deliveryPersonRepository.findById(5L)).thenReturn(Optional.of(dp));

                // Mock Strings
                lenient().when(deliveryPersonMessageService.getCreditGrantedAlwaysMessage(anyString(), anyLong()))
                                .thenReturn("Credit APPROVED");
                lenient().when(deliveryPersonMessageService.getCreditDeniedMessage(anyString(), anyLong()))
                                .thenReturn("Credit DENIED");
                lenient().when(customerMessageService.getMessageCreditPrivilegesGranted(anyString()))
                                .thenReturn("Credit payments are now enabled");

                String btnId = CreditCustomerFlowService.PREFIX_ADMIN_CREDIT_GRANT + "100_5"; // orderId_dpId

                creditCustomerFlowService.handleAdminDecision(admin, btnId);

                assertThat(c.getRole()).isEqualTo(UserRole.CREDIT_CUSTOMER);
                assertThat(order.getStatus()).isEqualTo(OrderStatus.ON_CREDIT_PURCHASE);

                verify(customerRepository).save(c);
                verify(customerOrderRepository).save(order);
                verify(whatsAppService).sendSimpleText(eq("91999"), contains("Credit APPROVED")); // Notify DP
                verify(whatsAppService).sendSimpleText(eq("91777"), contains("Credit payments are now enabled")); // Notify
                                                                                                                  // Customer
        }

        @Test
        void handleAdminDecision_deny_shouldNotifyDP() {
                TeamMember admin = new TeamMember();

                Customer c = new Customer();
                c.setId(10L);
                c.setName("Cust1");
                c.setRole(UserRole.CUSTOMER);

                CustomerOrder order = new CustomerOrder();
                order.setId(100L);
                order.setCustomer(c);

                when(customerOrderRepository.findById(100L)).thenReturn(Optional.of(order));

                DeliveryPerson dp = new DeliveryPerson();
                dp.setId(5L);
                dp.setWaPhoneNumber("91999");
                when(deliveryPersonRepository.findById(5L)).thenReturn(Optional.of(dp));

                // Mock Strings
                lenient().when(deliveryPersonMessageService.getCreditDeniedMessage(anyString(), anyLong()))
                                .thenReturn("Credit DENIED");

                String btnId = CreditCustomerFlowService.PREFIX_ADMIN_CREDIT_DENY + "100_5";

                creditCustomerFlowService.handleAdminDecision(admin, btnId);

                verify(whatsAppService).sendSimpleText(eq("91999"), contains("Credit DENIED"));
                assertThat(c.getRole()).isEqualTo(UserRole.CUSTOMER); // Should not change
        }
}
