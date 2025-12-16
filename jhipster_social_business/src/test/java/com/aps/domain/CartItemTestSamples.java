package com.aps.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CartItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CartItem getCartItemSample1() {
        return new CartItem().id(1L);
    }

    public static CartItem getCartItemSample2() {
        return new CartItem().id(2L);
    }

    public static CartItem getCartItemRandomSampleGenerator() {
        return new CartItem().id(longCount.incrementAndGet());
    }
}
