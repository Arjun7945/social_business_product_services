package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeliveryZoneDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeliveryZoneDTO.class);
        DeliveryZoneDTO deliveryZoneDTO1 = new DeliveryZoneDTO();
        deliveryZoneDTO1.setId(1L);
        DeliveryZoneDTO deliveryZoneDTO2 = new DeliveryZoneDTO();
        assertThat(deliveryZoneDTO1).isNotEqualTo(deliveryZoneDTO2);
        deliveryZoneDTO2.setId(deliveryZoneDTO1.getId());
        assertThat(deliveryZoneDTO1).isEqualTo(deliveryZoneDTO2);
        deliveryZoneDTO2.setId(2L);
        assertThat(deliveryZoneDTO1).isNotEqualTo(deliveryZoneDTO2);
        deliveryZoneDTO1.setId(null);
        assertThat(deliveryZoneDTO1).isNotEqualTo(deliveryZoneDTO2);
    }
}
