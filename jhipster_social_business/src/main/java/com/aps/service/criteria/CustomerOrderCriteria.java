package com.aps.service.criteria;

import com.aps.domain.enumeration.OrderStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.CustomerOrder} entity. This class is used
 * in {@link com.aps.web.rest.CustomerOrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customer-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerOrderCriteria implements Serializable, Criteria {

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

    private InstantFilter orderTime;

    private BigDecimalFilter totalAmount;

    private OrderStatusFilter status;

    private StringFilter paymentMethod;

    private InstantFilter confirmedAt;

    private LongFilter itemsId;

    private LongFilter deliveryPersonId;

    private LongFilter customerId;

    private Boolean distinct;

    public CustomerOrderCriteria() {}

    public CustomerOrderCriteria(CustomerOrderCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.orderTime = other.optionalOrderTime().map(InstantFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(OrderStatusFilter::copy).orElse(null);
        this.paymentMethod = other.optionalPaymentMethod().map(StringFilter::copy).orElse(null);
        this.confirmedAt = other.optionalConfirmedAt().map(InstantFilter::copy).orElse(null);
        this.itemsId = other.optionalItemsId().map(LongFilter::copy).orElse(null);
        this.deliveryPersonId = other.optionalDeliveryPersonId().map(LongFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CustomerOrderCriteria copy() {
        return new CustomerOrderCriteria(this);
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

    public InstantFilter getOrderTime() {
        return orderTime;
    }

    public Optional<InstantFilter> optionalOrderTime() {
        return Optional.ofNullable(orderTime);
    }

    public InstantFilter orderTime() {
        if (orderTime == null) {
            setOrderTime(new InstantFilter());
        }
        return orderTime;
    }

    public void setOrderTime(InstantFilter orderTime) {
        this.orderTime = orderTime;
    }

    public BigDecimalFilter getTotalAmount() {
        return totalAmount;
    }

    public Optional<BigDecimalFilter> optionalTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }

    public BigDecimalFilter totalAmount() {
        if (totalAmount == null) {
            setTotalAmount(new BigDecimalFilter());
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimalFilter totalAmount) {
        this.totalAmount = totalAmount;
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

    public StringFilter getPaymentMethod() {
        return paymentMethod;
    }

    public Optional<StringFilter> optionalPaymentMethod() {
        return Optional.ofNullable(paymentMethod);
    }

    public StringFilter paymentMethod() {
        if (paymentMethod == null) {
            setPaymentMethod(new StringFilter());
        }
        return paymentMethod;
    }

    public void setPaymentMethod(StringFilter paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public InstantFilter getConfirmedAt() {
        return confirmedAt;
    }

    public Optional<InstantFilter> optionalConfirmedAt() {
        return Optional.ofNullable(confirmedAt);
    }

    public InstantFilter confirmedAt() {
        if (confirmedAt == null) {
            setConfirmedAt(new InstantFilter());
        }
        return confirmedAt;
    }

    public void setConfirmedAt(InstantFilter confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LongFilter getItemsId() {
        return itemsId;
    }

    public Optional<LongFilter> optionalItemsId() {
        return Optional.ofNullable(itemsId);
    }

    public LongFilter itemsId() {
        if (itemsId == null) {
            setItemsId(new LongFilter());
        }
        return itemsId;
    }

    public void setItemsId(LongFilter itemsId) {
        this.itemsId = itemsId;
    }

    public LongFilter getDeliveryPersonId() {
        return deliveryPersonId;
    }

    public Optional<LongFilter> optionalDeliveryPersonId() {
        return Optional.ofNullable(deliveryPersonId);
    }

    public LongFilter deliveryPersonId() {
        if (deliveryPersonId == null) {
            setDeliveryPersonId(new LongFilter());
        }
        return deliveryPersonId;
    }

    public void setDeliveryPersonId(LongFilter deliveryPersonId) {
        this.deliveryPersonId = deliveryPersonId;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
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
        final CustomerOrderCriteria that = (CustomerOrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orderTime, that.orderTime) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(status, that.status) &&
            Objects.equals(paymentMethod, that.paymentMethod) &&
            Objects.equals(confirmedAt, that.confirmedAt) &&
            Objects.equals(itemsId, that.itemsId) &&
            Objects.equals(deliveryPersonId, that.deliveryPersonId) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            orderTime,
            totalAmount,
            status,
            paymentMethod,
            confirmedAt,
            itemsId,
            deliveryPersonId,
            customerId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerOrderCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOrderTime().map(f -> "orderTime=" + f + ", ").orElse("") +
            optionalTotalAmount().map(f -> "totalAmount=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalPaymentMethod().map(f -> "paymentMethod=" + f + ", ").orElse("") +
            optionalConfirmedAt().map(f -> "confirmedAt=" + f + ", ").orElse("") +
            optionalItemsId().map(f -> "itemsId=" + f + ", ").orElse("") +
            optionalDeliveryPersonId().map(f -> "deliveryPersonId=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
