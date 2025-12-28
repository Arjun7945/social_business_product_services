package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RemovedUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static RemovedUser getRemovedUserSample1() {
        return new RemovedUser()
            .id(1L)
            .originalId(1L)
            .name("name1")
            .whatsappNumber("whatsappNumber1")
            .phoneNumber("phoneNumber1")
            .address("address1")
            .reasonForRemoval("reasonForRemoval1")
            .orderHistoryId(1L);
    }

    public static RemovedUser getRemovedUserSample2() {
        return new RemovedUser()
            .id(2L)
            .originalId(2L)
            .name("name2")
            .whatsappNumber("whatsappNumber2")
            .phoneNumber("phoneNumber2")
            .address("address2")
            .reasonForRemoval("reasonForRemoval2")
            .orderHistoryId(2L);
    }

    public static RemovedUser getRemovedUserRandomSampleGenerator() {
        return new RemovedUser()
            .id(longCount.incrementAndGet())
            .originalId(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .whatsappNumber(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .reasonForRemoval(UUID.randomUUID().toString())
            .orderHistoryId(longCount.incrementAndGet());
    }
}
