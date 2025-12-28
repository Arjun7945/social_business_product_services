package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnedOrderItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnedOrderItemDTO.class);
        ReturnedOrderItemDTO returnedOrderItemDTO1 = new ReturnedOrderItemDTO();
        returnedOrderItemDTO1.setId(1L);
        ReturnedOrderItemDTO returnedOrderItemDTO2 = new ReturnedOrderItemDTO();
        assertThat(returnedOrderItemDTO1).isNotEqualTo(returnedOrderItemDTO2);
        returnedOrderItemDTO2.setId(returnedOrderItemDTO1.getId());
        assertThat(returnedOrderItemDTO1).isEqualTo(returnedOrderItemDTO2);
        returnedOrderItemDTO2.setId(2L);
        assertThat(returnedOrderItemDTO1).isNotEqualTo(returnedOrderItemDTO2);
        returnedOrderItemDTO1.setId(null);
        assertThat(returnedOrderItemDTO1).isNotEqualTo(returnedOrderItemDTO2);
    }
}
