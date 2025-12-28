package com.aps.domain;

import static com.aps.domain.RemovedUserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RemovedUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RemovedUser.class);
        RemovedUser removedUser1 = getRemovedUserSample1();
        RemovedUser removedUser2 = new RemovedUser();
        assertThat(removedUser1).isNotEqualTo(removedUser2);

        removedUser2.setId(removedUser1.getId());
        assertThat(removedUser1).isEqualTo(removedUser2);

        removedUser2 = getRemovedUserSample2();
        assertThat(removedUser1).isNotEqualTo(removedUser2);
    }
}
