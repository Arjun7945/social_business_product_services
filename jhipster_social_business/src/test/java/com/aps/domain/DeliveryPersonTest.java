package com.aps.domain;

import static com.aps.domain.CustomerOrderTestSamples.*;
import static com.aps.domain.DeliveryPersonTestSamples.*;
import static com.aps.domain.DeliveryZoneTestSamples.*;
import static com.aps.domain.TeamMemberTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DeliveryPersonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeliveryPerson.class);
        DeliveryPerson deliveryPerson1 = getDeliveryPersonSample1();
        DeliveryPerson deliveryPerson2 = new DeliveryPerson();
        assertThat(deliveryPerson1).isNotEqualTo(deliveryPerson2);

        deliveryPerson2.setId(deliveryPerson1.getId());
        assertThat(deliveryPerson1).isEqualTo(deliveryPerson2);

        deliveryPerson2 = getDeliveryPersonSample2();
        assertThat(deliveryPerson1).isNotEqualTo(deliveryPerson2);
    }

    @Test
    void ordersTest() {
        DeliveryPerson deliveryPerson = getDeliveryPersonRandomSampleGenerator();
        CustomerOrder customerOrderBack = getCustomerOrderRandomSampleGenerator();

        deliveryPerson.addOrders(customerOrderBack);
        assertThat(deliveryPerson.getOrders()).containsOnly(customerOrderBack);
        assertThat(customerOrderBack.getDeliveryPerson()).isEqualTo(deliveryPerson);

        deliveryPerson.removeOrders(customerOrderBack);
        assertThat(deliveryPerson.getOrders()).doesNotContain(customerOrderBack);
        assertThat(customerOrderBack.getDeliveryPerson()).isNull();

        deliveryPerson.orders(new HashSet<>(Set.of(customerOrderBack)));
        assertThat(deliveryPerson.getOrders()).containsOnly(customerOrderBack);
        assertThat(customerOrderBack.getDeliveryPerson()).isEqualTo(deliveryPerson);

        deliveryPerson.setOrders(new HashSet<>());
        assertThat(deliveryPerson.getOrders()).doesNotContain(customerOrderBack);
        assertThat(customerOrderBack.getDeliveryPerson()).isNull();
    }

    @Test
    void addedByTest() {
        DeliveryPerson deliveryPerson = getDeliveryPersonRandomSampleGenerator();
        TeamMember teamMemberBack = getTeamMemberRandomSampleGenerator();

        deliveryPerson.setAddedBy(teamMemberBack);
        assertThat(deliveryPerson.getAddedBy()).isEqualTo(teamMemberBack);

        deliveryPerson.addedBy(null);
        assertThat(deliveryPerson.getAddedBy()).isNull();
    }

    @Test
    void zoneTest() {
        DeliveryPerson deliveryPerson = getDeliveryPersonRandomSampleGenerator();
        DeliveryZone deliveryZoneBack = getDeliveryZoneRandomSampleGenerator();

        deliveryPerson.setZone(deliveryZoneBack);
        assertThat(deliveryPerson.getZone()).isEqualTo(deliveryZoneBack);

        deliveryPerson.zone(null);
        assertThat(deliveryPerson.getZone()).isNull();
    }
}
