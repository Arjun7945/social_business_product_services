package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CustomerCriteriaTest {

    @Test
    void newCustomerCriteriaHasAllFiltersNullTest() {
        var customerCriteria = new CustomerCriteria();
        assertThat(customerCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void customerCriteriaFluentMethodsCreatesFiltersTest() {
        var customerCriteria = new CustomerCriteria();

        setAllFilters(customerCriteria);

        assertThat(customerCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void customerCriteriaCopyCreatesNullFilterTest() {
        var customerCriteria = new CustomerCriteria();
        var copy = customerCriteria.copy();

        assertThat(customerCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(customerCriteria)
        );
    }

    @Test
    void customerCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var customerCriteria = new CustomerCriteria();
        setAllFilters(customerCriteria);

        var copy = customerCriteria.copy();

        assertThat(customerCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(customerCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var customerCriteria = new CustomerCriteria();

        assertThat(customerCriteria).hasToString("CustomerCriteria{}");
    }

    private static void setAllFilters(CustomerCriteria customerCriteria) {
        customerCriteria.id();
        customerCriteria.waPhoneNumber();
        customerCriteria.name();
        customerCriteria.phoneNumber();
        customerCriteria.locationLat();
        customerCriteria.locationLon();
        customerCriteria.address();
        customerCriteria.distanceFromBusinessKm();
        customerCriteria.isPincodeValid();
        customerCriteria.role();
        customerCriteria.joinedAt();
        customerCriteria.lastInteractionAt();
        customerCriteria.ordersId();
        customerCriteria.cartId();
        customerCriteria.addedById();
        customerCriteria.distinct();
    }

    private static Condition<CustomerCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getWaPhoneNumber()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getLocationLat()) &&
                condition.apply(criteria.getLocationLon()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getDistanceFromBusinessKm()) &&
                condition.apply(criteria.getIsPincodeValid()) &&
                condition.apply(criteria.getRole()) &&
                condition.apply(criteria.getJoinedAt()) &&
                condition.apply(criteria.getLastInteractionAt()) &&
                condition.apply(criteria.getOrdersId()) &&
                condition.apply(criteria.getCartId()) &&
                condition.apply(criteria.getAddedById()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CustomerCriteria> copyFiltersAre(CustomerCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getWaPhoneNumber(), copy.getWaPhoneNumber()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getLocationLat(), copy.getLocationLat()) &&
                condition.apply(criteria.getLocationLon(), copy.getLocationLon()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getDistanceFromBusinessKm(), copy.getDistanceFromBusinessKm()) &&
                condition.apply(criteria.getIsPincodeValid(), copy.getIsPincodeValid()) &&
                condition.apply(criteria.getRole(), copy.getRole()) &&
                condition.apply(criteria.getJoinedAt(), copy.getJoinedAt()) &&
                condition.apply(criteria.getLastInteractionAt(), copy.getLastInteractionAt()) &&
                condition.apply(criteria.getOrdersId(), copy.getOrdersId()) &&
                condition.apply(criteria.getCartId(), copy.getCartId()) &&
                condition.apply(criteria.getAddedById(), copy.getAddedById()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
