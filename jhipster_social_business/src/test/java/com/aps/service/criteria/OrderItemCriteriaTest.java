package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OrderItemCriteriaTest {

    @Test
    void newOrderItemCriteriaHasAllFiltersNullTest() {
        var orderItemCriteria = new OrderItemCriteria();
        assertThat(orderItemCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void orderItemCriteriaFluentMethodsCreatesFiltersTest() {
        var orderItemCriteria = new OrderItemCriteria();

        setAllFilters(orderItemCriteria);

        assertThat(orderItemCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void orderItemCriteriaCopyCreatesNullFilterTest() {
        var orderItemCriteria = new OrderItemCriteria();
        var copy = orderItemCriteria.copy();

        assertThat(orderItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(orderItemCriteria)
        );
    }

    @Test
    void orderItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var orderItemCriteria = new OrderItemCriteria();
        setAllFilters(orderItemCriteria);

        var copy = orderItemCriteria.copy();

        assertThat(orderItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(orderItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var orderItemCriteria = new OrderItemCriteria();

        assertThat(orderItemCriteria).hasToString("OrderItemCriteria{}");
    }

    private static void setAllFilters(OrderItemCriteria orderItemCriteria) {
        orderItemCriteria.id();
        orderItemCriteria.quantityKg();
        orderItemCriteria.priceAtOrder();
        orderItemCriteria.productId();
        orderItemCriteria.orderId();
        orderItemCriteria.distinct();
    }

    private static Condition<OrderItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantityKg()) &&
                condition.apply(criteria.getPriceAtOrder()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getOrderId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OrderItemCriteria> copyFiltersAre(OrderItemCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantityKg(), copy.getQuantityKg()) &&
                condition.apply(criteria.getPriceAtOrder(), copy.getPriceAtOrder()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getOrderId(), copy.getOrderId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
