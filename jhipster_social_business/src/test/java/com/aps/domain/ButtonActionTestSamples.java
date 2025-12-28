package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ButtonActionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ButtonAction getButtonActionSample1() {
        return new ButtonAction().id(1L).waMessageId("waMessageId1").buttonId("buttonId1").clickedBy("clickedBy1");
    }

    public static ButtonAction getButtonActionSample2() {
        return new ButtonAction().id(2L).waMessageId("waMessageId2").buttonId("buttonId2").clickedBy("clickedBy2");
    }

    public static ButtonAction getButtonActionRandomSampleGenerator() {
        return new ButtonAction()
            .id(longCount.incrementAndGet())
            .waMessageId(UUID.randomUUID().toString())
            .buttonId(UUID.randomUUID().toString())
            .clickedBy(UUID.randomUUID().toString());
    }
}
