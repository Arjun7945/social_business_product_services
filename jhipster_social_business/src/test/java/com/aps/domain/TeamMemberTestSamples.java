package com.aps.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TeamMemberTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TeamMember getTeamMemberSample1() {
        return new TeamMember().id(1L).name("name1").waPhoneNumber("waPhoneNumber1").phoneNumber("phoneNumber1");
    }

    public static TeamMember getTeamMemberSample2() {
        return new TeamMember().id(2L).name("name2").waPhoneNumber("waPhoneNumber2").phoneNumber("phoneNumber2");
    }

    public static TeamMember getTeamMemberRandomSampleGenerator() {
        return new TeamMember()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .waPhoneNumber(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString());
    }
}
