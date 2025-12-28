package com.aps.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ReturnStatusHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ReturnStatusHistory getReturnStatusHistorySample1() {
        return new ReturnStatusHistory().id(1L);
    }

    public static ReturnStatusHistory getReturnStatusHistorySample2() {
        return new ReturnStatusHistory().id(2L);
    }

    public static ReturnStatusHistory getReturnStatusHistoryRandomSampleGenerator() {
        return new ReturnStatusHistory().id(longCount.incrementAndGet());
    }
}
