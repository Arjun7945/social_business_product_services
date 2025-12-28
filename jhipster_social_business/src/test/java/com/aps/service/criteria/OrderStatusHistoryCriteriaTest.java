package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OrderStatusHistoryCriteriaTest {

    @Test
    void newOrderStatusHistoryCriteriaHasAllFiltersNullTest() {
        var orderStatusHistoryCriteria = new OrderStatusHistoryCriteria();
        assertThat(orderStatusHistoryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void orderStatusHistoryCriteriaFluentMethodsCreatesFiltersTest() {
        var orderStatusHistoryCriteria = new OrderStatusHistoryCriteria();

        setAllFilters(orderStatusHistoryCriteria);

        assertThat(orderStatusHistoryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void orderStatusHistoryCriteriaCopyCreatesNullFilterTest() {
        var orderStatusHistoryCriteria = new OrderStatusHistoryCriteria();
        var copy = orderStatusHistoryCriteria.copy();

        assertThat(orderStatusHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(orderStatusHistoryCriteria)
        );
    }

    @Test
    void orderStatusHistoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var orderStatusHistoryCriteria = new OrderStatusHistoryCriteria();
        setAllFilters(orderStatusHistoryCriteria);

        var copy = orderStatusHistoryCriteria.copy();

        assertThat(orderStatusHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(orderStatusHistoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var orderStatusHistoryCriteria = new OrderStatusHistoryCriteria();

        assertThat(orderStatusHistoryCriteria).hasToString("OrderStatusHistoryCriteria{}");
    }

    private static void setAllFilters(OrderStatusHistoryCriteria orderStatusHistoryCriteria) {
        orderStatusHistoryCriteria.id();
        orderStatusHistoryCriteria.status();
        orderStatusHistoryCriteria.changeTime();
        orderStatusHistoryCriteria.customerOrderId();
        orderStatusHistoryCriteria.distinct();
    }

    private static Condition<OrderStatusHistoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getChangeTime()) &&
                condition.apply(criteria.getCustomerOrderId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OrderStatusHistoryCriteria> copyFiltersAre(
        OrderStatusHistoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getChangeTime(), copy.getChangeTime()) &&
                condition.apply(criteria.getCustomerOrderId(), copy.getCustomerOrderId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
