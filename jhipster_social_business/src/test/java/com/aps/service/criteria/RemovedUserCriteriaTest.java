package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RemovedUserCriteriaTest {

    @Test
    void newRemovedUserCriteriaHasAllFiltersNullTest() {
        var removedUserCriteria = new RemovedUserCriteria();
        assertThat(removedUserCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void removedUserCriteriaFluentMethodsCreatesFiltersTest() {
        var removedUserCriteria = new RemovedUserCriteria();

        setAllFilters(removedUserCriteria);

        assertThat(removedUserCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void removedUserCriteriaCopyCreatesNullFilterTest() {
        var removedUserCriteria = new RemovedUserCriteria();
        var copy = removedUserCriteria.copy();

        assertThat(removedUserCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(removedUserCriteria)
        );
    }

    @Test
    void removedUserCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var removedUserCriteria = new RemovedUserCriteria();
        setAllFilters(removedUserCriteria);

        var copy = removedUserCriteria.copy();

        assertThat(removedUserCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(removedUserCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var removedUserCriteria = new RemovedUserCriteria();

        assertThat(removedUserCriteria).hasToString("RemovedUserCriteria{}");
    }

    private static void setAllFilters(RemovedUserCriteria removedUserCriteria) {
        removedUserCriteria.id();
        removedUserCriteria.originalId();
        removedUserCriteria.name();
        removedUserCriteria.role();
        removedUserCriteria.whatsappNumber();
        removedUserCriteria.phoneNumber();
        removedUserCriteria.address();
        removedUserCriteria.locationLat();
        removedUserCriteria.locationLon();
        removedUserCriteria.joinedAt();
        removedUserCriteria.removedAt();
        removedUserCriteria.reasonForRemoval();
        removedUserCriteria.status();
        removedUserCriteria.orderHistoryId();
        removedUserCriteria.distanceFromBusinessKm();
        removedUserCriteria.isPincodeValid();
        removedUserCriteria.distinct();
    }

    private static Condition<RemovedUserCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOriginalId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getRole()) &&
                condition.apply(criteria.getWhatsappNumber()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getLocationLat()) &&
                condition.apply(criteria.getLocationLon()) &&
                condition.apply(criteria.getJoinedAt()) &&
                condition.apply(criteria.getRemovedAt()) &&
                condition.apply(criteria.getReasonForRemoval()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getOrderHistoryId()) &&
                condition.apply(criteria.getDistanceFromBusinessKm()) &&
                condition.apply(criteria.getIsPincodeValid()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RemovedUserCriteria> copyFiltersAre(RemovedUserCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getOriginalId(), copy.getOriginalId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getRole(), copy.getRole()) &&
                condition.apply(criteria.getWhatsappNumber(), copy.getWhatsappNumber()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getLocationLat(), copy.getLocationLat()) &&
                condition.apply(criteria.getLocationLon(), copy.getLocationLon()) &&
                condition.apply(criteria.getJoinedAt(), copy.getJoinedAt()) &&
                condition.apply(criteria.getRemovedAt(), copy.getRemovedAt()) &&
                condition.apply(criteria.getReasonForRemoval(), copy.getReasonForRemoval()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getOrderHistoryId(), copy.getOrderHistoryId()) &&
                condition.apply(criteria.getDistanceFromBusinessKm(), copy.getDistanceFromBusinessKm()) &&
                condition.apply(criteria.getIsPincodeValid(), copy.getIsPincodeValid()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
