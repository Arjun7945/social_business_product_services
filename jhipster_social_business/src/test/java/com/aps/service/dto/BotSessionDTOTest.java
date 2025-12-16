package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BotSessionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BotSessionDTO.class);
        BotSessionDTO botSessionDTO1 = new BotSessionDTO();
        botSessionDTO1.setId(1L);
        BotSessionDTO botSessionDTO2 = new BotSessionDTO();
        assertThat(botSessionDTO1).isNotEqualTo(botSessionDTO2);
        botSessionDTO2.setId(botSessionDTO1.getId());
        assertThat(botSessionDTO1).isEqualTo(botSessionDTO2);
        botSessionDTO2.setId(2L);
        assertThat(botSessionDTO1).isNotEqualTo(botSessionDTO2);
        botSessionDTO1.setId(null);
        assertThat(botSessionDTO1).isNotEqualTo(botSessionDTO2);
    }
}
