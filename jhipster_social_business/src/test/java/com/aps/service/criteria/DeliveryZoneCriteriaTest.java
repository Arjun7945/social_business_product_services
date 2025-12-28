package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DeliveryZoneCriteriaTest {

    @Test
    void newDeliveryZoneCriteriaHasAllFiltersNullTest() {
        var deliveryZoneCriteria = new DeliveryZoneCriteria();
        assertThat(deliveryZoneCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void deliveryZoneCriteriaFluentMethodsCreatesFiltersTest() {
        var deliveryZoneCriteria = new DeliveryZoneCriteria();

        setAllFilters(deliveryZoneCriteria);

        assertThat(deliveryZoneCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void deliveryZoneCriteriaCopyCreatesNullFilterTest() {
        var deliveryZoneCriteria = new DeliveryZoneCriteria();
        var copy = deliveryZoneCriteria.copy();

        assertThat(deliveryZoneCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(deliveryZoneCriteria)
        );
    }

    @Test
    void deliveryZoneCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var deliveryZoneCriteria = new DeliveryZoneCriteria();
        setAllFilters(deliveryZoneCriteria);

        var copy = deliveryZoneCriteria.copy();

        assertThat(deliveryZoneCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(deliveryZoneCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var deliveryZoneCriteria = new DeliveryZoneCriteria();

        assertThat(deliveryZoneCriteria).hasToString("DeliveryZoneCriteria{}");
    }

    private static void setAllFilters(DeliveryZoneCriteria deliveryZoneCriteria) {
        deliveryZoneCriteria.id();
        deliveryZoneCriteria.zoneName();
        deliveryZoneCriteria.pincode();
        deliveryZoneCriteria.customersId();
        deliveryZoneCriteria.deliveryPersonsId();
        deliveryZoneCriteria.distinct();
    }

    private static Condition<DeliveryZoneCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getZoneName()) &&
                condition.apply(criteria.getPincode()) &&
                condition.apply(criteria.getCustomersId()) &&
                condition.apply(criteria.getDeliveryPersonsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DeliveryZoneCriteria> copyFiltersAre(
        DeliveryZoneCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getZoneName(), copy.getZoneName()) &&
                condition.apply(criteria.getPincode(), copy.getPincode()) &&
                condition.apply(criteria.getCustomersId(), copy.getCustomersId()) &&
                condition.apply(criteria.getDeliveryPersonsId(), copy.getDeliveryPersonsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
