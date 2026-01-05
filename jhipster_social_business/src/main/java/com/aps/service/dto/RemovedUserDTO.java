package com.aps.service.dto;

import com.aps.domain.enumeration.AccountStatus;
import com.aps.domain.enumeration.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.RemovedUser} entity.
 */
@Schema(description = "RemovedUser.\nUpdated to include distance and pincode status for audit.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RemovedUserDTO implements Serializable {

    private Long id;

    private Long originalId;

    private String name;

    private UserRole role;

    private String whatsappNumber;

    private String phoneNumber;

    private String address;

    private Double locationLat;

    private Double locationLon;

    private Instant joinedAt;

    private Instant removedAt;

    private String reasonForRemoval;

    @Lob
    private String lastSessionData;

    private AccountStatus status;

    private Long orderHistoryId;

    @Schema(description = "NEW: Stored for Customer audit")
    private Double distanceFromBusinessKm;

    @Schema(description = "NEW: Stored for Customer audit")
    private Boolean isPincodeValid;

    private String zoneName;

    private Long zoneId;

    private String addedBy;

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Instant getRemovedAt() {
        return removedAt;
    }

    public void setRemovedAt(Instant removedAt) {
        this.removedAt = removedAt;
    }

    public String getReasonForRemoval() {
        return reasonForRemoval;
    }

    public void setReasonForRemoval(String reasonForRemoval) {
        this.reasonForRemoval = reasonForRemoval;
    }

    public String getLastSessionData() {
        return lastSessionData;
    }

    public void setLastSessionData(String lastSessionData) {
        this.lastSessionData = lastSessionData;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getOrderHistoryId() {
        return orderHistoryId;
    }

    public void setOrderHistoryId(Long orderHistoryId) {
        this.orderHistoryId = orderHistoryId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemovedUserDTO)) {
            return false;
        }

        RemovedUserDTO removedUserDTO = (RemovedUserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, removedUserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RemovedUserDTO{" +
                "id=" + getId() +
                ", originalId=" + getOriginalId() +
                ", name='" + getName() + "'" +
                ", role='" + getRole() + "'" +
                ", whatsappNumber='" + getWhatsappNumber() + "'" +
                ", phoneNumber='" + getPhoneNumber() + "'" +
                ", address='" + getAddress() + "'" +
                ", locationLat=" + getLocationLat() +
                ", locationLon=" + getLocationLon() +
                ", joinedAt='" + getJoinedAt() + "'" +
                ", removedAt='" + getRemovedAt() + "'" +
                ", reasonForRemoval='" + getReasonForRemoval() + "'" +
                ", lastSessionData='" + getLastSessionData() + "'" +
                ", status='" + getStatus() + "'" +
                ", orderHistoryId=" + getOrderHistoryId() +
                ", distanceFromBusinessKm=" + getDistanceFromBusinessKm() +
                ", isPincodeValid='" + getIsPincodeValid() + "'" +
                ", zoneName='" + getZoneName() + "'" +
                ", zoneId=" + getZoneId() +
                "}";
    }
}
