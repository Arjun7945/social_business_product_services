package com.aps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Individual items within an order.
 */
@Entity
@Table(name = "order_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Quantity remains Double as weight can be fractional
     */
    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "quantity_kg", nullable = false)
    private Double quantityKg;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price_at_order", precision = 21, scale = 2, nullable = false)
    private BigDecimal priceAtOrder;

    /**
     * OrderItem links to a Product (Snapshot needed? usually yes but simple link here)
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "images" }, allowSetters = true)
    private FishProduct product;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "items", "deliveryPerson", "customer" }, allowSetters = true)
    private CustomerOrder order;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantityKg() {
        return this.quantityKg;
    }

    public OrderItem quantityKg(Double quantityKg) {
        this.setQuantityKg(quantityKg);
        return this;
    }

    public void setQuantityKg(Double quantityKg) {
        this.quantityKg = quantityKg;
    }

    public BigDecimal getPriceAtOrder() {
        return this.priceAtOrder;
    }

    public OrderItem priceAtOrder(BigDecimal priceAtOrder) {
        this.setPriceAtOrder(priceAtOrder);
        return this;
    }

    public void setPriceAtOrder(BigDecimal priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    public FishProduct getProduct() {
        return this.product;
    }

    public void setProduct(FishProduct fishProduct) {
        this.product = fishProduct;
    }

    public OrderItem product(FishProduct fishProduct) {
        this.setProduct(fishProduct);
        return this;
    }

    public CustomerOrder getOrder() {
        return this.order;
    }

    public void setOrder(CustomerOrder customerOrder) {
        this.order = customerOrder;
    }

    public OrderItem order(CustomerOrder customerOrder) {
        this.setOrder(customerOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItem)) {
            return false;
        }
        return getId() != null && getId().equals(((OrderItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItem{" +
            "id=" + getId() +
            ", quantityKg=" + getQuantityKg() +
            ", priceAtOrder=" + getPriceAtOrder() +
            "}";
    }
}
