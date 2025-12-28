package com.aps.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.ButtonAction} entity. This class is used
 * in {@link com.aps.web.rest.ButtonActionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /button-actions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ButtonActionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter waMessageId;

    private StringFilter buttonId;

    private InstantFilter clickedAt;

    private StringFilter clickedBy;

    private Boolean distinct;

    public ButtonActionCriteria() {}

    public ButtonActionCriteria(ButtonActionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.waMessageId = other.optionalWaMessageId().map(StringFilter::copy).orElse(null);
        this.buttonId = other.optionalButtonId().map(StringFilter::copy).orElse(null);
        this.clickedAt = other.optionalClickedAt().map(InstantFilter::copy).orElse(null);
        this.clickedBy = other.optionalClickedBy().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ButtonActionCriteria copy() {
        return new ButtonActionCriteria(this);
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

    public StringFilter getWaMessageId() {
        return waMessageId;
    }

    public Optional<StringFilter> optionalWaMessageId() {
        return Optional.ofNullable(waMessageId);
    }

    public StringFilter waMessageId() {
        if (waMessageId == null) {
            setWaMessageId(new StringFilter());
        }
        return waMessageId;
    }

    public void setWaMessageId(StringFilter waMessageId) {
        this.waMessageId = waMessageId;
    }

    public StringFilter getButtonId() {
        return buttonId;
    }

    public Optional<StringFilter> optionalButtonId() {
        return Optional.ofNullable(buttonId);
    }

    public StringFilter buttonId() {
        if (buttonId == null) {
            setButtonId(new StringFilter());
        }
        return buttonId;
    }

    public void setButtonId(StringFilter buttonId) {
        this.buttonId = buttonId;
    }

    public InstantFilter getClickedAt() {
        return clickedAt;
    }

    public Optional<InstantFilter> optionalClickedAt() {
        return Optional.ofNullable(clickedAt);
    }

    public InstantFilter clickedAt() {
        if (clickedAt == null) {
            setClickedAt(new InstantFilter());
        }
        return clickedAt;
    }

    public void setClickedAt(InstantFilter clickedAt) {
        this.clickedAt = clickedAt;
    }

    public StringFilter getClickedBy() {
        return clickedBy;
    }

    public Optional<StringFilter> optionalClickedBy() {
        return Optional.ofNullable(clickedBy);
    }

    public StringFilter clickedBy() {
        if (clickedBy == null) {
            setClickedBy(new StringFilter());
        }
        return clickedBy;
    }

    public void setClickedBy(StringFilter clickedBy) {
        this.clickedBy = clickedBy;
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
        final ButtonActionCriteria that = (ButtonActionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(waMessageId, that.waMessageId) &&
            Objects.equals(buttonId, that.buttonId) &&
            Objects.equals(clickedAt, that.clickedAt) &&
            Objects.equals(clickedBy, that.clickedBy) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, waMessageId, buttonId, clickedAt, clickedBy, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ButtonActionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalWaMessageId().map(f -> "waMessageId=" + f + ", ").orElse("") +
            optionalButtonId().map(f -> "buttonId=" + f + ", ").orElse("") +
            optionalClickedAt().map(f -> "clickedAt=" + f + ", ").orElse("") +
            optionalClickedBy().map(f -> "clickedBy=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
