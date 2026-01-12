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
        List<Object[]> activeStats = new ArrayList<>();
        activeStats.add(new Object[] { 1L, java.math.BigDecimal.valueOf(100.0) });

        when(customerOrderRepository.findStatsByDeliveryPersonIdAndStatus(eq(dpId), any()))
                .thenReturn(activeStats);

        // Act & Assert
        assertThatThrownBy(() -> userRemovalService.removeDeliveryPerson(dpId, "Test Reason"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot remove Test DP")
                .hasMessageContaining("1 pending orders");
    }
}
