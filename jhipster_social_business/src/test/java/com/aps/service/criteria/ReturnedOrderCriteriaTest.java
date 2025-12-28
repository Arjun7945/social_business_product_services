package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReturnedOrderCriteriaTest {

    @Test
    void newReturnedOrderCriteriaHasAllFiltersNullTest() {
        var returnedOrderCriteria = new ReturnedOrderCriteria();
        assertThat(returnedOrderCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void returnedOrderCriteriaFluentMethodsCreatesFiltersTest() {
        var returnedOrderCriteria = new ReturnedOrderCriteria();

        setAllFilters(returnedOrderCriteria);

        assertThat(returnedOrderCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void returnedOrderCriteriaCopyCreatesNullFilterTest() {
        var returnedOrderCriteria = new ReturnedOrderCriteria();
        var copy = returnedOrderCriteria.copy();

        assertThat(returnedOrderCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(returnedOrderCriteria)
        );
    }

    @Test
    void returnedOrderCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var returnedOrderCriteria = new ReturnedOrderCriteria();
        setAllFilters(returnedOrderCriteria);

        var copy = returnedOrderCriteria.copy();

        assertThat(returnedOrderCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(returnedOrderCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var returnedOrderCriteria = new ReturnedOrderCriteria();

        assertThat(returnedOrderCriteria).hasToString("ReturnedOrderCriteria{}");
    }

    private static void setAllFilters(ReturnedOrderCriteria returnedOrderCriteria) {
        returnedOrderCriteria.id();
        returnedOrderCriteria.returnDate();
        returnedOrderCriteria.paymentReceivedMode();
        returnedOrderCriteria.paymentReturnedMode();
        returnedOrderCriteria.productClaimStatus();
        returnedOrderCriteria.refundAmount();
        returnedOrderCriteria.historyId();
        returnedOrderCriteria.itemsId();
        returnedOrderCriteria.orderId();
        returnedOrderCriteria.customerId();
        returnedOrderCriteria.distinct();
    }

    private static Condition<ReturnedOrderCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReturnDate()) &&
                condition.apply(criteria.getPaymentReceivedMode()) &&
                condition.apply(criteria.getPaymentReturnedMode()) &&
                condition.apply(criteria.getProductClaimStatus()) &&
                condition.apply(criteria.getRefundAmount()) &&
                condition.apply(criteria.getHistoryId()) &&
                condition.apply(criteria.getItemsId()) &&
                condition.apply(criteria.getOrderId()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReturnedOrderCriteria> copyFiltersAre(
        ReturnedOrderCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReturnDate(), copy.getReturnDate()) &&
                condition.apply(criteria.getPaymentReceivedMode(), copy.getPaymentReceivedMode()) &&
                condition.apply(criteria.getPaymentReturnedMode(), copy.getPaymentReturnedMode()) &&
                condition.apply(criteria.getProductClaimStatus(), copy.getProductClaimStatus()) &&
                condition.apply(criteria.getRefundAmount(), copy.getRefundAmount()) &&
                condition.apply(criteria.getHistoryId(), copy.getHistoryId()) &&
                condition.apply(criteria.getItemsId(), copy.getItemsId()) &&
                condition.apply(criteria.getOrderId(), copy.getOrderId()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
