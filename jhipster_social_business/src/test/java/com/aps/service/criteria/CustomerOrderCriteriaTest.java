package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CustomerOrderCriteriaTest {

    @Test
    void newCustomerOrderCriteriaHasAllFiltersNullTest() {
        var customerOrderCriteria = new CustomerOrderCriteria();
        assertThat(customerOrderCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void customerOrderCriteriaFluentMethodsCreatesFiltersTest() {
        var customerOrderCriteria = new CustomerOrderCriteria();

        setAllFilters(customerOrderCriteria);

        assertThat(customerOrderCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void customerOrderCriteriaCopyCreatesNullFilterTest() {
        var customerOrderCriteria = new CustomerOrderCriteria();
        var copy = customerOrderCriteria.copy();

        assertThat(customerOrderCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(customerOrderCriteria)
        );
    }

    @Test
    void customerOrderCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var customerOrderCriteria = new CustomerOrderCriteria();
        setAllFilters(customerOrderCriteria);

        var copy = customerOrderCriteria.copy();

        assertThat(customerOrderCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(customerOrderCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var customerOrderCriteria = new CustomerOrderCriteria();

        assertThat(customerOrderCriteria).hasToString("CustomerOrderCriteria{}");
    }

    private static void setAllFilters(CustomerOrderCriteria customerOrderCriteria) {
        customerOrderCriteria.id();
        customerOrderCriteria.orderTime();
        customerOrderCriteria.totalAmount();
        customerOrderCriteria.status();
        customerOrderCriteria.paymentMethod();
        customerOrderCriteria.confirmedAt();
        customerOrderCriteria.itemsId();
        customerOrderCriteria.deliveryPersonId();
        customerOrderCriteria.customerId();
        customerOrderCriteria.distinct();
    }

    private static Condition<CustomerOrderCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOrderTime()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getPaymentMethod()) &&
                condition.apply(criteria.getConfirmedAt()) &&
                condition.apply(criteria.getItemsId()) &&
                condition.apply(criteria.getDeliveryPersonId()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CustomerOrderCriteria> copyFiltersAre(
        CustomerOrderCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getOrderTime(), copy.getOrderTime()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getPaymentMethod(), copy.getPaymentMethod()) &&
                condition.apply(criteria.getConfirmedAt(), copy.getConfirmedAt()) &&
                condition.apply(criteria.getItemsId(), copy.getItemsId()) &&
                condition.apply(criteria.getDeliveryPersonId(), copy.getDeliveryPersonId()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
