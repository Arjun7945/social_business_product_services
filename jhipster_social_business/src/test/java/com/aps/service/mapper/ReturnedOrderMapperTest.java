package com.aps.service.mapper;

import static com.aps.domain.ReturnedOrderAsserts.*;
import static com.aps.domain.ReturnedOrderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReturnedOrderMapperTest {

    private ReturnedOrderMapper returnedOrderMapper;

    @BeforeEach
    void setUp() {
        returnedOrderMapper = new ReturnedOrderMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReturnedOrderSample1();
        var actual = returnedOrderMapper.toEntity(returnedOrderMapper.toDto(expected));
        assertReturnedOrderAllPropertiesEquals(expected, actual);
    }
}
