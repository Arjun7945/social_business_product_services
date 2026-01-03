package com.aps.service.dto;

import com.aps.domain.enumeration.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.OrderStatusHistory} entity.
 */
@Schema(description = "Tracks the history of Order Status changes.\nEssential for the Angular frontend status tracker.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderStatusHistoryDTO implements Serializable {

    private Long id;

    @NotNull
    private OrderStatus status;

    @NotNull
    @NotNull
    private Instant changeTime;

    private Instant onWayTime;

    private Instant paymentPendingTime;

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

    public Instant getOnWayTime() {
        return onWayTime;
    }

    public void setOnWayTime(Instant onWayTime) {
        this.onWayTime = onWayTime;
    }

    public Instant getPaymentPendingTime() {
        return paymentPendingTime;
    }

    public void setPaymentPendingTime(Instant paymentPendingTime) {
        this.paymentPendingTime = paymentPendingTime;
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

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderStatusHistoryDTO{" +
                "id=" + getId() +
                ", status='" + getStatus() + "'" +
                ", status='" + getStatus() + "'" +
                ", changeTime='" + getChangeTime() + "'" +
                ", onWayTime='" + getOnWayTime() + "'" +
                ", paymentPendingTime='" + getPaymentPendingTime() + "'" +
                "}";
    }
}
