package com.aps.domain;

import static com.aps.domain.CustomerOrderTestSamples.*;
import static com.aps.domain.CustomerTestSamples.*;
import static com.aps.domain.OrderItemTestSamples.*;
import static com.aps.domain.TeamMemberTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerOrder.class);
        CustomerOrder customerOrder1 = getCustomerOrderSample1();
        CustomerOrder customerOrder2 = new CustomerOrder();
        assertThat(customerOrder1).isNotEqualTo(customerOrder2);

        customerOrder2.setId(customerOrder1.getId());
        assertThat(customerOrder1).isEqualTo(customerOrder2);

        customerOrder2 = getCustomerOrderSample2();
        assertThat(customerOrder1).isNotEqualTo(customerOrder2);
    }

    @Test
    void itemsTest() {
        CustomerOrder customerOrder = getCustomerOrderRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        customerOrder.addItems(orderItemBack);
        assertThat(customerOrder.getItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(customerOrder);

        customerOrder.removeItems(orderItemBack);
        assertThat(customerOrder.getItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();

        customerOrder.items(new HashSet<>(Set.of(orderItemBack)));
        assertThat(customerOrder.getItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(customerOrder);

        customerOrder.setItems(new HashSet<>());
        assertThat(customerOrder.getItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();
    }

    @Test
    void deliveryPersonTest() {
        CustomerOrder customerOrder = getCustomerOrderRandomSampleGenerator();
        TeamMember teamMemberBack = getTeamMemberRandomSampleGenerator();

        customerOrder.setDeliveryPerson(teamMemberBack);
        assertThat(customerOrder.getDeliveryPerson()).isEqualTo(teamMemberBack);

        customerOrder.deliveryPerson(null);
        assertThat(customerOrder.getDeliveryPerson()).isNull();
    }

    @Test
    void customerTest() {
        CustomerOrder customerOrder = getCustomerOrderRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        customerOrder.setCustomer(customerBack);
        assertThat(customerOrder.getCustomer()).isEqualTo(customerBack);

        customerOrder.customer(null);
        assertThat(customerOrder.getCustomer()).isNull();
    }
}
