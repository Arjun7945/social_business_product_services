package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerOrderDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerOrderDTO.class);
        CustomerOrderDTO customerOrderDTO1 = new CustomerOrderDTO();
        customerOrderDTO1.setId(1L);
        CustomerOrderDTO customerOrderDTO2 = new CustomerOrderDTO();
        assertThat(customerOrderDTO1).isNotEqualTo(customerOrderDTO2);
        customerOrderDTO2.setId(customerOrderDTO1.getId());
        assertThat(customerOrderDTO1).isEqualTo(customerOrderDTO2);
        customerOrderDTO2.setId(2L);
        assertThat(customerOrderDTO1).isNotEqualTo(customerOrderDTO2);
        customerOrderDTO1.setId(null);
        assertThat(customerOrderDTO1).isNotEqualTo(customerOrderDTO2);
    }
}
