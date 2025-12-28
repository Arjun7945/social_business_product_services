package com.aps.service.mapper;

import static com.aps.domain.ButtonActionAsserts.*;
import static com.aps.domain.ButtonActionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ButtonActionMapperTest {

    private ButtonActionMapper buttonActionMapper;

    @BeforeEach
    void setUp() {
        buttonActionMapper = new ButtonActionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getButtonActionSample1();
        var actual = buttonActionMapper.toEntity(buttonActionMapper.toDto(expected));
        assertButtonActionAllPropertiesEquals(expected, actual);
    }
}
