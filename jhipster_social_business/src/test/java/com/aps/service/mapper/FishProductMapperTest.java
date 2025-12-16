package com.aps.service.mapper;

import static com.aps.domain.FishProductAsserts.*;
import static com.aps.domain.FishProductTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FishProductMapperTest {

    private FishProductMapper fishProductMapper;

    @BeforeEach
    void setUp() {
        fishProductMapper = new FishProductMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFishProductSample1();
        var actual = fishProductMapper.toEntity(fishProductMapper.toDto(expected));
        assertFishProductAllPropertiesEquals(expected, actual);
    }
}
