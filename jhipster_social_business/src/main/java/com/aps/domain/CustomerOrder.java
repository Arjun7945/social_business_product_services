package com.aps.domain;

import com.aps.domain.enumeration.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Main Order entity.
 * <p>
 * Represents a definitive purchase order in the system.
 * Updated to support "Global Archive" strategy with dedicated fields
 * for linking to removed customers and delivery personnel
 * (`removed_customer_id`, `removed_delivery_person_id`).
 * </p>
 */
@Entity
@Table(name = "customer_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CustomerOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "order_time", nullable = false)
    private Instant orderTime;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Version
    @Column(name = "version")
    private Long version;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @NotNull
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "transaction_id")
    private String transactionId;

    /**
     * One Order has many OrderItems
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "order" }, allowSetters = true)
    private Set<OrderItem> items = new HashSet<>();

    /**
     * TeamMember (Delivery) assigned to Order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember deliveryPerson;

    /**
     * Link to Archive if Customer is deleted.
     * Preserves history even after physical deletion of the Customer row.
     */
    @Column(name = "removed_customer_id")
    private Long removedCustomerId;

    /**
     * Link to Archive if Delivery Person is deleted.
     * Preserves history even after physical deletion of the TeamMember row.
     */
    @Column(name = "removed_delivery_person_id")
    private Long removedDeliveryPersonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "orders", "carts", "addedBy" }, allowSetters = true)
    private Customer customer;

    // Direct Getters and Setters

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getOrderTime() {
        return this.orderTime;
    }

    public void setOrderTime(Instant orderTime) {
        this.orderTime = orderTime;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getConfirmedAt() {
        return this.confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Set<OrderItem> getItems() {
        return this.items;
    }

    public void setItems(Set<OrderItem> orderItems) {
        if (this.items != null) {
            this.items.forEach(i -> i.setOrder(null));
        }
        if (orderItems != null) {
            orderItems.forEach(i -> i.setOrder(this));
        }
        this.items = orderItems;
    }

    public TeamMember getDeliveryPerson() {
        return this.deliveryPerson;
    }

    public void setDeliveryPerson(TeamMember teamMember) {
        this.deliveryPerson = teamMember;
    }

    public Long getRemovedCustomerId() {
        return this.removedCustomerId;
    }

    public void setRemovedCustomerId(Long removedCustomerId) {
        this.removedCustomerId = removedCustomerId;
    }

    public Long getRemovedDeliveryPersonId() {
        return this.removedDeliveryPersonId;
    }

    public void setRemovedDeliveryPersonId(Long removedDeliveryPersonId) {
        this.removedDeliveryPersonId = removedDeliveryPersonId;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Fluent Setters

    public CustomerOrder id(Long id) {
        this.id = id;
        return this;
    }

    public CustomerOrder orderTime(Instant orderTime) {
        this.orderTime = orderTime;
        return this;
    }

    public CustomerOrder totalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public CustomerOrder status(OrderStatus status) {
        this.status = status;
        return this;
    }

    public CustomerOrder paymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public CustomerOrder confirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
        return this;
    }

    public CustomerOrder transactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public CustomerOrder items(Set<OrderItem> orderItems) {
        this.setItems(orderItems);
        return this;
    }

    public CustomerOrder addItems(OrderItem orderItem) {
        this.items.add(orderItem);
        orderItem.setOrder(this);
        return this;
    }

    public CustomerOrder removeItems(OrderItem orderItem) {
        this.items.remove(orderItem);
        orderItem.setOrder(null);
        return this;
    }

    public CustomerOrder deliveryPerson(TeamMember teamMember) {
        this.setDeliveryPerson(teamMember);
        return this;
    }

    public CustomerOrder removedCustomerId(Long removedCustomerId) {
        this.setRemovedCustomerId(removedCustomerId);
        return this;
    }

    public CustomerOrder removedDeliveryPersonId(Long removedDeliveryPersonId) {
        this.setRemovedDeliveryPersonId(removedDeliveryPersonId);
        return this;
    }

    public CustomerOrder customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerOrder)) {
            return false;
        }
        return id != null && id.equals(((CustomerOrder) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CustomerOrder{" +
                "id=" + getId() +
                ", orderTime='" + getOrderTime() + "'" +
                ", totalAmount=" + getTotalAmount() +
                ", status='" + getStatus() + "'" +
                ", paymentMethod='" + getPaymentMethod() + "'" +
                ", transactionId='" + getTransactionId() + "'" +
                "}";
    }
}
