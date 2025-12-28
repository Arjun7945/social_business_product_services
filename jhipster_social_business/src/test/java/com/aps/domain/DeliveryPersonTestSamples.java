package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DeliveryPersonTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DeliveryPerson getDeliveryPersonSample1() {
        return new DeliveryPerson().id(1L).name("name1").waPhoneNumber("waPhoneNumber1").phoneNumber("phoneNumber1");
    }

    public static DeliveryPerson getDeliveryPersonSample2() {
        return new DeliveryPerson().id(2L).name("name2").waPhoneNumber("waPhoneNumber2").phoneNumber("phoneNumber2");
    }

    public static DeliveryPerson getDeliveryPersonRandomSampleGenerator() {
        return new DeliveryPerson()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .waPhoneNumber(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString());
    }
}
