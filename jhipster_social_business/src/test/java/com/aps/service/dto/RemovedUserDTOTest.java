package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RemovedUserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RemovedUserDTO.class);
        RemovedUserDTO removedUserDTO1 = new RemovedUserDTO();
        removedUserDTO1.setId(1L);
        RemovedUserDTO removedUserDTO2 = new RemovedUserDTO();
        assertThat(removedUserDTO1).isNotEqualTo(removedUserDTO2);
        removedUserDTO2.setId(removedUserDTO1.getId());
        assertThat(removedUserDTO1).isEqualTo(removedUserDTO2);
        removedUserDTO2.setId(2L);
        assertThat(removedUserDTO1).isNotEqualTo(removedUserDTO2);
        removedUserDTO1.setId(null);
        assertThat(removedUserDTO1).isNotEqualTo(removedUserDTO2);
    }
}
