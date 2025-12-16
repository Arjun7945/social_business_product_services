package com.aps.service.mapper;

import static com.aps.domain.CartItemAsserts.*;
import static com.aps.domain.CartItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartItemMapperTest {

    private CartItemMapper cartItemMapper;

    @BeforeEach
    void setUp() {
        cartItemMapper = new CartItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCartItemSample1();
        var actual = cartItemMapper.toEntity(cartItemMapper.toDto(expected));
        assertCartItemAllPropertiesEquals(expected, actual);
    }
}
