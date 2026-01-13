package com.aps.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.when;

import com.aps.domain.DeliveryPerson;
import com.aps.repository.CustomerOrderRepository;
import com.aps.repository.DeliveryPersonRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRemovalServiceTest {

        @Mock
        private CustomerOrderRepository customerOrderRepository;

        @Mock
        private DeliveryPersonRepository deliveryPersonRepository;

        private UserRemovalService userRemovalService;

        @BeforeEach
        void setUp() {
                // Mock other dependencies as lenient or null since we only test the safeguard
                userRemovalService = new UserRemovalService(
                                null, // customerRepository
                                null, // teamMemberRepository
                                deliveryPersonRepository,
                                customerOrderRepository,
                                null, // removedUserRepository
                                null, // removedOrderSummaryRepository
                                null, // customerFlowService
                                null, // shoppingCartRepository
                                null, // returnedOrderRepository
                                null // deliveryZoneRepository
                );
        }

        @Test
        void removeDeliveryPerson_ShouldThrowException_WhenOrdersArePending() {
                // Arrange
                Long dpId = 1L;
                DeliveryPerson dp = new DeliveryPerson();
                dp.setId(dpId);
                dp.setName("Test DP");

                when(deliveryPersonRepository.findById(dpId)).thenReturn(Optional.of(dp));

                // Return stats indicating 1 active order
                // Return list of active orders
                List<com.aps.domain.CustomerOrder> activeOrders = new ArrayList<>();
                com.aps.domain.CustomerOrder order = new com.aps.domain.CustomerOrder();
                order.setId(101L);
                order.setTotalAmount(java.math.BigDecimal.valueOf(100.0));
                activeOrders.add(order);

                when(customerOrderRepository.findAllByDeliveryPersonIdAndStatusIn(eq(dpId),
                                org.mockito.ArgumentMatchers.anyList()))
                                .thenReturn(activeOrders);

                // Fallback: Mock stats to return 4-element array to avoid AIOOBE if logic falls
                // through
                List<Object[]> activeStats = new ArrayList<>();
                activeStats.add(new Object[] { 0L, java.math.BigDecimal.ZERO, null, null });
                org.mockito.Mockito.lenient()
                                .when(customerOrderRepository.findStatsByDeliveryPersonIdAndStatus(eq(dpId), any()))
                                .thenReturn(activeStats);

                // Act & Assert
                assertThatThrownBy(() -> userRemovalService.removeDeliveryPerson(dpId, "Test Reason"))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("Delivery Person Test DP has")
                                .hasMessageContaining("1 pending orders");
        }
}
