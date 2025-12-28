package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DeliveryPersonCriteriaTest {

    @Test
    void newDeliveryPersonCriteriaHasAllFiltersNullTest() {
        var deliveryPersonCriteria = new DeliveryPersonCriteria();
        assertThat(deliveryPersonCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void deliveryPersonCriteriaFluentMethodsCreatesFiltersTest() {
        var deliveryPersonCriteria = new DeliveryPersonCriteria();

        setAllFilters(deliveryPersonCriteria);

        assertThat(deliveryPersonCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void deliveryPersonCriteriaCopyCreatesNullFilterTest() {
        var deliveryPersonCriteria = new DeliveryPersonCriteria();
        var copy = deliveryPersonCriteria.copy();

        assertThat(deliveryPersonCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(deliveryPersonCriteria)
        );
    }

    @Test
    void deliveryPersonCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var deliveryPersonCriteria = new DeliveryPersonCriteria();
        setAllFilters(deliveryPersonCriteria);

        var copy = deliveryPersonCriteria.copy();

        assertThat(deliveryPersonCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(deliveryPersonCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var deliveryPersonCriteria = new DeliveryPersonCriteria();

        assertThat(deliveryPersonCriteria).hasToString("DeliveryPersonCriteria{}");
    }

    private static void setAllFilters(DeliveryPersonCriteria deliveryPersonCriteria) {
        deliveryPersonCriteria.id();
        deliveryPersonCriteria.name();
        deliveryPersonCriteria.waPhoneNumber();
        deliveryPersonCriteria.phoneNumber();
        deliveryPersonCriteria.status();
        deliveryPersonCriteria.joinedAt();
        deliveryPersonCriteria.isActive();
        deliveryPersonCriteria.ordersId();
        deliveryPersonCriteria.addedById();
        deliveryPersonCriteria.zoneId();
        deliveryPersonCriteria.distinct();
    }

    private static Condition<DeliveryPersonCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getWaPhoneNumber()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getJoinedAt()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getOrdersId()) &&
                condition.apply(criteria.getAddedById()) &&
                condition.apply(criteria.getZoneId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DeliveryPersonCriteria> copyFiltersAre(
        DeliveryPersonCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getWaPhoneNumber(), copy.getWaPhoneNumber()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getJoinedAt(), copy.getJoinedAt()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getOrdersId(), copy.getOrdersId()) &&
                condition.apply(criteria.getAddedById(), copy.getAddedById()) &&
                condition.apply(criteria.getZoneId(), copy.getZoneId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
