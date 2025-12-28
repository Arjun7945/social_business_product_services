package com.aps.domain;

import com.aps.domain.enumeration.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * DeliveryPerson
 * Separated from TeamMember to handle specific delivery logic and categorization.
 * SPLIT BY: DeliveryZone (A delivery person in Zone A only delivers to Customers in Zone A).
 * NEW: Added by Team Member relationship.
 */
@Entity
@Table(name = "delivery_person")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryPerson implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "wa_phone_number", unique = true)
    private String waPhoneNumber;

    @NotNull
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    /**
     * FREE, BUSY, OFF_DUTY
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * One Delivery Person controls many Orders (History/Active)
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "deliveryPerson")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "history", "items", "customer", "deliveryPerson" }, allowSetters = true)
    private Set<CustomerOrder> orders = new HashSet<>();

    /**
     * Delivery Person added by TeamMember (e.g. Admin)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember addedBy;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "customers", "deliveryPersons" }, allowSetters = true)
    private DeliveryZone zone;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DeliveryPerson id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public DeliveryPerson name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWaPhoneNumber() {
        return this.waPhoneNumber;
    }

    public DeliveryPerson waPhoneNumber(String waPhoneNumber) {
        this.setWaPhoneNumber(waPhoneNumber);
        return this;
    }

    public void setWaPhoneNumber(String waPhoneNumber) {
        this.waPhoneNumber = waPhoneNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public DeliveryPerson phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public DeliveryStatus getStatus() {
        return this.status;
    }

    public DeliveryPerson status(DeliveryStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public Instant getJoinedAt() {
        return this.joinedAt;
    }

    public DeliveryPerson joinedAt(Instant joinedAt) {
        this.setJoinedAt(joinedAt);
        return this;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public DeliveryPerson isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Set<CustomerOrder> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<CustomerOrder> customerOrders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setDeliveryPerson(null));
        }
        if (customerOrders != null) {
            customerOrders.forEach(i -> i.setDeliveryPerson(this));
        }
        this.orders = customerOrders;
    }

    public DeliveryPerson orders(Set<CustomerOrder> customerOrders) {
        this.setOrders(customerOrders);
        return this;
    }

    public DeliveryPerson addOrders(CustomerOrder customerOrder) {
        this.orders.add(customerOrder);
        customerOrder.setDeliveryPerson(this);
        return this;
    }

    public DeliveryPerson removeOrders(CustomerOrder customerOrder) {
        this.orders.remove(customerOrder);
        customerOrder.setDeliveryPerson(null);
        return this;
    }

    public TeamMember getAddedBy() {
        return this.addedBy;
    }

    public void setAddedBy(TeamMember teamMember) {
        this.addedBy = teamMember;
    }

    public DeliveryPerson addedBy(TeamMember teamMember) {
        this.setAddedBy(teamMember);
        return this;
    }

    public DeliveryZone getZone() {
        return this.zone;
    }

    public void setZone(DeliveryZone deliveryZone) {
        this.zone = deliveryZone;
    }

    public DeliveryPerson zone(DeliveryZone deliveryZone) {
        this.setZone(deliveryZone);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryPerson)) {
            return false;
        }
        return getId() != null && getId().equals(((DeliveryPerson) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryPerson{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", waPhoneNumber='" + getWaPhoneNumber() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", status='" + getStatus() + "'" +
            ", joinedAt='" + getJoinedAt() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
