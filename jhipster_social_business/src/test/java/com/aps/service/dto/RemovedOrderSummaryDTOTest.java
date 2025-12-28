package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RemovedOrderSummaryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RemovedOrderSummaryDTO.class);
        RemovedOrderSummaryDTO removedOrderSummaryDTO1 = new RemovedOrderSummaryDTO();
        removedOrderSummaryDTO1.setId(1L);
        RemovedOrderSummaryDTO removedOrderSummaryDTO2 = new RemovedOrderSummaryDTO();
        assertThat(removedOrderSummaryDTO1).isNotEqualTo(removedOrderSummaryDTO2);
        removedOrderSummaryDTO2.setId(removedOrderSummaryDTO1.getId());
        assertThat(removedOrderSummaryDTO1).isEqualTo(removedOrderSummaryDTO2);
        removedOrderSummaryDTO2.setId(2L);
        assertThat(removedOrderSummaryDTO1).isNotEqualTo(removedOrderSummaryDTO2);
        removedOrderSummaryDTO1.setId(null);
        assertThat(removedOrderSummaryDTO1).isNotEqualTo(removedOrderSummaryDTO2);
    }
}
