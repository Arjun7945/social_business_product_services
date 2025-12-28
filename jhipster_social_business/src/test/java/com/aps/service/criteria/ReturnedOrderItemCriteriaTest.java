package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReturnedOrderItemCriteriaTest {

    @Test
    void newReturnedOrderItemCriteriaHasAllFiltersNullTest() {
        var returnedOrderItemCriteria = new ReturnedOrderItemCriteria();
        assertThat(returnedOrderItemCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void returnedOrderItemCriteriaFluentMethodsCreatesFiltersTest() {
        var returnedOrderItemCriteria = new ReturnedOrderItemCriteria();

        setAllFilters(returnedOrderItemCriteria);

        assertThat(returnedOrderItemCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void returnedOrderItemCriteriaCopyCreatesNullFilterTest() {
        var returnedOrderItemCriteria = new ReturnedOrderItemCriteria();
        var copy = returnedOrderItemCriteria.copy();

        assertThat(returnedOrderItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(returnedOrderItemCriteria)
        );
    }

    @Test
    void returnedOrderItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var returnedOrderItemCriteria = new ReturnedOrderItemCriteria();
        setAllFilters(returnedOrderItemCriteria);

        var copy = returnedOrderItemCriteria.copy();

        assertThat(returnedOrderItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(returnedOrderItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var returnedOrderItemCriteria = new ReturnedOrderItemCriteria();

        assertThat(returnedOrderItemCriteria).hasToString("ReturnedOrderItemCriteria{}");
    }

    private static void setAllFilters(ReturnedOrderItemCriteria returnedOrderItemCriteria) {
        returnedOrderItemCriteria.id();
        returnedOrderItemCriteria.quantity();
        returnedOrderItemCriteria.productComment();
        returnedOrderItemCriteria.productId();
        returnedOrderItemCriteria.returnedOrderId();
        returnedOrderItemCriteria.distinct();
    }

    private static Condition<ReturnedOrderItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getProductComment()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getReturnedOrderId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReturnedOrderItemCriteria> copyFiltersAre(
        ReturnedOrderItemCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getProductComment(), copy.getProductComment()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getReturnedOrderId(), copy.getReturnedOrderId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
