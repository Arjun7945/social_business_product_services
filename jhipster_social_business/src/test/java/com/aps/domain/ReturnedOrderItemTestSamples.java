package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReturnedOrderItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ReturnedOrderItem getReturnedOrderItemSample1() {
        return new ReturnedOrderItem().id(1L).productComment("productComment1");
    }

    public static ReturnedOrderItem getReturnedOrderItemSample2() {
        return new ReturnedOrderItem().id(2L).productComment("productComment2");
    }

    public static ReturnedOrderItem getReturnedOrderItemRandomSampleGenerator() {
        return new ReturnedOrderItem().id(longCount.incrementAndGet()).productComment(UUID.randomUUID().toString());
    }
}
