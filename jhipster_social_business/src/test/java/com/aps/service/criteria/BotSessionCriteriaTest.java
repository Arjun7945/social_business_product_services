package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BotSessionCriteriaTest {

    @Test
    void newBotSessionCriteriaHasAllFiltersNullTest() {
        var botSessionCriteria = new BotSessionCriteria();
        assertThat(botSessionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void botSessionCriteriaFluentMethodsCreatesFiltersTest() {
        var botSessionCriteria = new BotSessionCriteria();

        setAllFilters(botSessionCriteria);

        assertThat(botSessionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void botSessionCriteriaCopyCreatesNullFilterTest() {
        var botSessionCriteria = new BotSessionCriteria();
        var copy = botSessionCriteria.copy();

        assertThat(botSessionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(botSessionCriteria)
        );
    }

    @Test
    void botSessionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var botSessionCriteria = new BotSessionCriteria();
        setAllFilters(botSessionCriteria);

        var copy = botSessionCriteria.copy();

        assertThat(botSessionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(botSessionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var botSessionCriteria = new BotSessionCriteria();

        assertThat(botSessionCriteria).hasToString("BotSessionCriteria{}");
    }

    private static void setAllFilters(BotSessionCriteria botSessionCriteria) {
        botSessionCriteria.id();
        botSessionCriteria.waPhoneNumber();
        botSessionCriteria.currentState();
        botSessionCriteria.lastActiveAt();
        botSessionCriteria.distinct();
    }

    private static Condition<BotSessionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getWaPhoneNumber()) &&
                condition.apply(criteria.getCurrentState()) &&
                condition.apply(criteria.getLastActiveAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BotSessionCriteria> copyFiltersAre(BotSessionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getWaPhoneNumber(), copy.getWaPhoneNumber()) &&
                condition.apply(criteria.getCurrentState(), copy.getCurrentState()) &&
                condition.apply(criteria.getLastActiveAt(), copy.getLastActiveAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
