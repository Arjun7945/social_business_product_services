package com.aps.domain;

import static com.aps.domain.ButtonActionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ButtonActionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ButtonAction.class);
        ButtonAction buttonAction1 = getButtonActionSample1();
        ButtonAction buttonAction2 = new ButtonAction();
        assertThat(buttonAction1).isNotEqualTo(buttonAction2);

        buttonAction2.setId(buttonAction1.getId());
        assertThat(buttonAction1).isEqualTo(buttonAction2);

        buttonAction2 = getButtonActionSample2();
        assertThat(buttonAction1).isNotEqualTo(buttonAction2);
    }
}
