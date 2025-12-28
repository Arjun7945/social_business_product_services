package com.aps.service.mapper;

import static com.aps.domain.RemovedOrderSummaryAsserts.*;
import static com.aps.domain.RemovedOrderSummaryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemovedOrderSummaryMapperTest {

    private RemovedOrderSummaryMapper removedOrderSummaryMapper;

    @BeforeEach
    void setUp() {
        removedOrderSummaryMapper = new RemovedOrderSummaryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRemovedOrderSummarySample1();
        var actual = removedOrderSummaryMapper.toEntity(removedOrderSummaryMapper.toDto(expected));
        assertRemovedOrderSummaryAllPropertiesEquals(expected, actual);
    }
}
