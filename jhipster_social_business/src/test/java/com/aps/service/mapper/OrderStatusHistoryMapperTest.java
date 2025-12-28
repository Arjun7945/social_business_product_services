package com.aps.service.mapper;

import static com.aps.domain.OrderStatusHistoryAsserts.*;
import static com.aps.domain.OrderStatusHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderStatusHistoryMapperTest {

    private OrderStatusHistoryMapper orderStatusHistoryMapper;

    @BeforeEach
    void setUp() {
        orderStatusHistoryMapper = new OrderStatusHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOrderStatusHistorySample1();
        var actual = orderStatusHistoryMapper.toEntity(orderStatusHistoryMapper.toDto(expected));
        assertOrderStatusHistoryAllPropertiesEquals(expected, actual);
    }
}
