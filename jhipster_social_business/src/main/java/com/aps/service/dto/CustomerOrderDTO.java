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
@Schema(description = "Main Order entity.\nRenamed to CustomerOrder to avoid SQL keyword conflicts.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerOrderDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant orderTime;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal totalAmount;

    @NotNull
    private OrderStatus status;

    @NotNull
    private String paymentMethod;

    private Instant confirmedAt;

    @Schema(description = "TeamMember (Delivery) assigned to Order")
    private TeamMemberDTO deliveryPerson;

    @NotNull
    private CustomerDTO customer;

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

    public TeamMemberDTO getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(TeamMemberDTO deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
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
            ", deliveryPerson=" + getDeliveryPerson() +
            ", customer=" + getCustomer() +
            "}";
    }
}
