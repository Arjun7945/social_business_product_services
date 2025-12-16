package com.aps.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.OrderItem} entity. This class is used
 * in {@link com.aps.web.rest.OrderItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /order-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter quantityKg;

    private BigDecimalFilter priceAtOrder;

    private LongFilter productId;

    private LongFilter orderId;

    private Boolean distinct;

    public OrderItemCriteria() {}

    public OrderItemCriteria(OrderItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantityKg = other.optionalQuantityKg().map(DoubleFilter::copy).orElse(null);
        this.priceAtOrder = other.optionalPriceAtOrder().map(BigDecimalFilter::copy).orElse(null);
        this.productId = other.optionalProductId().map(LongFilter::copy).orElse(null);
        this.orderId = other.optionalOrderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OrderItemCriteria copy() {
        return new OrderItemCriteria(this);
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

    public DoubleFilter getQuantityKg() {
        return quantityKg;
    }

    public Optional<DoubleFilter> optionalQuantityKg() {
        return Optional.ofNullable(quantityKg);
    }

    public DoubleFilter quantityKg() {
        if (quantityKg == null) {
            setQuantityKg(new DoubleFilter());
        }
        return quantityKg;
    }

    public void setQuantityKg(DoubleFilter quantityKg) {
        this.quantityKg = quantityKg;
    }

    public BigDecimalFilter getPriceAtOrder() {
        return priceAtOrder;
    }

    public Optional<BigDecimalFilter> optionalPriceAtOrder() {
        return Optional.ofNullable(priceAtOrder);
    }

    public BigDecimalFilter priceAtOrder() {
        if (priceAtOrder == null) {
            setPriceAtOrder(new BigDecimalFilter());
        }
        return priceAtOrder;
    }

    public void setPriceAtOrder(BigDecimalFilter priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public Optional<LongFilter> optionalProductId() {
        return Optional.ofNullable(productId);
    }

    public LongFilter productId() {
        if (productId == null) {
            setProductId(new LongFilter());
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
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
        final OrderItemCriteria that = (OrderItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantityKg, that.quantityKg) &&
            Objects.equals(priceAtOrder, that.priceAtOrder) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantityKg, priceAtOrder, productId, orderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantityKg().map(f -> "quantityKg=" + f + ", ").orElse("") +
            optionalPriceAtOrder().map(f -> "priceAtOrder=" + f + ", ").orElse("") +
            optionalProductId().map(f -> "productId=" + f + ", ").orElse("") +
            optionalOrderId().map(f -> "orderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
