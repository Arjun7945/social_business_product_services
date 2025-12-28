package com.aps.service.mapper;

import static com.aps.domain.DeliveryPersonAsserts.*;
import static com.aps.domain.DeliveryPersonTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeliveryPersonMapperTest {

    private DeliveryPersonMapper deliveryPersonMapper;

    @BeforeEach
    void setUp() {
        deliveryPersonMapper = new DeliveryPersonMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDeliveryPersonSample1();
        var actual = deliveryPersonMapper.toEntity(deliveryPersonMapper.toDto(expected));
        assertDeliveryPersonAllPropertiesEquals(expected, actual);
    }
}
