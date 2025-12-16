package com.aps.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamMemberDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamMemberDTO.class);
        TeamMemberDTO teamMemberDTO1 = new TeamMemberDTO();
        teamMemberDTO1.setId(1L);
        TeamMemberDTO teamMemberDTO2 = new TeamMemberDTO();
        assertThat(teamMemberDTO1).isNotEqualTo(teamMemberDTO2);
        teamMemberDTO2.setId(teamMemberDTO1.getId());
        assertThat(teamMemberDTO1).isEqualTo(teamMemberDTO2);
        teamMemberDTO2.setId(2L);
        assertThat(teamMemberDTO1).isNotEqualTo(teamMemberDTO2);
        teamMemberDTO1.setId(null);
        assertThat(teamMemberDTO1).isNotEqualTo(teamMemberDTO2);
    }
}
