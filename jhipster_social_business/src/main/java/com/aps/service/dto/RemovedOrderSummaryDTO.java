package com.aps.service.dto;

import com.aps.domain.enumeration.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.RemovedOrderSummary} entity.
 */
@Schema(description = "RemovedOrderSummary.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RemovedOrderSummaryDTO implements Serializable {

    private Long id;

    private Long userOriginalId;

    private String userName;

    private UserRole userRole;

    private Integer totalOrders;

    private Double totalAmount;

    private Instant firstInteractionAt;

    private Instant lastInteractionAt;

    private Instant removedAt;

    private String addedBy;

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserOriginalId() {
        return userOriginalId;
    }

    public void setUserOriginalId(Long userOriginalId) {
        this.userOriginalId = userOriginalId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getFirstInteractionAt() {
        return firstInteractionAt;
    }

    public void setFirstInteractionAt(Instant firstInteractionAt) {
        this.firstInteractionAt = firstInteractionAt;
    }

    public Instant getLastInteractionAt() {
        return lastInteractionAt;
    }

    public void setLastInteractionAt(Instant lastInteractionAt) {
        this.lastInteractionAt = lastInteractionAt;
    }

    public Instant getRemovedAt() {
        return removedAt;
    }

    public void setRemovedAt(Instant removedAt) {
        this.removedAt = removedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemovedOrderSummaryDTO)) {
            return false;
        }

        RemovedOrderSummaryDTO removedOrderSummaryDTO = (RemovedOrderSummaryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, removedOrderSummaryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RemovedOrderSummaryDTO{" +
                "id=" + getId() +
                ", userOriginalId=" + getUserOriginalId() +
                ", userName='" + getUserName() + "'" +
                ", userRole='" + getUserRole() + "'" +
                ", totalOrders=" + getTotalOrders() +
                ", totalAmount=" + getTotalAmount() +
                ", firstInteractionAt='" + getFirstInteractionAt() + "'" +
                ", lastInteractionAt='" + getLastInteractionAt() + "'" +
                ", removedAt='" + getRemovedAt() + "'" +
                "}";
    }
}
