package com.aps.domain;

import com.aps.domain.enumeration.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Tracks the history of Order Status changes.
 * Essential for the Angular frontend status tracker.
 */
@Entity
@Table(name = "order_status_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderStatusHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @NotNull
    @Column(name = "change_time", nullable = false)
    private Instant changeTime;

    @JsonIgnoreProperties(value = { "history", "items", "customer", "deliveryPerson" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "history")
    private CustomerOrder customerOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderStatusHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public OrderStatusHistory status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Instant getChangeTime() {
        return this.changeTime;
    }

    public OrderStatusHistory changeTime(Instant changeTime) {
        this.setChangeTime(changeTime);
        return this;
    }

    public void setChangeTime(Instant changeTime) {
        this.changeTime = changeTime;
    }

    public CustomerOrder getCustomerOrder() {
        return this.customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        if (this.customerOrder != null) {
            this.customerOrder.setHistory(null);
        }
        if (customerOrder != null) {
            customerOrder.setHistory(this);
        }
        this.customerOrder = customerOrder;
    }

    public OrderStatusHistory customerOrder(CustomerOrder customerOrder) {
        this.setCustomerOrder(customerOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderStatusHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((OrderStatusHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderStatusHistory{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", changeTime='" + getChangeTime() + "'" +
            "}";
    }
}
