package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Customer getCustomerSample1() {
        return new Customer().id(1L).waPhoneNumber("waPhoneNumber1").name("name1").phoneNumber("phoneNumber1").address("address1");
    }

    public static Customer getCustomerSample2() {
        return new Customer().id(2L).waPhoneNumber("waPhoneNumber2").name("name2").phoneNumber("phoneNumber2").address("address2");
    }

    public static Customer getCustomerRandomSampleGenerator() {
        return new Customer()
            .id(longCount.incrementAndGet())
            .waPhoneNumber(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString());
    }
}
