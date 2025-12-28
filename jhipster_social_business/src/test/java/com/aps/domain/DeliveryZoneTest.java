package com.aps.domain;

import static com.aps.domain.CustomerTestSamples.*;
import static com.aps.domain.DeliveryPersonTestSamples.*;
import static com.aps.domain.DeliveryZoneTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DeliveryZoneTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeliveryZone.class);
        DeliveryZone deliveryZone1 = getDeliveryZoneSample1();
        DeliveryZone deliveryZone2 = new DeliveryZone();
        assertThat(deliveryZone1).isNotEqualTo(deliveryZone2);

        deliveryZone2.setId(deliveryZone1.getId());
        assertThat(deliveryZone1).isEqualTo(deliveryZone2);

        deliveryZone2 = getDeliveryZoneSample2();
        assertThat(deliveryZone1).isNotEqualTo(deliveryZone2);
    }

    @Test
    void customersTest() {
        DeliveryZone deliveryZone = getDeliveryZoneRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        deliveryZone.addCustomers(customerBack);
        assertThat(deliveryZone.getCustomers()).containsOnly(customerBack);
        assertThat(customerBack.getZone()).isEqualTo(deliveryZone);

        deliveryZone.removeCustomers(customerBack);
        assertThat(deliveryZone.getCustomers()).doesNotContain(customerBack);
        assertThat(customerBack.getZone()).isNull();

        deliveryZone.customers(new HashSet<>(Set.of(customerBack)));
        assertThat(deliveryZone.getCustomers()).containsOnly(customerBack);
        assertThat(customerBack.getZone()).isEqualTo(deliveryZone);

        deliveryZone.setCustomers(new HashSet<>());
        assertThat(deliveryZone.getCustomers()).doesNotContain(customerBack);
        assertThat(customerBack.getZone()).isNull();
    }

    @Test
    void deliveryPersonsTest() {
        DeliveryZone deliveryZone = getDeliveryZoneRandomSampleGenerator();
        DeliveryPerson deliveryPersonBack = getDeliveryPersonRandomSampleGenerator();

        deliveryZone.addDeliveryPersons(deliveryPersonBack);
        assertThat(deliveryZone.getDeliveryPersons()).containsOnly(deliveryPersonBack);
        assertThat(deliveryPersonBack.getZone()).isEqualTo(deliveryZone);

        deliveryZone.removeDeliveryPersons(deliveryPersonBack);
        assertThat(deliveryZone.getDeliveryPersons()).doesNotContain(deliveryPersonBack);
        assertThat(deliveryPersonBack.getZone()).isNull();

        deliveryZone.deliveryPersons(new HashSet<>(Set.of(deliveryPersonBack)));
        assertThat(deliveryZone.getDeliveryPersons()).containsOnly(deliveryPersonBack);
        assertThat(deliveryPersonBack.getZone()).isEqualTo(deliveryZone);

        deliveryZone.setDeliveryPersons(new HashSet<>());
        assertThat(deliveryZone.getDeliveryPersons()).doesNotContain(deliveryPersonBack);
        assertThat(deliveryPersonBack.getZone()).isNull();
    }
}
