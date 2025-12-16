package com.aps.domain;

import static com.aps.domain.BotSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BotSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BotSession.class);
        BotSession botSession1 = getBotSessionSample1();
        BotSession botSession2 = new BotSession();
        assertThat(botSession1).isNotEqualTo(botSession2);

        botSession2.setId(botSession1.getId());
        assertThat(botSession1).isEqualTo(botSession2);

        botSession2 = getBotSessionSample2();
        assertThat(botSession1).isNotEqualTo(botSession2);
    }
}
