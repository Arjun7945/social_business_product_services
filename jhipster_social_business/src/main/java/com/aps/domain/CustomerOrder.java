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
 */
@Entity
@Table(name = "customer_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
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

    /**
     * Updated Enum: NOT_TAKEN, ON_WAY, etc.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @NotNull
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private com.aps.domain.enumeration.PaymentMode paymentMethod;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "removed_customer_id")
    private Long removedCustomerId;

    @Column(name = "removed_delivery_person_id")
    private Long removedDeliveryPersonId;

    @Column(name = "transaction_id")
    private String transactionId;

    /**
     * One Order has exactly One Status History (Current Status Detail)
     */
    @JsonIgnoreProperties(value = { "customerOrder" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private OrderStatusHistory history;

    /**
     * One Order has many OrderItems
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "order" }, allowSetters = true)
    private Set<OrderItem> items = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "orders", "carts", "returns", "addedBy", "zone" }, allowSetters = true)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "orders", "addedBy", "zone" }, allowSetters = true)
    private DeliveryPerson deliveryPerson;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CustomerOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getOrderTime() {
        return this.orderTime;
    }

    public CustomerOrder orderTime(Instant orderTime) {
        this.setOrderTime(orderTime);
        return this;
    }

    public void setOrderTime(Instant orderTime) {
        this.orderTime = orderTime;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public CustomerOrder totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public CustomerOrder status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public com.aps.domain.enumeration.PaymentMode getPaymentMethod() {
        return this.paymentMethod;
    }

    public CustomerOrder paymentMethod(com.aps.domain.enumeration.PaymentMode paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public void setPaymentMethod(com.aps.domain.enumeration.PaymentMode paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getConfirmedAt() {
        return this.confirmedAt;
    }

    public CustomerOrder confirmedAt(Instant confirmedAt) {
        this.setConfirmedAt(confirmedAt);
        return this;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Long getRemovedCustomerId() {
        return this.removedCustomerId;
    }

    public CustomerOrder removedCustomerId(Long removedCustomerId) {
        this.setRemovedCustomerId(removedCustomerId);
        return this;
    }

    public void setRemovedCustomerId(Long removedCustomerId) {
        this.removedCustomerId = removedCustomerId;
    }

    public Long getRemovedDeliveryPersonId() {
        return this.removedDeliveryPersonId;
    }

    public CustomerOrder removedDeliveryPersonId(Long removedDeliveryPersonId) {
        this.setRemovedDeliveryPersonId(removedDeliveryPersonId);
        return this;
    }

    public void setRemovedDeliveryPersonId(Long removedDeliveryPersonId) {
        this.removedDeliveryPersonId = removedDeliveryPersonId;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public CustomerOrder transactionId(String transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public OrderStatusHistory getHistory() {
        return this.history;
    }

    public void setHistory(OrderStatusHistory orderStatusHistory) {
        this.history = orderStatusHistory;
    }

    public CustomerOrder history(OrderStatusHistory orderStatusHistory) {
        this.setHistory(orderStatusHistory);
        return this;
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

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerOrder customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public DeliveryPerson getDeliveryPerson() {
        return this.deliveryPerson;
    }

    public void setDeliveryPerson(DeliveryPerson deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }

    public CustomerOrder deliveryPerson(DeliveryPerson deliveryPerson) {
        this.setDeliveryPerson(deliveryPerson);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((CustomerOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerOrder{" +
                "id=" + getId() +
                ", orderTime='" + getOrderTime() + "'" +
                ", totalAmount=" + getTotalAmount() +
                ", status='" + getStatus() + "'" +
                ", paymentMethod='" + getPaymentMethod() + "'" +
                ", confirmedAt='" + getConfirmedAt() + "'" +
                ", removedCustomerId=" + getRemovedCustomerId() +
                ", removedDeliveryPersonId=" + getRemovedDeliveryPersonId() +
                ", transactionId='" + getTransactionId() + "'" +
                "}";
    }
}
