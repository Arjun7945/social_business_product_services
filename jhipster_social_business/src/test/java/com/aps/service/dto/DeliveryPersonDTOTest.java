package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeliveryPersonDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeliveryPersonDTO.class);
        DeliveryPersonDTO deliveryPersonDTO1 = new DeliveryPersonDTO();
        deliveryPersonDTO1.setId(1L);
        DeliveryPersonDTO deliveryPersonDTO2 = new DeliveryPersonDTO();
        assertThat(deliveryPersonDTO1).isNotEqualTo(deliveryPersonDTO2);
        deliveryPersonDTO2.setId(deliveryPersonDTO1.getId());
        assertThat(deliveryPersonDTO1).isEqualTo(deliveryPersonDTO2);
        deliveryPersonDTO2.setId(2L);
        assertThat(deliveryPersonDTO1).isNotEqualTo(deliveryPersonDTO2);
        deliveryPersonDTO1.setId(null);
        assertThat(deliveryPersonDTO1).isNotEqualTo(deliveryPersonDTO2);
    }
}
