package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItemDTO.class);
        OrderItemDTO orderItemDTO1 = new OrderItemDTO();
        orderItemDTO1.setId(1L);
        OrderItemDTO orderItemDTO2 = new OrderItemDTO();
        assertThat(orderItemDTO1).isNotEqualTo(orderItemDTO2);
        orderItemDTO2.setId(orderItemDTO1.getId());
        assertThat(orderItemDTO1).isEqualTo(orderItemDTO2);
        orderItemDTO2.setId(2L);
        assertThat(orderItemDTO1).isNotEqualTo(orderItemDTO2);
        orderItemDTO1.setId(null);
        assertThat(orderItemDTO1).isNotEqualTo(orderItemDTO2);
    }
}
