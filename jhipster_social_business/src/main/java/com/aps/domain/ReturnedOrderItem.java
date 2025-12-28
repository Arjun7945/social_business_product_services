package com.aps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * NEW ENTITY: ReturnedOrderItem
 * Handles multiple products in a single return request.
 */
@Entity
@Table(name = "returned_order_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnedOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "product_comment")
    private String productComment;

    /**
     * Returned Order Item links to Specific Product
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "image" }, allowSetters = true)
    private FishProduct product;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "histories", "items", "order", "customer" }, allowSetters = true)
    private ReturnedOrder returnedOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReturnedOrderItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantity() {
        return this.quantity;
    }

    public ReturnedOrderItem quantity(Double quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getProductComment() {
        return this.productComment;
    }

    public ReturnedOrderItem productComment(String productComment) {
        this.setProductComment(productComment);
        return this;
    }

    public void setProductComment(String productComment) {
        this.productComment = productComment;
    }

    public FishProduct getProduct() {
        return this.product;
    }

    public void setProduct(FishProduct fishProduct) {
        this.product = fishProduct;
    }

    public ReturnedOrderItem product(FishProduct fishProduct) {
        this.setProduct(fishProduct);
        return this;
    }

    public ReturnedOrder getReturnedOrder() {
        return this.returnedOrder;
    }

    public void setReturnedOrder(ReturnedOrder returnedOrder) {
        this.returnedOrder = returnedOrder;
    }

    public ReturnedOrderItem returnedOrder(ReturnedOrder returnedOrder) {
        this.setReturnedOrder(returnedOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnedOrderItem)) {
            return false;
        }
        return getId() != null && getId().equals(((ReturnedOrderItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnedOrderItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", productComment='" + getProductComment() + "'" +
            "}";
    }
}
