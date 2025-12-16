package com.aps.service.mapper;

import static com.aps.domain.TeamMemberAsserts.*;
import static com.aps.domain.TeamMemberTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeamMemberMapperTest {

    private TeamMemberMapper teamMemberMapper;

    @BeforeEach
    void setUp() {
        teamMemberMapper = new TeamMemberMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTeamMemberSample1();
        var actual = teamMemberMapper.toEntity(teamMemberMapper.toDto(expected));
        assertTeamMemberAllPropertiesEquals(expected, actual);
    }
}
