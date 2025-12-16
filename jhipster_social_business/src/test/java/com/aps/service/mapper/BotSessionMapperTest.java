package com.aps.service.mapper;

import static com.aps.domain.BotSessionAsserts.*;
import static com.aps.domain.BotSessionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BotSessionMapperTest {

    private BotSessionMapper botSessionMapper;

    @BeforeEach
    void setUp() {
        botSessionMapper = new BotSessionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBotSessionSample1();
        var actual = botSessionMapper.toEntity(botSessionMapper.toDto(expected));
        assertBotSessionAllPropertiesEquals(expected, actual);
    }
}
