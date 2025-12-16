package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ShoppingCartCriteriaTest {

    @Test
    void newShoppingCartCriteriaHasAllFiltersNullTest() {
        var shoppingCartCriteria = new ShoppingCartCriteria();
        assertThat(shoppingCartCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void shoppingCartCriteriaFluentMethodsCreatesFiltersTest() {
        var shoppingCartCriteria = new ShoppingCartCriteria();

        setAllFilters(shoppingCartCriteria);

        assertThat(shoppingCartCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void shoppingCartCriteriaCopyCreatesNullFilterTest() {
        var shoppingCartCriteria = new ShoppingCartCriteria();
        var copy = shoppingCartCriteria.copy();

        assertThat(shoppingCartCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(shoppingCartCriteria)
        );
    }

    @Test
    void shoppingCartCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var shoppingCartCriteria = new ShoppingCartCriteria();
        setAllFilters(shoppingCartCriteria);

        var copy = shoppingCartCriteria.copy();

        assertThat(shoppingCartCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(shoppingCartCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var shoppingCartCriteria = new ShoppingCartCriteria();

        assertThat(shoppingCartCriteria).hasToString("ShoppingCartCriteria{}");
    }

    private static void setAllFilters(ShoppingCartCriteria shoppingCartCriteria) {
        shoppingCartCriteria.id();
        shoppingCartCriteria.createdAt();
        shoppingCartCriteria.updatedAt();
        shoppingCartCriteria.itemsId();
        shoppingCartCriteria.customerId();
        shoppingCartCriteria.distinct();
    }

    private static Condition<ShoppingCartCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getItemsId()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ShoppingCartCriteria> copyFiltersAre(
        ShoppingCartCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getItemsId(), copy.getItemsId()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
