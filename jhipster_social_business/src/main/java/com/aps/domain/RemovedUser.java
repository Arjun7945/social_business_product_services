package com.aps.domain;

import com.aps.domain.enumeration.AccountStatus;
import com.aps.domain.enumeration.UserRole;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * RemovedUser.
 * Updated to include distance and pincode status for audit.
 */
@Entity
@Table(name = "removed_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RemovedUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "original_id")
    private Long originalId;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Column(name = "whatsapp_number")
    private String whatsappNumber;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "location_lat")
    private Double locationLat;

    @Column(name = "location_lon")
    private Double locationLon;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @Column(name = "removed_at")
    private Instant removedAt;

    @Column(name = "reason_for_removal")
    private String reasonForRemoval;

    @Lob
    @Column(name = "last_session_data")
    private String lastSessionData;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "order_history_id")
    private Long orderHistoryId;

    /**
     * NEW: Stored for Customer audit
     */
    @Column(name = "distance_from_business_km")
    private Double distanceFromBusinessKm;

    /**
     * NEW: Stored for Customer audit
     */
    @Column(name = "is_pincode_valid")
    private Boolean isPincodeValid;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RemovedUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOriginalId() {
        return this.originalId;
    }

    public RemovedUser originalId(Long originalId) {
        this.setOriginalId(originalId);
        return this;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    public String getName() {
        return this.name;
    }

    public RemovedUser name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getRole() {
        return this.role;
    }

    public RemovedUser role(UserRole role) {
        this.setRole(role);
        return this;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getWhatsappNumber() {
        return this.whatsappNumber;
    }

    public RemovedUser whatsappNumber(String whatsappNumber) {
        this.setWhatsappNumber(whatsappNumber);
        return this;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public RemovedUser phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public RemovedUser address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLocationLat() {
        return this.locationLat;
    }

    public RemovedUser locationLat(Double locationLat) {
        this.setLocationLat(locationLat);
        return this;
    }

    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }

    public Double getLocationLon() {
        return this.locationLon;
    }

    public RemovedUser locationLon(Double locationLon) {
        this.setLocationLon(locationLon);
        return this;
    }

    public void setLocationLon(Double locationLon) {
        this.locationLon = locationLon;
    }

    public Instant getJoinedAt() {
        return this.joinedAt;
    }

    public RemovedUser joinedAt(Instant joinedAt) {
        this.setJoinedAt(joinedAt);
        return this;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Instant getRemovedAt() {
        return this.removedAt;
    }

    public RemovedUser removedAt(Instant removedAt) {
        this.setRemovedAt(removedAt);
        return this;
    }

    public void setRemovedAt(Instant removedAt) {
        this.removedAt = removedAt;
    }

    public String getReasonForRemoval() {
        return this.reasonForRemoval;
    }

    public RemovedUser reasonForRemoval(String reasonForRemoval) {
        this.setReasonForRemoval(reasonForRemoval);
        return this;
    }

    public void setReasonForRemoval(String reasonForRemoval) {
        this.reasonForRemoval = reasonForRemoval;
    }

    public String getLastSessionData() {
        return this.lastSessionData;
    }

    public RemovedUser lastSessionData(String lastSessionData) {
        this.setLastSessionData(lastSessionData);
        return this;
    }

    public void setLastSessionData(String lastSessionData) {
        this.lastSessionData = lastSessionData;
    }

    public AccountStatus getStatus() {
        return this.status;
    }

    public RemovedUser status(AccountStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getOrderHistoryId() {
        return this.orderHistoryId;
    }

    public RemovedUser orderHistoryId(Long orderHistoryId) {
        this.setOrderHistoryId(orderHistoryId);
        return this;
    }

    public void setOrderHistoryId(Long orderHistoryId) {
        this.orderHistoryId = orderHistoryId;
    }

    public Double getDistanceFromBusinessKm() {
        return this.distanceFromBusinessKm;
    }

    public RemovedUser distanceFromBusinessKm(Double distanceFromBusinessKm) {
        this.setDistanceFromBusinessKm(distanceFromBusinessKm);
        return this;
    }

    public void setDistanceFromBusinessKm(Double distanceFromBusinessKm) {
        this.distanceFromBusinessKm = distanceFromBusinessKm;
    }

    public Boolean getIsPincodeValid() {
        return this.isPincodeValid;
    }

    public RemovedUser isPincodeValid(Boolean isPincodeValid) {
        this.setIsPincodeValid(isPincodeValid);
        return this;
    }

    public void setIsPincodeValid(Boolean isPincodeValid) {
        this.isPincodeValid = isPincodeValid;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemovedUser)) {
            return false;
        }
        return getId() != null && getId().equals(((RemovedUser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RemovedUser{" +
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
            "}";
    }
}
