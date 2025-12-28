package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProductImageCriteriaTest {

    @Test
    void newProductImageCriteriaHasAllFiltersNullTest() {
        var productImageCriteria = new ProductImageCriteria();
        assertThat(productImageCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void productImageCriteriaFluentMethodsCreatesFiltersTest() {
        var productImageCriteria = new ProductImageCriteria();

        setAllFilters(productImageCriteria);

        assertThat(productImageCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void productImageCriteriaCopyCreatesNullFilterTest() {
        var productImageCriteria = new ProductImageCriteria();
        var copy = productImageCriteria.copy();

        assertThat(productImageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(productImageCriteria)
        );
    }

    @Test
    void productImageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var productImageCriteria = new ProductImageCriteria();
        setAllFilters(productImageCriteria);

        var copy = productImageCriteria.copy();

        assertThat(productImageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(productImageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var productImageCriteria = new ProductImageCriteria();

        assertThat(productImageCriteria).hasToString("ProductImageCriteria{}");
    }

    private static void setAllFilters(ProductImageCriteria productImageCriteria) {
        productImageCriteria.id();
        productImageCriteria.imageUrl();
        productImageCriteria.displayOrder();
        productImageCriteria.productId();
        productImageCriteria.distinct();
    }

    private static Condition<ProductImageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getImageUrl()) &&
                condition.apply(criteria.getDisplayOrder()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProductImageCriteria> copyFiltersAre(
        ProductImageCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getImageUrl(), copy.getImageUrl()) &&
                condition.apply(criteria.getDisplayOrder(), copy.getDisplayOrder()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
