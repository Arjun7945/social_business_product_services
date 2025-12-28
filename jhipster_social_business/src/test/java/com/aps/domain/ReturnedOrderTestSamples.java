package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReturnedOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ReturnedOrder getReturnedOrderSample1() {
        return new ReturnedOrder().id(1L).paymentReceivedMode("paymentReceivedMode1").paymentReturnedMode("paymentReturnedMode1");
    }

    public static ReturnedOrder getReturnedOrderSample2() {
        return new ReturnedOrder().id(2L).paymentReceivedMode("paymentReceivedMode2").paymentReturnedMode("paymentReturnedMode2");
    }

    public static ReturnedOrder getReturnedOrderRandomSampleGenerator() {
        return new ReturnedOrder()
            .id(longCount.incrementAndGet())
            .paymentReceivedMode(UUID.randomUUID().toString())
            .paymentReturnedMode(UUID.randomUUID().toString());
    }
}
