package com.aps.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TeamMemberCriteriaTest {

    @Test
    void newTeamMemberCriteriaHasAllFiltersNullTest() {
        var teamMemberCriteria = new TeamMemberCriteria();
        assertThat(teamMemberCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void teamMemberCriteriaFluentMethodsCreatesFiltersTest() {
        var teamMemberCriteria = new TeamMemberCriteria();

        setAllFilters(teamMemberCriteria);

        assertThat(teamMemberCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void teamMemberCriteriaCopyCreatesNullFilterTest() {
        var teamMemberCriteria = new TeamMemberCriteria();
        var copy = teamMemberCriteria.copy();

        assertThat(teamMemberCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(teamMemberCriteria)
        );
    }

    @Test
    void teamMemberCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var teamMemberCriteria = new TeamMemberCriteria();
        setAllFilters(teamMemberCriteria);

        var copy = teamMemberCriteria.copy();

        assertThat(teamMemberCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(teamMemberCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var teamMemberCriteria = new TeamMemberCriteria();

        assertThat(teamMemberCriteria).hasToString("TeamMemberCriteria{}");
    }

    private static void setAllFilters(TeamMemberCriteria teamMemberCriteria) {
        teamMemberCriteria.id();
        teamMemberCriteria.name();
        teamMemberCriteria.waPhoneNumber();
        teamMemberCriteria.phoneNumber();
        teamMemberCriteria.role();
        teamMemberCriteria.isActive();
        teamMemberCriteria.distinct();
    }

    private static Condition<TeamMemberCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getWaPhoneNumber()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getRole()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TeamMemberCriteria> copyFiltersAre(TeamMemberCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getWaPhoneNumber(), copy.getWaPhoneNumber()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getRole(), copy.getRole()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
