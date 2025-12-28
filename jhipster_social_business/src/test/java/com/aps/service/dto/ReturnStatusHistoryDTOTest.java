package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnStatusHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnStatusHistoryDTO.class);
        ReturnStatusHistoryDTO returnStatusHistoryDTO1 = new ReturnStatusHistoryDTO();
        returnStatusHistoryDTO1.setId(1L);
        ReturnStatusHistoryDTO returnStatusHistoryDTO2 = new ReturnStatusHistoryDTO();
        assertThat(returnStatusHistoryDTO1).isNotEqualTo(returnStatusHistoryDTO2);
        returnStatusHistoryDTO2.setId(returnStatusHistoryDTO1.getId());
        assertThat(returnStatusHistoryDTO1).isEqualTo(returnStatusHistoryDTO2);
        returnStatusHistoryDTO2.setId(2L);
        assertThat(returnStatusHistoryDTO1).isNotEqualTo(returnStatusHistoryDTO2);
        returnStatusHistoryDTO1.setId(null);
        assertThat(returnStatusHistoryDTO1).isNotEqualTo(returnStatusHistoryDTO2);
    }
}
