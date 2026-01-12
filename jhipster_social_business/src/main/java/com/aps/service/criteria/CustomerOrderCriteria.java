package com.aps.service.criteria;

import com.aps.domain.enumeration.OrderStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.CustomerOrder} entity. This
 * class is used
 * in {@link com.aps.web.rest.CustomerOrderResource} to receive all the possible
 * filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customer-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerOrderCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OrderStatus
     */
    public static class OrderStatusFilter extends Filter<OrderStatus> {

        public OrderStatusFilter() {
        }

        public OrderStatusFilter(OrderStatusFilter filter) {
            super(filter);
        }

        @Override
        public OrderStatusFilter copy() {
            return new OrderStatusFilter(this);
        }
    }

    public static class PaymentModeFilter extends Filter<com.aps.domain.enumeration.PaymentMode> {
        public PaymentModeFilter() {
        }

        public PaymentModeFilter(PaymentModeFilter filter) {
            super(filter);
        }

        @Override
        public PaymentModeFilter copy() {
            return new PaymentModeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter orderTime;

    private BigDecimalFilter totalAmount;

    private OrderStatusFilter status;

    private PaymentModeFilter paymentMethod;

    private InstantFilter confirmedAt;

    private LongFilter removedCustomerId;

    private LongFilter removedDeliveryPersonId;

    private StringFilter transactionId;

    private LongFilter historyId;

    private LongFilter itemsId;

    private LongFilter customerId;

    private LongFilter deliveryPersonId;

    private Boolean distinct;

    public CustomerOrderCriteria() {
    }

    public CustomerOrderCriteria(CustomerOrderCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.orderTime = other.optionalOrderTime().map(InstantFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(OrderStatusFilter::copy).orElse(null);
        this.paymentMethod = other.optionalPaymentMethod().map(PaymentModeFilter::copy).orElse(null);
        this.confirmedAt = other.optionalConfirmedAt().map(InstantFilter::copy).orElse(null);
        this.removedCustomerId = other.optionalRemovedCustomerId().map(LongFilter::copy).orElse(null);
        this.removedDeliveryPersonId = other.optionalRemovedDeliveryPersonId().map(LongFilter::copy).orElse(null);
        this.transactionId = other.optionalTransactionId().map(StringFilter::copy).orElse(null);
        this.historyId = other.optionalHistoryId().map(LongFilter::copy).orElse(null);
        this.itemsId = other.optionalItemsId().map(LongFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.deliveryPersonId = other.optionalDeliveryPersonId().map(LongFilter::copy).orElse(null);
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

    public PaymentModeFilter getPaymentMethod() {
        return paymentMethod;
    }

    public Optional<PaymentModeFilter> optionalPaymentMethod() {
        return Optional.ofNullable(paymentMethod);
    }

    public PaymentModeFilter paymentMethod() {
        if (paymentMethod == null) {
            setPaymentMethod(new PaymentModeFilter());
        }
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentModeFilter paymentMethod) {
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

    public LongFilter getRemovedCustomerId() {
        return removedCustomerId;
    }

    public Optional<LongFilter> optionalRemovedCustomerId() {
        return Optional.ofNullable(removedCustomerId);
    }

    public LongFilter removedCustomerId() {
        if (removedCustomerId == null) {
            setRemovedCustomerId(new LongFilter());
        }
        return removedCustomerId;
    }

    public void setRemovedCustomerId(LongFilter removedCustomerId) {
        this.removedCustomerId = removedCustomerId;
    }

    public LongFilter getRemovedDeliveryPersonId() {
        return removedDeliveryPersonId;
    }

    public Optional<LongFilter> optionalRemovedDeliveryPersonId() {
        return Optional.ofNullable(removedDeliveryPersonId);
    }

    public LongFilter removedDeliveryPersonId() {
        if (removedDeliveryPersonId == null) {
            setRemovedDeliveryPersonId(new LongFilter());
        }
        return removedDeliveryPersonId;
    }

    public void setRemovedDeliveryPersonId(LongFilter removedDeliveryPersonId) {
        this.removedDeliveryPersonId = removedDeliveryPersonId;
    }

    public StringFilter getTransactionId() {
        return transactionId;
    }

    public Optional<StringFilter> optionalTransactionId() {
        return Optional.ofNullable(transactionId);
    }

    public StringFilter transactionId() {
        if (transactionId == null) {
            setTransactionId(new StringFilter());
        }
        return transactionId;
    }

    public void setTransactionId(StringFilter transactionId) {
        this.transactionId = transactionId;
    }

    public LongFilter getHistoryId() {
        return historyId;
    }

    public Optional<LongFilter> optionalHistoryId() {
        return Optional.ofNullable(historyId);
    }

    public LongFilter historyId() {
        if (historyId == null) {
            setHistoryId(new LongFilter());
        }
        return historyId;
    }

    public void setHistoryId(LongFilter historyId) {
        this.historyId = historyId;
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
        return (Objects.equals(id, that.id) &&
                Objects.equals(orderTime, that.orderTime) &&
                Objects.equals(totalAmount, that.totalAmount) &&
                Objects.equals(status, that.status) &&
                Objects.equals(paymentMethod, that.paymentMethod) &&
                Objects.equals(confirmedAt, that.confirmedAt) &&
                Objects.equals(removedCustomerId, that.removedCustomerId) &&
                Objects.equals(removedDeliveryPersonId, that.removedDeliveryPersonId) &&
                Objects.equals(transactionId, that.transactionId) &&
                Objects.equals(historyId, that.historyId) &&
                Objects.equals(itemsId, that.itemsId) &&
                Objects.equals(customerId, that.customerId) &&
                Objects.equals(deliveryPersonId, that.deliveryPersonId) &&
                Objects.equals(distinct, that.distinct));
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
                removedCustomerId,
                removedDeliveryPersonId,
                transactionId,
                historyId,
                itemsId,
                customerId,
                deliveryPersonId,
                distinct);
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
                optionalRemovedCustomerId().map(f -> "removedCustomerId=" + f + ", ").orElse("") +
                optionalRemovedDeliveryPersonId().map(f -> "removedDeliveryPersonId=" + f + ", ").orElse("") +
                optionalTransactionId().map(f -> "transactionId=" + f + ", ").orElse("") +
                optionalHistoryId().map(f -> "historyId=" + f + ", ").orElse("") +
                optionalItemsId().map(f -> "itemsId=" + f + ", ").orElse("") +
                optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
                optionalDeliveryPersonId().map(f -> "deliveryPersonId=" + f + ", ").orElse("") +
                optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
                "}";
    }
}
