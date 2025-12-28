package com.aps.service.dto;

import com.aps.domain.enumeration.ReturnStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.ReturnStatusHistory} entity.
 */
@Schema(description = "Track the lifecycle of a return (Requested -> Picked Up -> Refunded).\nEnables frontend tracking for customers.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnStatusHistoryDTO implements Serializable {

    private Long id;

    @NotNull
    private ReturnStatus status;

    @NotNull
    private Instant changeTime;

    private ReturnedOrderDTO returnedOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public Instant getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Instant changeTime) {
        this.changeTime = changeTime;
    }

    public ReturnedOrderDTO getReturnedOrder() {
        return returnedOrder;
    }

    public void setReturnedOrder(ReturnedOrderDTO returnedOrder) {
        this.returnedOrder = returnedOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnStatusHistoryDTO)) {
            return false;
        }

        ReturnStatusHistoryDTO returnStatusHistoryDTO = (ReturnStatusHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, returnStatusHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnStatusHistoryDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", changeTime='" + getChangeTime() + "'" +
            ", returnedOrder=" + getReturnedOrder() +
            "}";
    }
}
