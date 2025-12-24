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
 * Entity to track the history of Order Status changes.
 */
@Entity
@Table(name = "order_status_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "items", "deliveryPerson", "customer" }, allowSetters = true)
    private CustomerOrder customerOrder;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatusHistory id(Long id) {
        this.id = id;
        return this;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderStatusHistory status(OrderStatus status) {
        this.status = status;
        return this;
    }

    public Instant getChangeTime() {
        return this.changeTime;
    }

    public void setChangeTime(Instant changeTime) {
        this.changeTime = changeTime;
    }

    public OrderStatusHistory changeTime(Instant changeTime) {
        this.changeTime = changeTime;
        return this;
    }

    public CustomerOrder getCustomerOrder() {
        return this.customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public OrderStatusHistory customerOrder(CustomerOrder customerOrder) {
        this.setCustomerOrder(customerOrder);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderStatusHistory)) {
            return false;
        }
        return id != null && id.equals(((OrderStatusHistory) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "OrderStatusHistory{" +
                "id=" + getId() +
                ", status='" + getStatus() + "'" +
                ", changeTime='" + getChangeTime() + "'" +
                "}";
    }
}
