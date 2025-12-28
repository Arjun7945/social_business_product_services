package com.aps.domain;

import static com.aps.domain.CustomerOrderTestSamples.*;
import static com.aps.domain.CustomerTestSamples.*;
import static com.aps.domain.ReturnStatusHistoryTestSamples.*;
import static com.aps.domain.ReturnedOrderItemTestSamples.*;
import static com.aps.domain.ReturnedOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ReturnedOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnedOrder.class);
        ReturnedOrder returnedOrder1 = getReturnedOrderSample1();
        ReturnedOrder returnedOrder2 = new ReturnedOrder();
        assertThat(returnedOrder1).isNotEqualTo(returnedOrder2);

        returnedOrder2.setId(returnedOrder1.getId());
        assertThat(returnedOrder1).isEqualTo(returnedOrder2);

        returnedOrder2 = getReturnedOrderSample2();
        assertThat(returnedOrder1).isNotEqualTo(returnedOrder2);
    }

    @Test
    void historyTest() {
        ReturnedOrder returnedOrder = getReturnedOrderRandomSampleGenerator();
        ReturnStatusHistory returnStatusHistoryBack = getReturnStatusHistoryRandomSampleGenerator();

        returnedOrder.addHistory(returnStatusHistoryBack);
        assertThat(returnedOrder.getHistories()).containsOnly(returnStatusHistoryBack);
        assertThat(returnStatusHistoryBack.getReturnedOrder()).isEqualTo(returnedOrder);

        returnedOrder.removeHistory(returnStatusHistoryBack);
        assertThat(returnedOrder.getHistories()).doesNotContain(returnStatusHistoryBack);
        assertThat(returnStatusHistoryBack.getReturnedOrder()).isNull();

        returnedOrder.histories(new HashSet<>(Set.of(returnStatusHistoryBack)));
        assertThat(returnedOrder.getHistories()).containsOnly(returnStatusHistoryBack);
        assertThat(returnStatusHistoryBack.getReturnedOrder()).isEqualTo(returnedOrder);

        returnedOrder.setHistories(new HashSet<>());
        assertThat(returnedOrder.getHistories()).doesNotContain(returnStatusHistoryBack);
        assertThat(returnStatusHistoryBack.getReturnedOrder()).isNull();
    }

    @Test
    void itemsTest() {
        ReturnedOrder returnedOrder = getReturnedOrderRandomSampleGenerator();
        ReturnedOrderItem returnedOrderItemBack = getReturnedOrderItemRandomSampleGenerator();

        returnedOrder.addItems(returnedOrderItemBack);
        assertThat(returnedOrder.getItems()).containsOnly(returnedOrderItemBack);
        assertThat(returnedOrderItemBack.getReturnedOrder()).isEqualTo(returnedOrder);

        returnedOrder.removeItems(returnedOrderItemBack);
        assertThat(returnedOrder.getItems()).doesNotContain(returnedOrderItemBack);
        assertThat(returnedOrderItemBack.getReturnedOrder()).isNull();

        returnedOrder.items(new HashSet<>(Set.of(returnedOrderItemBack)));
        assertThat(returnedOrder.getItems()).containsOnly(returnedOrderItemBack);
        assertThat(returnedOrderItemBack.getReturnedOrder()).isEqualTo(returnedOrder);

        returnedOrder.setItems(new HashSet<>());
        assertThat(returnedOrder.getItems()).doesNotContain(returnedOrderItemBack);
        assertThat(returnedOrderItemBack.getReturnedOrder()).isNull();
    }

    @Test
    void orderTest() {
        ReturnedOrder returnedOrder = getReturnedOrderRandomSampleGenerator();
        CustomerOrder customerOrderBack = getCustomerOrderRandomSampleGenerator();

        returnedOrder.setOrder(customerOrderBack);
        assertThat(returnedOrder.getOrder()).isEqualTo(customerOrderBack);

        returnedOrder.order(null);
        assertThat(returnedOrder.getOrder()).isNull();
    }

    @Test
    void customerTest() {
        ReturnedOrder returnedOrder = getReturnedOrderRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        returnedOrder.setCustomer(customerBack);
        assertThat(returnedOrder.getCustomer()).isEqualTo(customerBack);

        returnedOrder.customer(null);
        assertThat(returnedOrder.getCustomer()).isNull();
    }
}
