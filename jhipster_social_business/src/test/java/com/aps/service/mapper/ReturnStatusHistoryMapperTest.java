package com.aps.service.mapper;

import static com.aps.domain.ReturnStatusHistoryAsserts.*;
import static com.aps.domain.ReturnStatusHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReturnStatusHistoryMapperTest {

    private ReturnStatusHistoryMapper returnStatusHistoryMapper;

    @BeforeEach
    void setUp() {
        returnStatusHistoryMapper = new ReturnStatusHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReturnStatusHistorySample1();
        var actual = returnStatusHistoryMapper.toEntity(returnStatusHistoryMapper.toDto(expected));
        assertReturnStatusHistoryAllPropertiesEquals(expected, actual);
    }
}
