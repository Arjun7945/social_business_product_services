package com.aps.service.dto;

import com.aps.domain.enumeration.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.CustomerOrder} entity.
 */
@Schema(description = "Main Order entity.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerOrderDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant orderTime;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal totalAmount;

    @NotNull
    @Schema(description = "Updated Enum: NOT_TAKEN, ON_WAY, etc.", requiredMode = Schema.RequiredMode.REQUIRED)
    private OrderStatus status;

    @NotNull
    private String paymentMethod;

    private Instant confirmedAt;

    private Long removedCustomerId;

    private Long removedDeliveryPersonId;

    private String transactionId;

    @Schema(description = "One Order has exactly One Status History (Current Status Detail)")
    private OrderStatusHistoryDTO history;

    private CustomerDTO customer;

    private DeliveryPersonDTO deliveryPerson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Instant orderTime) {
        this.orderTime = orderTime;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Long getRemovedCustomerId() {
        return removedCustomerId;
    }

    public void setRemovedCustomerId(Long removedCustomerId) {
        this.removedCustomerId = removedCustomerId;
    }

    public Long getRemovedDeliveryPersonId() {
        return removedDeliveryPersonId;
    }

    public void setRemovedDeliveryPersonId(Long removedDeliveryPersonId) {
        this.removedDeliveryPersonId = removedDeliveryPersonId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public OrderStatusHistoryDTO getHistory() {
        return history;
    }

    public void setHistory(OrderStatusHistoryDTO history) {
        this.history = history;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public DeliveryPersonDTO getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(DeliveryPersonDTO deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerOrderDTO)) {
            return false;
        }

        CustomerOrderDTO customerOrderDTO = (CustomerOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customerOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerOrderDTO{" +
            "id=" + getId() +
            ", orderTime='" + getOrderTime() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", status='" + getStatus() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", confirmedAt='" + getConfirmedAt() + "'" +
            ", removedCustomerId=" + getRemovedCustomerId() +
            ", removedDeliveryPersonId=" + getRemovedDeliveryPersonId() +
            ", transactionId='" + getTransactionId() + "'" +
            ", history=" + getHistory() +
            ", customer=" + getCustomer() +
            ", deliveryPerson=" + getDeliveryPerson() +
            "}";
    }
}
