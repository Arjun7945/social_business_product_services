package com.aps.domain;

import com.aps.domain.enumeration.AccountStatus;
import com.aps.domain.enumeration.UserRole;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RemovedUser.
 * <p>
 * Stores comprehensive audit and restoration data for any user (Customer or
 * TeamMember)
 * who has been removed from the active system. This allows for:
 * 1. Audit trails (who was removed, when, and why).
 * 2. Potential account restoration.
 * 3. Linking orphaned historical orders to a known identity.
 * </p>
 */
@Entity
@Table(name = "removed_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RemovedUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * The original ID of the user in the source table (customer or team_member).
     */
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

    /**
     * Stored for potential restoration of delivery logistics.
     */
    @Column(name = "address")
    private String address;

    @Column(name = "location_lat")
    private Double locationLat;

    @Column(name = "location_lon")
    private Double locationLon;

    /**
     * The original join date, preserved to maintain seniority upon restoration.
     */
    @Column(name = "joined_at")
    private Instant joinedAt;

    @Column(name = "removed_at")
    private Instant removedAt;

    @Column(name = "reason_for_removal")
    private String reasonForRemoval;

    /**
     * Snapshot of the session state at the time of removal.
     */
    @Lob
    @Column(name = "last_session_data")
    private String lastSessionData;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    /**
     * Link to the {@link RemovedOrderSummary} containing aggregate financial stats.
     * Null if no relevant history exists (e.g. some staff roles).
     */
    @Column(name = "order_history_id")
    private Long orderHistoryId;

    // Direct Getters and Setters (JPA Requirement without Lombok)

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOriginalId() {
        return this.originalId;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getRole() {
        return this.role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getWhatsappNumber() {
        return this.whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLocationLat() {
        return this.locationLat;
    }

    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }

    public Double getLocationLon() {
        return this.locationLon;
    }

    public void setLocationLon(Double locationLon) {
        this.locationLon = locationLon;
    }

    public Instant getJoinedAt() {
        return this.joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Instant getRemovedAt() {
        return this.removedAt;
    }

    public void setRemovedAt(Instant removedAt) {
        this.removedAt = removedAt;
    }

    public String getReasonForRemoval() {
        return this.reasonForRemoval;
    }

    public void setReasonForRemoval(String reasonForRemoval) {
        this.reasonForRemoval = reasonForRemoval;
    }

    public String getLastSessionData() {
        return this.lastSessionData;
    }

    public void setLastSessionData(String lastSessionData) {
        this.lastSessionData = lastSessionData;
    }

    public AccountStatus getStatus() {
        return this.status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getOrderHistoryId() {
        return this.orderHistoryId;
    }

    public void setOrderHistoryId(Long orderHistoryId) {
        this.orderHistoryId = orderHistoryId;
    }

    // Fluent Setters for easier construction
    public RemovedUser id(Long id) {
        this.id = id;
        return this;
    }

    public RemovedUser name(String name) {
        this.name = name;
        return this;
    }

    public RemovedUser role(UserRole role) {
        this.role = role;
        return this;
    }

    public RemovedUser status(AccountStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemovedUser)) {
            return false;
        }
        return id != null && id.equals(((RemovedUser) o).id);
    }

    @Override
    public int hashCode() {
        // JPA entity recommendation: Use class hashCode to allow lazy loading proxies
        // to work correctly
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "RemovedUser{" +
            "id=" +
            getId() +
            ", originalId=" +
            getOriginalId() +
            ", name='" +
            getName() +
            "'" +
            ", role='" +
            getRole() +
            "'" +
            ", status='" +
            getStatus() +
            "'" +
            "}"
        );
    }
}
