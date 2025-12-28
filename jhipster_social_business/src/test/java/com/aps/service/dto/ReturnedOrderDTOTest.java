package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnedOrderDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnedOrderDTO.class);
        ReturnedOrderDTO returnedOrderDTO1 = new ReturnedOrderDTO();
        returnedOrderDTO1.setId(1L);
        ReturnedOrderDTO returnedOrderDTO2 = new ReturnedOrderDTO();
        assertThat(returnedOrderDTO1).isNotEqualTo(returnedOrderDTO2);
        returnedOrderDTO2.setId(returnedOrderDTO1.getId());
        assertThat(returnedOrderDTO1).isEqualTo(returnedOrderDTO2);
        returnedOrderDTO2.setId(2L);
        assertThat(returnedOrderDTO1).isNotEqualTo(returnedOrderDTO2);
        returnedOrderDTO1.setId(null);
        assertThat(returnedOrderDTO1).isNotEqualTo(returnedOrderDTO2);
    }
}
