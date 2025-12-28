package com.aps.service.mapper;

import static com.aps.domain.ReturnedOrderItemAsserts.*;
import static com.aps.domain.ReturnedOrderItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReturnedOrderItemMapperTest {

    private ReturnedOrderItemMapper returnedOrderItemMapper;

    @BeforeEach
    void setUp() {
        returnedOrderItemMapper = new ReturnedOrderItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReturnedOrderItemSample1();
        var actual = returnedOrderItemMapper.toEntity(returnedOrderItemMapper.toDto(expected));
        assertReturnedOrderItemAllPropertiesEquals(expected, actual);
    }
}
