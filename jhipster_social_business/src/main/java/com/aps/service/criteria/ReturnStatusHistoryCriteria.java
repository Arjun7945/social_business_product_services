package com.aps.service.criteria;

import com.aps.domain.enumeration.ReturnStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.ReturnStatusHistory} entity. This class is used
 * in {@link com.aps.web.rest.ReturnStatusHistoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /return-status-histories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnStatusHistoryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ReturnStatus
     */
    public static class ReturnStatusFilter extends Filter<ReturnStatus> {

        public ReturnStatusFilter() {}

        public ReturnStatusFilter(ReturnStatusFilter filter) {
            super(filter);
        }

        @Override
        public ReturnStatusFilter copy() {
            return new ReturnStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ReturnStatusFilter status;

    private InstantFilter changeTime;

    private LongFilter returnedOrderId;

    private Boolean distinct;

    public ReturnStatusHistoryCriteria() {}

    public ReturnStatusHistoryCriteria(ReturnStatusHistoryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ReturnStatusFilter::copy).orElse(null);
        this.changeTime = other.optionalChangeTime().map(InstantFilter::copy).orElse(null);
        this.returnedOrderId = other.optionalReturnedOrderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReturnStatusHistoryCriteria copy() {
        return new ReturnStatusHistoryCriteria(this);
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

    public ReturnStatusFilter getStatus() {
        return status;
    }

    public Optional<ReturnStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ReturnStatusFilter status() {
        if (status == null) {
            setStatus(new ReturnStatusFilter());
        }
        return status;
    }

    public void setStatus(ReturnStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getChangeTime() {
        return changeTime;
    }

    public Optional<InstantFilter> optionalChangeTime() {
        return Optional.ofNullable(changeTime);
    }

    public InstantFilter changeTime() {
        if (changeTime == null) {
            setChangeTime(new InstantFilter());
        }
        return changeTime;
    }

    public void setChangeTime(InstantFilter changeTime) {
        this.changeTime = changeTime;
    }

    public LongFilter getReturnedOrderId() {
        return returnedOrderId;
    }

    public Optional<LongFilter> optionalReturnedOrderId() {
        return Optional.ofNullable(returnedOrderId);
    }

    public LongFilter returnedOrderId() {
        if (returnedOrderId == null) {
            setReturnedOrderId(new LongFilter());
        }
        return returnedOrderId;
    }

    public void setReturnedOrderId(LongFilter returnedOrderId) {
        this.returnedOrderId = returnedOrderId;
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
        final ReturnStatusHistoryCriteria that = (ReturnStatusHistoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(changeTime, that.changeTime) &&
            Objects.equals(returnedOrderId, that.returnedOrderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, changeTime, returnedOrderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnStatusHistoryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalChangeTime().map(f -> "changeTime=" + f + ", ").orElse("") +
            optionalReturnedOrderId().map(f -> "returnedOrderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
