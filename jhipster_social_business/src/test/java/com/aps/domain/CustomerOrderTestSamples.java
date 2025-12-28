package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CustomerOrder getCustomerOrderSample1() {
        return new CustomerOrder()
            .id(1L)
            .paymentMethod("paymentMethod1")
            .removedCustomerId(1L)
            .removedDeliveryPersonId(1L)
            .transactionId("transactionId1");
    }

    public static CustomerOrder getCustomerOrderSample2() {
        return new CustomerOrder()
            .id(2L)
            .paymentMethod("paymentMethod2")
            .removedCustomerId(2L)
            .removedDeliveryPersonId(2L)
            .transactionId("transactionId2");
    }

    public static CustomerOrder getCustomerOrderRandomSampleGenerator() {
        return new CustomerOrder()
            .id(longCount.incrementAndGet())
            .paymentMethod(UUID.randomUUID().toString())
            .removedCustomerId(longCount.incrementAndGet())
            .removedDeliveryPersonId(longCount.incrementAndGet())
            .transactionId(UUID.randomUUID().toString());
    }
}
