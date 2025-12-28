package com.aps.service.mapper;

import static com.aps.domain.RemovedUserAsserts.*;
import static com.aps.domain.RemovedUserTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemovedUserMapperTest {

    private RemovedUserMapper removedUserMapper;

    @BeforeEach
    void setUp() {
        removedUserMapper = new RemovedUserMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRemovedUserSample1();
        var actual = removedUserMapper.toEntity(removedUserMapper.toDto(expected));
        assertRemovedUserAllPropertiesEquals(expected, actual);
    }
}
