package com.aps.service.dto;

import com.aps.domain.enumeration.DeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.DeliveryPerson} entity.
 */
@Schema(description = "DeliveryPerson\nSeparated from TeamMember to handle specific delivery logic and categorization.\nSPLIT BY: DeliveryZone (A delivery person in Zone A only delivers to Customers in Zone A).\nNEW: Added by Team Member relationship.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryPersonDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String waPhoneNumber;

    @NotNull
    private String phoneNumber;

    @NotNull
    @Schema(description = "FREE, BUSY, OFF_DUTY", requiredMode = Schema.RequiredMode.REQUIRED)
    private DeliveryStatus status;

    private Instant joinedAt;

    @NotNull
    private Boolean isActive;

    @Schema(description = "Delivery Person added by TeamMember (e.g. Admin)")
    private TeamMemberDTO addedBy;

    @NotNull
    private DeliveryZoneDTO zone;

    private String chosenOrder;

    @NotNull
    @Min(value = 2)
    private Integer chosenOrderLimit = 5;

    public String getChosenOrder() {
        return chosenOrder;
    }

    public void setChosenOrder(String chosenOrder) {
        this.chosenOrder = chosenOrder;
    }

    public Integer getChosenOrderLimit() {
        return chosenOrderLimit;
    }

    public void setChosenOrderLimit(Integer chosenOrderLimit) {
        this.chosenOrderLimit = chosenOrderLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWaPhoneNumber() {
        return waPhoneNumber;
    }

    public void setWaPhoneNumber(String waPhoneNumber) {
        this.waPhoneNumber = waPhoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public TeamMemberDTO getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(TeamMemberDTO addedBy) {
        this.addedBy = addedBy;
    }

    public DeliveryZoneDTO getZone() {
        return zone;
    }

    public void setZone(DeliveryZoneDTO zone) {
        this.zone = zone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryPersonDTO)) {
            return false;
        }

        DeliveryPersonDTO deliveryPersonDTO = (DeliveryPersonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, deliveryPersonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryPersonDTO{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", waPhoneNumber='" + getWaPhoneNumber() + "'" +
                ", phoneNumber='" + getPhoneNumber() + "'" +
                ", status='" + getStatus() + "'" +
                ", joinedAt='" + getJoinedAt() + "'" +
                ", isActive='" + getIsActive() + "'" +
                ", addedBy=" + getAddedBy() +
                ", zone=" + getZone() +
                "}";
    }
}
