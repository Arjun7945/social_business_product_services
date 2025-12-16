package com.aps.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.CartItem} entity. This class is used
 * in {@link com.aps.web.rest.CartItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cart-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CartItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter quantityKg;

    private InstantFilter addedAt;

    private LongFilter productId;

    private LongFilter cartId;

    private Boolean distinct;

    public CartItemCriteria() {}

    public CartItemCriteria(CartItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantityKg = other.optionalQuantityKg().map(DoubleFilter::copy).orElse(null);
        this.addedAt = other.optionalAddedAt().map(InstantFilter::copy).orElse(null);
        this.productId = other.optionalProductId().map(LongFilter::copy).orElse(null);
        this.cartId = other.optionalCartId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CartItemCriteria copy() {
        return new CartItemCriteria(this);
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

    public InstantFilter getAddedAt() {
        return addedAt;
    }

    public Optional<InstantFilter> optionalAddedAt() {
        return Optional.ofNullable(addedAt);
    }

    public InstantFilter addedAt() {
        if (addedAt == null) {
            setAddedAt(new InstantFilter());
        }
        return addedAt;
    }

    public void setAddedAt(InstantFilter addedAt) {
        this.addedAt = addedAt;
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

    public LongFilter getCartId() {
        return cartId;
    }

    public Optional<LongFilter> optionalCartId() {
        return Optional.ofNullable(cartId);
    }

    public LongFilter cartId() {
        if (cartId == null) {
            setCartId(new LongFilter());
        }
        return cartId;
    }

    public void setCartId(LongFilter cartId) {
        this.cartId = cartId;
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
        final CartItemCriteria that = (CartItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantityKg, that.quantityKg) &&
            Objects.equals(addedAt, that.addedAt) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(cartId, that.cartId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantityKg, addedAt, productId, cartId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantityKg().map(f -> "quantityKg=" + f + ", ").orElse("") +
            optionalAddedAt().map(f -> "addedAt=" + f + ", ").orElse("") +
            optionalProductId().map(f -> "productId=" + f + ", ").orElse("") +
            optionalCartId().map(f -> "cartId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
