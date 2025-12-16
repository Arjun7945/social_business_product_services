package com.aps.domain;

import static com.aps.domain.CartItemTestSamples.*;
import static com.aps.domain.FishProductTestSamples.*;
import static com.aps.domain.ShoppingCartTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartItem.class);
        CartItem cartItem1 = getCartItemSample1();
        CartItem cartItem2 = new CartItem();
        assertThat(cartItem1).isNotEqualTo(cartItem2);

        cartItem2.setId(cartItem1.getId());
        assertThat(cartItem1).isEqualTo(cartItem2);

        cartItem2 = getCartItemSample2();
        assertThat(cartItem1).isNotEqualTo(cartItem2);
    }

    @Test
    void productTest() {
        CartItem cartItem = getCartItemRandomSampleGenerator();
        FishProduct fishProductBack = getFishProductRandomSampleGenerator();

        cartItem.setProduct(fishProductBack);
        assertThat(cartItem.getProduct()).isEqualTo(fishProductBack);

        cartItem.product(null);
        assertThat(cartItem.getProduct()).isNull();
    }

    @Test
    void cartTest() {
        CartItem cartItem = getCartItemRandomSampleGenerator();
        ShoppingCart shoppingCartBack = getShoppingCartRandomSampleGenerator();

        cartItem.setCart(shoppingCartBack);
        assertThat(cartItem.getCart()).isEqualTo(shoppingCartBack);

        cartItem.cart(null);
        assertThat(cartItem.getCart()).isNull();
    }
}
