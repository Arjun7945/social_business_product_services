package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderStatusHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderStatusHistoryDTO.class);
        OrderStatusHistoryDTO orderStatusHistoryDTO1 = new OrderStatusHistoryDTO();
        orderStatusHistoryDTO1.setId(1L);
        OrderStatusHistoryDTO orderStatusHistoryDTO2 = new OrderStatusHistoryDTO();
        assertThat(orderStatusHistoryDTO1).isNotEqualTo(orderStatusHistoryDTO2);
        orderStatusHistoryDTO2.setId(orderStatusHistoryDTO1.getId());
        assertThat(orderStatusHistoryDTO1).isEqualTo(orderStatusHistoryDTO2);
        orderStatusHistoryDTO2.setId(2L);
        assertThat(orderStatusHistoryDTO1).isNotEqualTo(orderStatusHistoryDTO2);
        orderStatusHistoryDTO1.setId(null);
        assertThat(orderStatusHistoryDTO1).isNotEqualTo(orderStatusHistoryDTO2);
    }
}
