package com.aps.service.mapper;

import static com.aps.domain.ShoppingCartAsserts.*;
import static com.aps.domain.ShoppingCartTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShoppingCartMapperTest {

    private ShoppingCartMapper shoppingCartMapper;

    @BeforeEach
    void setUp() {
        shoppingCartMapper = new ShoppingCartMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getShoppingCartSample1();
        var actual = shoppingCartMapper.toEntity(shoppingCartMapper.toDto(expected));
        assertShoppingCartAllPropertiesEquals(expected, actual);
    }
}
