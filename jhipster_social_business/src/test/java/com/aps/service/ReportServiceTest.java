package com.aps.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.aps.domain.CustomerOrder;
import com.aps.repository.CustomerOrderRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private CustomerOrderRepository orderRepository;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(orderRepository);
    }

    @Test
    void generateTodaysOrdersReport_ShouldNotThrowFileNotFoundException() {
        // Arrange
        when(orderRepository.findAllByOrderTimeBetween(any(), any())).thenReturn(Collections.emptyList());

        // Act & Assert
        // We expect it to NOT throw "Report template not found" (FileNotFoundException
        // wrapped in RuntimeException)
        // It might throw JRException if compilation fails due to missing fonts/etc in
        // test env,
        // but it should NOT fail on "template not found".
        // However, if list is empty, it returns null immediately (Line 84 in service).
        // We need to provide a list to reach the template loading logic.

        CustomerOrder order = new CustomerOrder();
        order.setTotalAmount(java.math.BigDecimal.TEN);

        when(orderRepository.findAllByOrderTimeBetween(any(), any())).thenReturn(Collections.singletonList(order));

        assertThatCode(() -> reportService.generateTodaysOrdersReport("TestUser", "PDF"))
                .doesNotThrowAnyException();
    }

    @Test
    void generateUnpaidOrdersReport_ShouldNotThrowException() {
        CustomerOrder order = new CustomerOrder();
        order.setTotalAmount(java.math.BigDecimal.TEN);

        when(orderRepository.findAllByStatusNot(any())).thenReturn(Collections.singletonList(order));

        assertThatCode(() -> reportService.generateUnpaidOrdersReport("TestUser", "PDF"))
                .doesNotThrowAnyException();
    }
}
