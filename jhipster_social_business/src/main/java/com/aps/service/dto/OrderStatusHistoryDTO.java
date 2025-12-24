package com.aps.service.dto;

import com.aps.domain.enumeration.OrderStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.OrderStatusHistory} entity.
 */
public class OrderStatusHistoryDTO implements Serializable {

    private Long id;

    private OrderStatus status;

    private Instant changeTime;

    private CustomerOrderDTO customerOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Instant getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Instant changeTime) {
        this.changeTime = changeTime;
    }

    public CustomerOrderDTO getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrderDTO customerOrder) {
        this.customerOrder = customerOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderStatusHistoryDTO)) {
            return false;
        }

        OrderStatusHistoryDTO orderStatusHistoryDTO = (OrderStatusHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderStatusHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "OrderStatusHistoryDTO{" +
                "id=" + getId() +
                ", status='" + getStatus() + "'" +
                ", changeTime='" + getChangeTime() + "'" +
                "}";
    }
}
