package com.aps.domain;

import com.aps.domain.enumeration.ReturnProductStatus;
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
 * NEW ENTITY: ReturnedOrder
 * Stores information about returned orders.
 * Linked to Order, and Customer for full context.
 * NOTE: Products are now Master-Detail via ReturnedOrderItem.
 */
@Entity
@Table(name = "returned_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnedOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "return_date", nullable = false)
    private Instant returnDate;

    @Column(name = "payment_received_mode")
    private String paymentReceivedMode;

    @Column(name = "payment_returned_mode")
    private String paymentReturnedMode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "product_claim_status", nullable = false)
    private ReturnProductStatus productClaimStatus;

    @DecimalMin(value = "0")
    @Column(name = "refund_amount", precision = 21, scale = 2)
    private BigDecimal refundAmount;

    /**
     * Return Status History tracking
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "returnedOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "returnedOrder" }, allowSetters = true)
    private Set<ReturnStatusHistory> histories = new HashSet<>();

    /**
     * Support multiple products in one return order
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "returnedOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "returnedOrder" }, allowSetters = true)
    private Set<ReturnedOrderItem> items = new HashSet<>();

    /**
     * Returned Order links to Original Order
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "history", "items", "customer", "deliveryPerson" }, allowSetters = true)
    private CustomerOrder order;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "orders", "carts", "returns", "addedBy", "zone" }, allowSetters = true)
    private Customer customer;

    @Column(name = "removed_customer_id")
    private Long removedCustomerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReturnedOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getReturnDate() {
        return this.returnDate;
    }

    public ReturnedOrder returnDate(Instant returnDate) {
        this.setReturnDate(returnDate);
        return this;
    }

    public void setReturnDate(Instant returnDate) {
        this.returnDate = returnDate;
    }

    public String getPaymentReceivedMode() {
        return this.paymentReceivedMode;
    }

    public ReturnedOrder paymentReceivedMode(String paymentReceivedMode) {
        this.setPaymentReceivedMode(paymentReceivedMode);
        return this;
    }

    public void setPaymentReceivedMode(String paymentReceivedMode) {
        this.paymentReceivedMode = paymentReceivedMode;
    }

    public String getPaymentReturnedMode() {
        return this.paymentReturnedMode;
    }

    public ReturnedOrder paymentReturnedMode(String paymentReturnedMode) {
        this.setPaymentReturnedMode(paymentReturnedMode);
        return this;
    }

    public void setPaymentReturnedMode(String paymentReturnedMode) {
        this.paymentReturnedMode = paymentReturnedMode;
    }

    public ReturnProductStatus getProductClaimStatus() {
        return this.productClaimStatus;
    }

    public ReturnedOrder productClaimStatus(ReturnProductStatus productClaimStatus) {
        this.setProductClaimStatus(productClaimStatus);
        return this;
    }

    public void setProductClaimStatus(ReturnProductStatus productClaimStatus) {
        this.productClaimStatus = productClaimStatus;
    }

    public BigDecimal getRefundAmount() {
        return this.refundAmount;
    }

    public ReturnedOrder refundAmount(BigDecimal refundAmount) {
        this.setRefundAmount(refundAmount);
        return this;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Set<ReturnStatusHistory> getHistories() {
        return this.histories;
    }

    public void setHistories(Set<ReturnStatusHistory> returnStatusHistories) {
        if (this.histories != null) {
            this.histories.forEach(i -> i.setReturnedOrder(null));
        }
        if (returnStatusHistories != null) {
            returnStatusHistories.forEach(i -> i.setReturnedOrder(this));
        }
        this.histories = returnStatusHistories;
    }

    public ReturnedOrder histories(Set<ReturnStatusHistory> returnStatusHistories) {
        this.setHistories(returnStatusHistories);
        return this;
    }

    public ReturnedOrder addHistory(ReturnStatusHistory returnStatusHistory) {
        this.histories.add(returnStatusHistory);
        returnStatusHistory.setReturnedOrder(this);
        return this;
    }

    public ReturnedOrder removeHistory(ReturnStatusHistory returnStatusHistory) {
        this.histories.remove(returnStatusHistory);
        returnStatusHistory.setReturnedOrder(null);
        return this;
    }

    public Set<ReturnedOrderItem> getItems() {
        return this.items;
    }

    public void setItems(Set<ReturnedOrderItem> returnedOrderItems) {
        if (this.items != null) {
            this.items.forEach(i -> i.setReturnedOrder(null));
        }
        if (returnedOrderItems != null) {
            returnedOrderItems.forEach(i -> i.setReturnedOrder(this));
        }
        this.items = returnedOrderItems;
    }

    public ReturnedOrder items(Set<ReturnedOrderItem> returnedOrderItems) {
        this.setItems(returnedOrderItems);
        return this;
    }

    public ReturnedOrder addItems(ReturnedOrderItem returnedOrderItem) {
        this.items.add(returnedOrderItem);
        returnedOrderItem.setReturnedOrder(this);
        return this;
    }

    public ReturnedOrder removeItems(ReturnedOrderItem returnedOrderItem) {
        this.items.remove(returnedOrderItem);
        returnedOrderItem.setReturnedOrder(null);
        return this;
    }

    public CustomerOrder getOrder() {
        return this.order;
    }

    public void setOrder(CustomerOrder customerOrder) {
        this.order = customerOrder;
    }

    public ReturnedOrder order(CustomerOrder customerOrder) {
        this.setOrder(customerOrder);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ReturnedOrder customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Long getRemovedCustomerId() {
        return this.removedCustomerId;
    }

    public ReturnedOrder removedCustomerId(Long removedCustomerId) {
        this.setRemovedCustomerId(removedCustomerId);
        return this;
    }

    public void setRemovedCustomerId(Long removedCustomerId) {
        this.removedCustomerId = removedCustomerId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnedOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((ReturnedOrder) o).getId());
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
        return "ReturnedOrder{" +
                "id=" + getId() +
                ", returnDate='" + getReturnDate() + "'" +
                ", paymentReceivedMode='" + getPaymentReceivedMode() + "'" +
                ", paymentReturnedMode='" + getPaymentReturnedMode() + "'" +
                ", productClaimStatus='" + getProductClaimStatus() + "'" +
                ", refundAmount=" + getRefundAmount() +
                "}";
    }
}
