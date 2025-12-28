package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReturnStatusHistoryCriteriaTest {

    @Test
    void newReturnStatusHistoryCriteriaHasAllFiltersNullTest() {
        var returnStatusHistoryCriteria = new ReturnStatusHistoryCriteria();
        assertThat(returnStatusHistoryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void returnStatusHistoryCriteriaFluentMethodsCreatesFiltersTest() {
        var returnStatusHistoryCriteria = new ReturnStatusHistoryCriteria();

        setAllFilters(returnStatusHistoryCriteria);

        assertThat(returnStatusHistoryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void returnStatusHistoryCriteriaCopyCreatesNullFilterTest() {
        var returnStatusHistoryCriteria = new ReturnStatusHistoryCriteria();
        var copy = returnStatusHistoryCriteria.copy();

        assertThat(returnStatusHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(returnStatusHistoryCriteria)
        );
    }

    @Test
    void returnStatusHistoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var returnStatusHistoryCriteria = new ReturnStatusHistoryCriteria();
        setAllFilters(returnStatusHistoryCriteria);

        var copy = returnStatusHistoryCriteria.copy();

        assertThat(returnStatusHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(returnStatusHistoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var returnStatusHistoryCriteria = new ReturnStatusHistoryCriteria();

        assertThat(returnStatusHistoryCriteria).hasToString("ReturnStatusHistoryCriteria{}");
    }

    private static void setAllFilters(ReturnStatusHistoryCriteria returnStatusHistoryCriteria) {
        returnStatusHistoryCriteria.id();
        returnStatusHistoryCriteria.status();
        returnStatusHistoryCriteria.changeTime();
        returnStatusHistoryCriteria.returnedOrderId();
        returnStatusHistoryCriteria.distinct();
    }

    private static Condition<ReturnStatusHistoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getChangeTime()) &&
                condition.apply(criteria.getReturnedOrderId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReturnStatusHistoryCriteria> copyFiltersAre(
        ReturnStatusHistoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getChangeTime(), copy.getChangeTime()) &&
                condition.apply(criteria.getReturnedOrderId(), copy.getReturnedOrderId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
