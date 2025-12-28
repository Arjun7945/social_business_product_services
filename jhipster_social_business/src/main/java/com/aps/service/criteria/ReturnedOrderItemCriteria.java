package com.aps.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.ReturnedOrderItem} entity. This class is used
 * in {@link com.aps.web.rest.ReturnedOrderItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /returned-order-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnedOrderItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter quantity;

    private StringFilter productComment;

    private LongFilter productId;

    private LongFilter returnedOrderId;

    private Boolean distinct;

    public ReturnedOrderItemCriteria() {}

    public ReturnedOrderItemCriteria(ReturnedOrderItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantity = other.optionalQuantity().map(DoubleFilter::copy).orElse(null);
        this.productComment = other.optionalProductComment().map(StringFilter::copy).orElse(null);
        this.productId = other.optionalProductId().map(LongFilter::copy).orElse(null);
        this.returnedOrderId = other.optionalReturnedOrderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReturnedOrderItemCriteria copy() {
        return new ReturnedOrderItemCriteria(this);
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

    public DoubleFilter getQuantity() {
        return quantity;
    }

    public Optional<DoubleFilter> optionalQuantity() {
        return Optional.ofNullable(quantity);
    }

    public DoubleFilter quantity() {
        if (quantity == null) {
            setQuantity(new DoubleFilter());
        }
        return quantity;
    }

    public void setQuantity(DoubleFilter quantity) {
        this.quantity = quantity;
    }

    public StringFilter getProductComment() {
        return productComment;
    }

    public Optional<StringFilter> optionalProductComment() {
        return Optional.ofNullable(productComment);
    }

    public StringFilter productComment() {
        if (productComment == null) {
            setProductComment(new StringFilter());
        }
        return productComment;
    }

    public void setProductComment(StringFilter productComment) {
        this.productComment = productComment;
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
        final ReturnedOrderItemCriteria that = (ReturnedOrderItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(productComment, that.productComment) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(returnedOrderId, that.returnedOrderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantity, productComment, productId, returnedOrderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnedOrderItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantity().map(f -> "quantity=" + f + ", ").orElse("") +
            optionalProductComment().map(f -> "productComment=" + f + ", ").orElse("") +
            optionalProductId().map(f -> "productId=" + f + ", ").orElse("") +
            optionalReturnedOrderId().map(f -> "returnedOrderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
