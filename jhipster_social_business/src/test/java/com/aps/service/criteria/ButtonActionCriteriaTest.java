package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ButtonActionCriteriaTest {

    @Test
    void newButtonActionCriteriaHasAllFiltersNullTest() {
        var buttonActionCriteria = new ButtonActionCriteria();
        assertThat(buttonActionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void buttonActionCriteriaFluentMethodsCreatesFiltersTest() {
        var buttonActionCriteria = new ButtonActionCriteria();

        setAllFilters(buttonActionCriteria);

        assertThat(buttonActionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void buttonActionCriteriaCopyCreatesNullFilterTest() {
        var buttonActionCriteria = new ButtonActionCriteria();
        var copy = buttonActionCriteria.copy();

        assertThat(buttonActionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(buttonActionCriteria)
        );
    }

    @Test
    void buttonActionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var buttonActionCriteria = new ButtonActionCriteria();
        setAllFilters(buttonActionCriteria);

        var copy = buttonActionCriteria.copy();

        assertThat(buttonActionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(buttonActionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var buttonActionCriteria = new ButtonActionCriteria();

        assertThat(buttonActionCriteria).hasToString("ButtonActionCriteria{}");
    }

    private static void setAllFilters(ButtonActionCriteria buttonActionCriteria) {
        buttonActionCriteria.id();
        buttonActionCriteria.waMessageId();
        buttonActionCriteria.buttonId();
        buttonActionCriteria.clickedAt();
        buttonActionCriteria.clickedBy();
        buttonActionCriteria.distinct();
    }

    private static Condition<ButtonActionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getWaMessageId()) &&
                condition.apply(criteria.getButtonId()) &&
                condition.apply(criteria.getClickedAt()) &&
                condition.apply(criteria.getClickedBy()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ButtonActionCriteria> copyFiltersAre(
        ButtonActionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getWaMessageId(), copy.getWaMessageId()) &&
                condition.apply(criteria.getButtonId(), copy.getButtonId()) &&
                condition.apply(criteria.getClickedAt(), copy.getClickedAt()) &&
                condition.apply(criteria.getClickedBy(), copy.getClickedBy()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
