package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ButtonActionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ButtonActionDTO.class);
        ButtonActionDTO buttonActionDTO1 = new ButtonActionDTO();
        buttonActionDTO1.setId(1L);
        ButtonActionDTO buttonActionDTO2 = new ButtonActionDTO();
        assertThat(buttonActionDTO1).isNotEqualTo(buttonActionDTO2);
        buttonActionDTO2.setId(buttonActionDTO1.getId());
        assertThat(buttonActionDTO1).isEqualTo(buttonActionDTO2);
        buttonActionDTO2.setId(2L);
        assertThat(buttonActionDTO1).isNotEqualTo(buttonActionDTO2);
        buttonActionDTO1.setId(null);
        assertThat(buttonActionDTO1).isNotEqualTo(buttonActionDTO2);
    }
}
