package com.aps.domain;

import static com.aps.domain.CustomerOrderTestSamples.*;
import static com.aps.domain.CustomerTestSamples.*;
import static com.aps.domain.DeliveryZoneTestSamples.*;
import static com.aps.domain.ReturnedOrderTestSamples.*;
import static com.aps.domain.ShoppingCartTestSamples.*;
import static com.aps.domain.TeamMemberTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void ordersTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        CustomerOrder customerOrderBack = getCustomerOrderRandomSampleGenerator();

        customer.addOrders(customerOrderBack);
        assertThat(customer.getOrders()).containsOnly(customerOrderBack);
        assertThat(customerOrderBack.getCustomer()).isEqualTo(customer);

        customer.removeOrders(customerOrderBack);
        assertThat(customer.getOrders()).doesNotContain(customerOrderBack);
        assertThat(customerOrderBack.getCustomer()).isNull();

        customer.orders(new HashSet<>(Set.of(customerOrderBack)));
        assertThat(customer.getOrders()).containsOnly(customerOrderBack);
        assertThat(customerOrderBack.getCustomer()).isEqualTo(customer);

        customer.setOrders(new HashSet<>());
        assertThat(customer.getOrders()).doesNotContain(customerOrderBack);
        assertThat(customerOrderBack.getCustomer()).isNull();
    }

    @Test
    void cartTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        ShoppingCart shoppingCartBack = getShoppingCartRandomSampleGenerator();

        customer.addCart(shoppingCartBack);
        assertThat(customer.getCarts()).containsOnly(shoppingCartBack);
        assertThat(shoppingCartBack.getCustomer()).isEqualTo(customer);

        customer.removeCart(shoppingCartBack);
        assertThat(customer.getCarts()).doesNotContain(shoppingCartBack);
        assertThat(shoppingCartBack.getCustomer()).isNull();

        customer.carts(new HashSet<>(Set.of(shoppingCartBack)));
        assertThat(customer.getCarts()).containsOnly(shoppingCartBack);
        assertThat(shoppingCartBack.getCustomer()).isEqualTo(customer);

        customer.setCarts(new HashSet<>());
        assertThat(customer.getCarts()).doesNotContain(shoppingCartBack);
        assertThat(shoppingCartBack.getCustomer()).isNull();
    }

    @Test
    void returnsTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        ReturnedOrder returnedOrderBack = getReturnedOrderRandomSampleGenerator();

        customer.addReturns(returnedOrderBack);
        assertThat(customer.getReturns()).containsOnly(returnedOrderBack);
        assertThat(returnedOrderBack.getCustomer()).isEqualTo(customer);

        customer.removeReturns(returnedOrderBack);
        assertThat(customer.getReturns()).doesNotContain(returnedOrderBack);
        assertThat(returnedOrderBack.getCustomer()).isNull();

        customer.returns(new HashSet<>(Set.of(returnedOrderBack)));
        assertThat(customer.getReturns()).containsOnly(returnedOrderBack);
        assertThat(returnedOrderBack.getCustomer()).isEqualTo(customer);

        customer.setReturns(new HashSet<>());
        assertThat(customer.getReturns()).doesNotContain(returnedOrderBack);
        assertThat(returnedOrderBack.getCustomer()).isNull();
    }

    @Test
    void addedByTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        TeamMember teamMemberBack = getTeamMemberRandomSampleGenerator();

        customer.setAddedBy(teamMemberBack);
        assertThat(customer.getAddedBy()).isEqualTo(teamMemberBack);

        customer.addedBy(null);
        assertThat(customer.getAddedBy()).isNull();
    }

    @Test
    void zoneTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        DeliveryZone deliveryZoneBack = getDeliveryZoneRandomSampleGenerator();

        customer.setZone(deliveryZoneBack);
        assertThat(customer.getZone()).isEqualTo(deliveryZoneBack);

        customer.zone(null);
        assertThat(customer.getZone()).isNull();
    }
}
