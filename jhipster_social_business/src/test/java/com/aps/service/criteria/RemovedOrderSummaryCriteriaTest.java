package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RemovedOrderSummaryCriteriaTest {

    @Test
    void newRemovedOrderSummaryCriteriaHasAllFiltersNullTest() {
        var removedOrderSummaryCriteria = new RemovedOrderSummaryCriteria();
        assertThat(removedOrderSummaryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void removedOrderSummaryCriteriaFluentMethodsCreatesFiltersTest() {
        var removedOrderSummaryCriteria = new RemovedOrderSummaryCriteria();

        setAllFilters(removedOrderSummaryCriteria);

        assertThat(removedOrderSummaryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void removedOrderSummaryCriteriaCopyCreatesNullFilterTest() {
        var removedOrderSummaryCriteria = new RemovedOrderSummaryCriteria();
        var copy = removedOrderSummaryCriteria.copy();

        assertThat(removedOrderSummaryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(removedOrderSummaryCriteria)
        );
    }

    @Test
    void removedOrderSummaryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var removedOrderSummaryCriteria = new RemovedOrderSummaryCriteria();
        setAllFilters(removedOrderSummaryCriteria);

        var copy = removedOrderSummaryCriteria.copy();

        assertThat(removedOrderSummaryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(removedOrderSummaryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var removedOrderSummaryCriteria = new RemovedOrderSummaryCriteria();

        assertThat(removedOrderSummaryCriteria).hasToString("RemovedOrderSummaryCriteria{}");
    }

    private static void setAllFilters(RemovedOrderSummaryCriteria removedOrderSummaryCriteria) {
        removedOrderSummaryCriteria.id();
        removedOrderSummaryCriteria.userOriginalId();
        removedOrderSummaryCriteria.userName();
        removedOrderSummaryCriteria.userRole();
        removedOrderSummaryCriteria.totalOrders();
        removedOrderSummaryCriteria.totalAmount();
        removedOrderSummaryCriteria.firstInteractionAt();
        removedOrderSummaryCriteria.lastInteractionAt();
        removedOrderSummaryCriteria.removedAt();
        removedOrderSummaryCriteria.distinct();
    }

    private static Condition<RemovedOrderSummaryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getUserOriginalId()) &&
                condition.apply(criteria.getUserName()) &&
                condition.apply(criteria.getUserRole()) &&
                condition.apply(criteria.getTotalOrders()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getFirstInteractionAt()) &&
                condition.apply(criteria.getLastInteractionAt()) &&
                condition.apply(criteria.getRemovedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RemovedOrderSummaryCriteria> copyFiltersAre(
        RemovedOrderSummaryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getUserOriginalId(), copy.getUserOriginalId()) &&
                condition.apply(criteria.getUserName(), copy.getUserName()) &&
                condition.apply(criteria.getUserRole(), copy.getUserRole()) &&
                condition.apply(criteria.getTotalOrders(), copy.getTotalOrders()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getFirstInteractionAt(), copy.getFirstInteractionAt()) &&
                condition.apply(criteria.getLastInteractionAt(), copy.getLastInteractionAt()) &&
                condition.apply(criteria.getRemovedAt(), copy.getRemovedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
