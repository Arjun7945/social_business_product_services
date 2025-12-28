package com.aps.service.mapper;

import static com.aps.domain.DeliveryZoneAsserts.*;
import static com.aps.domain.DeliveryZoneTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeliveryZoneMapperTest {

    private DeliveryZoneMapper deliveryZoneMapper;

    @BeforeEach
    void setUp() {
        deliveryZoneMapper = new DeliveryZoneMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDeliveryZoneSample1();
        var actual = deliveryZoneMapper.toEntity(deliveryZoneMapper.toDto(expected));
        assertDeliveryZoneAllPropertiesEquals(expected, actual);
    }
}
