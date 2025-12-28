package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RemovedOrderSummaryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static RemovedOrderSummary getRemovedOrderSummarySample1() {
        return new RemovedOrderSummary().id(1L).userOriginalId(1L).userName("userName1").totalOrders(1);
    }

    public static RemovedOrderSummary getRemovedOrderSummarySample2() {
        return new RemovedOrderSummary().id(2L).userOriginalId(2L).userName("userName2").totalOrders(2);
    }

    public static RemovedOrderSummary getRemovedOrderSummaryRandomSampleGenerator() {
        return new RemovedOrderSummary()
            .id(longCount.incrementAndGet())
            .userOriginalId(longCount.incrementAndGet())
            .userName(UUID.randomUUID().toString())
            .totalOrders(intCount.incrementAndGet());
    }
}
