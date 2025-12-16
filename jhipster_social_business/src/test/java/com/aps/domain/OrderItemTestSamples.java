package com.aps.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class OrderItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static OrderItem getOrderItemSample1() {
        return new OrderItem().id(1L);
    }

    public static OrderItem getOrderItemSample2() {
        return new OrderItem().id(2L);
    }

    public static OrderItem getOrderItemRandomSampleGenerator() {
        return new OrderItem().id(longCount.incrementAndGet());
    }
}
