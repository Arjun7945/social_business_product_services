package com.aps.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.BotSession} entity. This class is used
 * in {@link com.aps.web.rest.BotSessionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /bot-sessions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BotSessionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter waPhoneNumber;

    private StringFilter currentState;

    private InstantFilter lastActiveAt;

    private Boolean distinct;

    public BotSessionCriteria() {}

    public BotSessionCriteria(BotSessionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.waPhoneNumber = other.optionalWaPhoneNumber().map(StringFilter::copy).orElse(null);
        this.currentState = other.optionalCurrentState().map(StringFilter::copy).orElse(null);
        this.lastActiveAt = other.optionalLastActiveAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BotSessionCriteria copy() {
        return new BotSessionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getWaPhoneNumber() {
        return waPhoneNumber;
    }

    public Optional<StringFilter> optionalWaPhoneNumber() {
        return Optional.ofNullable(waPhoneNumber);
    }

    public StringFilter waPhoneNumber() {
        if (waPhoneNumber == null) {
            setWaPhoneNumber(new StringFilter());
        }
        return waPhoneNumber;
    }

    public void setWaPhoneNumber(StringFilter waPhoneNumber) {
        this.waPhoneNumber = waPhoneNumber;
    }

    public StringFilter getCurrentState() {
        return currentState;
    }

    public Optional<StringFilter> optionalCurrentState() {
        return Optional.ofNullable(currentState);
    }

    public StringFilter currentState() {
        if (currentState == null) {
            setCurrentState(new StringFilter());
        }
        return currentState;
    }

    public void setCurrentState(StringFilter currentState) {
        this.currentState = currentState;
    }

    public InstantFilter getLastActiveAt() {
        return lastActiveAt;
    }

    public Optional<InstantFilter> optionalLastActiveAt() {
        return Optional.ofNullable(lastActiveAt);
    }

    public InstantFilter lastActiveAt() {
        if (lastActiveAt == null) {
            setLastActiveAt(new InstantFilter());
        }
        return lastActiveAt;
    }

    public void setLastActiveAt(InstantFilter lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BotSessionCriteria that = (BotSessionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(waPhoneNumber, that.waPhoneNumber) &&
            Objects.equals(currentState, that.currentState) &&
            Objects.equals(lastActiveAt, that.lastActiveAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, waPhoneNumber, currentState, lastActiveAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BotSessionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalWaPhoneNumber().map(f -> "waPhoneNumber=" + f + ", ").orElse("") +
            optionalCurrentState().map(f -> "currentState=" + f + ", ").orElse("") +
            optionalLastActiveAt().map(f -> "lastActiveAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
