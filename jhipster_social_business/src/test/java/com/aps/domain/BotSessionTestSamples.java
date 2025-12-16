package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BotSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BotSession getBotSessionSample1() {
        return new BotSession().id(1L).waPhoneNumber("waPhoneNumber1").currentState("currentState1");
    }

    public static BotSession getBotSessionSample2() {
        return new BotSession().id(2L).waPhoneNumber("waPhoneNumber2").currentState("currentState2");
    }

    public static BotSession getBotSessionRandomSampleGenerator() {
        return new BotSession()
            .id(longCount.incrementAndGet())
            .waPhoneNumber(UUID.randomUUID().toString())
            .currentState(UUID.randomUUID().toString());
    }
}
