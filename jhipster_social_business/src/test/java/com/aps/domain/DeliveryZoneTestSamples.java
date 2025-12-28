package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DeliveryZoneTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DeliveryZone getDeliveryZoneSample1() {
        return new DeliveryZone().id(1L).zoneName("zoneName1").pincode("pincode1");
    }

    public static DeliveryZone getDeliveryZoneSample2() {
        return new DeliveryZone().id(2L).zoneName("zoneName2").pincode("pincode2");
    }

    public static DeliveryZone getDeliveryZoneRandomSampleGenerator() {
        return new DeliveryZone()
            .id(longCount.incrementAndGet())
            .zoneName(UUID.randomUUID().toString())
            .pincode(UUID.randomUUID().toString());
    }
}
