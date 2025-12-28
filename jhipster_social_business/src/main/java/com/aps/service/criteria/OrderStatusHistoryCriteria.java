package com.aps.service.criteria;

import com.aps.domain.enumeration.OrderStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.OrderStatusHistory} entity. This class is used
 * in {@link com.aps.web.rest.OrderStatusHistoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /order-status-histories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderStatusHistoryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OrderStatus
     */
    public static class OrderStatusFilter extends Filter<OrderStatus> {

        public OrderStatusFilter() {}

        public OrderStatusFilter(OrderStatusFilter filter) {
            super(filter);
        }

        @Override
        public OrderStatusFilter copy() {
            return new OrderStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private OrderStatusFilter status;

    private InstantFilter changeTime;

    private LongFilter customerOrderId;

    private Boolean distinct;

    public OrderStatusHistoryCriteria() {}

    public OrderStatusHistoryCriteria(OrderStatusHistoryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(OrderStatusFilter::copy).orElse(null);
        this.changeTime = other.optionalChangeTime().map(InstantFilter::copy).orElse(null);
        this.customerOrderId = other.optionalCustomerOrderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OrderStatusHistoryCriteria copy() {
        return new OrderStatusHistoryCriteria(this);
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

    public OrderStatusFilter getStatus() {
        return status;
    }

    public Optional<OrderStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public OrderStatusFilter status() {
        if (status == null) {
            setStatus(new OrderStatusFilter());
        }
        return status;
    }

    public void setStatus(OrderStatusFilter status) {
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

    public LongFilter getCustomerOrderId() {
        return customerOrderId;
    }

    public Optional<LongFilter> optionalCustomerOrderId() {
        return Optional.ofNullable(customerOrderId);
    }

    public LongFilter customerOrderId() {
        if (customerOrderId == null) {
            setCustomerOrderId(new LongFilter());
        }
        return customerOrderId;
    }

    public void setCustomerOrderId(LongFilter customerOrderId) {
        this.customerOrderId = customerOrderId;
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
        final OrderStatusHistoryCriteria that = (OrderStatusHistoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(changeTime, that.changeTime) &&
            Objects.equals(customerOrderId, that.customerOrderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, changeTime, customerOrderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderStatusHistoryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalChangeTime().map(f -> "changeTime=" + f + ", ").orElse("") +
            optionalCustomerOrderId().map(f -> "customerOrderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
