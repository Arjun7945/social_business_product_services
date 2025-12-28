package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class FishProductCriteriaTest {

    @Test
    void newFishProductCriteriaHasAllFiltersNullTest() {
        var fishProductCriteria = new FishProductCriteria();
        assertThat(fishProductCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void fishProductCriteriaFluentMethodsCreatesFiltersTest() {
        var fishProductCriteria = new FishProductCriteria();

        setAllFilters(fishProductCriteria);

        assertThat(fishProductCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void fishProductCriteriaCopyCreatesNullFilterTest() {
        var fishProductCriteria = new FishProductCriteria();
        var copy = fishProductCriteria.copy();

        assertThat(fishProductCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(fishProductCriteria)
        );
    }

    @Test
    void fishProductCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var fishProductCriteria = new FishProductCriteria();
        setAllFilters(fishProductCriteria);

        var copy = fishProductCriteria.copy();

        assertThat(fishProductCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(fishProductCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var fishProductCriteria = new FishProductCriteria();

        assertThat(fishProductCriteria).hasToString("FishProductCriteria{}");
    }

    private static void setAllFilters(FishProductCriteria fishProductCriteria) {
        fishProductCriteria.id();
        fishProductCriteria.name();
        fishProductCriteria.pricePerKg();
        fishProductCriteria.availableQuantity();
        fishProductCriteria.description();
        fishProductCriteria.isAvailable();
        fishProductCriteria.createdAt();
        fishProductCriteria.imageId();
        fishProductCriteria.distinct();
    }

    private static Condition<FishProductCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getPricePerKg()) &&
                condition.apply(criteria.getAvailableQuantity()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getIsAvailable()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getImageId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<FishProductCriteria> copyFiltersAre(FishProductCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getPricePerKg(), copy.getPricePerKg()) &&
                condition.apply(criteria.getAvailableQuantity(), copy.getAvailableQuantity()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getIsAvailable(), copy.getIsAvailable()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getImageId(), copy.getImageId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
