package com.aps.service.criteria;

import com.aps.domain.enumeration.ReturnProductStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.ReturnedOrder} entity. This class is used
 * in {@link com.aps.web.rest.ReturnedOrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /returned-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnedOrderCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ReturnProductStatus
     */
    public static class ReturnProductStatusFilter extends Filter<ReturnProductStatus> {

        public ReturnProductStatusFilter() {}

        public ReturnProductStatusFilter(ReturnProductStatusFilter filter) {
            super(filter);
        }

        @Override
        public ReturnProductStatusFilter copy() {
            return new ReturnProductStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter returnDate;

    private StringFilter paymentReceivedMode;

    private StringFilter paymentReturnedMode;

    private ReturnProductStatusFilter productClaimStatus;

    private BigDecimalFilter refundAmount;

    private LongFilter historyId;

    private LongFilter itemsId;

    private LongFilter orderId;

    private LongFilter customerId;

    private Boolean distinct;

    public ReturnedOrderCriteria() {}

    public ReturnedOrderCriteria(ReturnedOrderCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.returnDate = other.optionalReturnDate().map(InstantFilter::copy).orElse(null);
        this.paymentReceivedMode = other.optionalPaymentReceivedMode().map(StringFilter::copy).orElse(null);
        this.paymentReturnedMode = other.optionalPaymentReturnedMode().map(StringFilter::copy).orElse(null);
        this.productClaimStatus = other.optionalProductClaimStatus().map(ReturnProductStatusFilter::copy).orElse(null);
        this.refundAmount = other.optionalRefundAmount().map(BigDecimalFilter::copy).orElse(null);
        this.historyId = other.optionalHistoryId().map(LongFilter::copy).orElse(null);
        this.itemsId = other.optionalItemsId().map(LongFilter::copy).orElse(null);
        this.orderId = other.optionalOrderId().map(LongFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReturnedOrderCriteria copy() {
        return new ReturnedOrderCriteria(this);
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

    public InstantFilter getReturnDate() {
        return returnDate;
    }

    public Optional<InstantFilter> optionalReturnDate() {
        return Optional.ofNullable(returnDate);
    }

    public InstantFilter returnDate() {
        if (returnDate == null) {
            setReturnDate(new InstantFilter());
        }
        return returnDate;
    }

    public void setReturnDate(InstantFilter returnDate) {
        this.returnDate = returnDate;
    }

    public StringFilter getPaymentReceivedMode() {
        return paymentReceivedMode;
    }

    public Optional<StringFilter> optionalPaymentReceivedMode() {
        return Optional.ofNullable(paymentReceivedMode);
    }

    public StringFilter paymentReceivedMode() {
        if (paymentReceivedMode == null) {
            setPaymentReceivedMode(new StringFilter());
        }
        return paymentReceivedMode;
    }

    public void setPaymentReceivedMode(StringFilter paymentReceivedMode) {
        this.paymentReceivedMode = paymentReceivedMode;
    }

    public StringFilter getPaymentReturnedMode() {
        return paymentReturnedMode;
    }

    public Optional<StringFilter> optionalPaymentReturnedMode() {
        return Optional.ofNullable(paymentReturnedMode);
    }

    public StringFilter paymentReturnedMode() {
        if (paymentReturnedMode == null) {
            setPaymentReturnedMode(new StringFilter());
        }
        return paymentReturnedMode;
    }

    public void setPaymentReturnedMode(StringFilter paymentReturnedMode) {
        this.paymentReturnedMode = paymentReturnedMode;
    }

    public ReturnProductStatusFilter getProductClaimStatus() {
        return productClaimStatus;
    }

    public Optional<ReturnProductStatusFilter> optionalProductClaimStatus() {
        return Optional.ofNullable(productClaimStatus);
    }

    public ReturnProductStatusFilter productClaimStatus() {
        if (productClaimStatus == null) {
            setProductClaimStatus(new ReturnProductStatusFilter());
        }
        return productClaimStatus;
    }

    public void setProductClaimStatus(ReturnProductStatusFilter productClaimStatus) {
        this.productClaimStatus = productClaimStatus;
    }

    public BigDecimalFilter getRefundAmount() {
        return refundAmount;
    }

    public Optional<BigDecimalFilter> optionalRefundAmount() {
        return Optional.ofNullable(refundAmount);
    }

    public BigDecimalFilter refundAmount() {
        if (refundAmount == null) {
            setRefundAmount(new BigDecimalFilter());
        }
        return refundAmount;
    }

    public void setRefundAmount(BigDecimalFilter refundAmount) {
        this.refundAmount = refundAmount;
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

    public LongFilter getOrderId() {
        return orderId;
    }

    public Optional<LongFilter> optionalOrderId() {
        return Optional.ofNullable(orderId);
    }

    public LongFilter orderId() {
        if (orderId == null) {
            setOrderId(new LongFilter());
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
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
        final ReturnedOrderCriteria that = (ReturnedOrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(returnDate, that.returnDate) &&
            Objects.equals(paymentReceivedMode, that.paymentReceivedMode) &&
            Objects.equals(paymentReturnedMode, that.paymentReturnedMode) &&
            Objects.equals(productClaimStatus, that.productClaimStatus) &&
            Objects.equals(refundAmount, that.refundAmount) &&
            Objects.equals(historyId, that.historyId) &&
            Objects.equals(itemsId, that.itemsId) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            returnDate,
            paymentReceivedMode,
            paymentReturnedMode,
            productClaimStatus,
            refundAmount,
            historyId,
            itemsId,
            orderId,
            customerId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnedOrderCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReturnDate().map(f -> "returnDate=" + f + ", ").orElse("") +
            optionalPaymentReceivedMode().map(f -> "paymentReceivedMode=" + f + ", ").orElse("") +
            optionalPaymentReturnedMode().map(f -> "paymentReturnedMode=" + f + ", ").orElse("") +
            optionalProductClaimStatus().map(f -> "productClaimStatus=" + f + ", ").orElse("") +
            optionalRefundAmount().map(f -> "refundAmount=" + f + ", ").orElse("") +
            optionalHistoryId().map(f -> "historyId=" + f + ", ").orElse("") +
            optionalItemsId().map(f -> "itemsId=" + f + ", ").orElse("") +
            optionalOrderId().map(f -> "orderId=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
