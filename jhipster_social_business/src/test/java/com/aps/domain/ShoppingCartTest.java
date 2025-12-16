package com.aps.domain;

import static com.aps.domain.CartItemTestSamples.*;
import static com.aps.domain.CustomerTestSamples.*;
import static com.aps.domain.ShoppingCartTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aps.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ShoppingCartTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShoppingCart.class);
        ShoppingCart shoppingCart1 = getShoppingCartSample1();
        ShoppingCart shoppingCart2 = new ShoppingCart();
        assertThat(shoppingCart1).isNotEqualTo(shoppingCart2);

        shoppingCart2.setId(shoppingCart1.getId());
        assertThat(shoppingCart1).isEqualTo(shoppingCart2);

        shoppingCart2 = getShoppingCartSample2();
        assertThat(shoppingCart1).isNotEqualTo(shoppingCart2);
    }

    @Test
    void itemsTest() {
        ShoppingCart shoppingCart = getShoppingCartRandomSampleGenerator();
        CartItem cartItemBack = getCartItemRandomSampleGenerator();

        shoppingCart.addItems(cartItemBack);
        assertThat(shoppingCart.getItems()).containsOnly(cartItemBack);
        assertThat(cartItemBack.getCart()).isEqualTo(shoppingCart);

        shoppingCart.removeItems(cartItemBack);
        assertThat(shoppingCart.getItems()).doesNotContain(cartItemBack);
        assertThat(cartItemBack.getCart()).isNull();

        shoppingCart.items(new HashSet<>(Set.of(cartItemBack)));
        assertThat(shoppingCart.getItems()).containsOnly(cartItemBack);
        assertThat(cartItemBack.getCart()).isEqualTo(shoppingCart);

        shoppingCart.setItems(new HashSet<>());
        assertThat(shoppingCart.getItems()).doesNotContain(cartItemBack);
        assertThat(cartItemBack.getCart()).isNull();
    }

    @Test
    void customerTest() {
        ShoppingCart shoppingCart = getShoppingCartRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        shoppingCart.setCustomer(customerBack);
        assertThat(shoppingCart.getCustomer()).isEqualTo(customerBack);

        shoppingCart.customer(null);
        assertThat(shoppingCart.getCustomer()).isNull();
    }
}
