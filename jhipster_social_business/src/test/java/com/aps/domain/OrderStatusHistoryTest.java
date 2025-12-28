package com.aps.domain;

import static com.aps.domain.CustomerOrderTestSamples.*;
import static com.aps.domain.OrderStatusHistoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderStatusHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderStatusHistory.class);
        OrderStatusHistory orderStatusHistory1 = getOrderStatusHistorySample1();
        OrderStatusHistory orderStatusHistory2 = new OrderStatusHistory();
        assertThat(orderStatusHistory1).isNotEqualTo(orderStatusHistory2);

        orderStatusHistory2.setId(orderStatusHistory1.getId());
        assertThat(orderStatusHistory1).isEqualTo(orderStatusHistory2);

        orderStatusHistory2 = getOrderStatusHistorySample2();
        assertThat(orderStatusHistory1).isNotEqualTo(orderStatusHistory2);
    }

    @Test
    void customerOrderTest() {
        OrderStatusHistory orderStatusHistory = getOrderStatusHistoryRandomSampleGenerator();
        CustomerOrder customerOrderBack = getCustomerOrderRandomSampleGenerator();

        orderStatusHistory.setCustomerOrder(customerOrderBack);
        assertThat(orderStatusHistory.getCustomerOrder()).isEqualTo(customerOrderBack);
        assertThat(customerOrderBack.getHistory()).isEqualTo(orderStatusHistory);

        orderStatusHistory.customerOrder(null);
        assertThat(orderStatusHistory.getCustomerOrder()).isNull();
        assertThat(customerOrderBack.getHistory()).isNull();
    }
}
