package com.aps.service.dto;

import com.aps.domain.enumeration.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.Customer} entity.
 */
@Schema(
    description = "Customer entity representing the end-user.\nStores WhatsApp details, location, and flow state.\nNOW LINKED TO: DeliveryZone (Category A, B, C...)"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerDTO implements Serializable {

    private Long id;

    @NotNull
    private String waPhoneNumber;

    private String name;

    private String phoneNumber;

    private Double locationLat;

    private Double locationLon;

    private String address;

    private Double distanceFromBusinessKm;

    @NotNull
    private Boolean isPincodeValid;

    @NotNull
    private UserRole role;

    private Instant joinedAt;

    private Instant lastInteractionAt;

    @Schema(description = "Executive who added the Customer")
    private TeamMemberDTO addedBy;

    private DeliveryZoneDTO zone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaPhoneNumber() {
        return waPhoneNumber;
    }

    public void setWaPhoneNumber(String waPhoneNumber) {
        this.waPhoneNumber = waPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }

    public Double getLocationLon() {
        return locationLon;
    }

    public void setLocationLon(Double locationLon) {
        this.locationLon = locationLon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getDistanceFromBusinessKm() {
        return distanceFromBusinessKm;
    }

    public void setDistanceFromBusinessKm(Double distanceFromBusinessKm) {
        this.distanceFromBusinessKm = distanceFromBusinessKm;
    }

    public Boolean getIsPincodeValid() {
        return isPincodeValid;
    }

    public void setIsPincodeValid(Boolean isPincodeValid) {
        this.isPincodeValid = isPincodeValid;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Instant getLastInteractionAt() {
        return lastInteractionAt;
    }

    public void setLastInteractionAt(Instant lastInteractionAt) {
        this.lastInteractionAt = lastInteractionAt;
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
        if (!(o instanceof CustomerDTO)) {
            return false;
        }

        CustomerDTO customerDTO = (CustomerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerDTO{" +
            "id=" + getId() +
            ", waPhoneNumber='" + getWaPhoneNumber() + "'" +
            ", name='" + getName() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", locationLat=" + getLocationLat() +
            ", locationLon=" + getLocationLon() +
            ", address='" + getAddress() + "'" +
            ", distanceFromBusinessKm=" + getDistanceFromBusinessKm() +
            ", isPincodeValid='" + getIsPincodeValid() + "'" +
            ", role='" + getRole() + "'" +
            ", joinedAt='" + getJoinedAt() + "'" +
            ", lastInteractionAt='" + getLastInteractionAt() + "'" +
            ", addedBy=" + getAddedBy() +
            ", zone=" + getZone() +
            "}";
    }
}
