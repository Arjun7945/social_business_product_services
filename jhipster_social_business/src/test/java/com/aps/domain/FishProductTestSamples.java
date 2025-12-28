package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FishProductTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FishProduct getFishProductSample1() {
        return new FishProduct().id(1L).name("name1").description("description1");
    }

    public static FishProduct getFishProductSample2() {
        return new FishProduct().id(2L).name("name2").description("description2");
    }

    public static FishProduct getFishProductRandomSampleGenerator() {
        return new FishProduct()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
