package com.aps.domain;

import com.aps.domain.enumeration.UserRole;
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
 * Customer entity representing the end-user.
 * Stores WhatsApp details, location, and flow state.
 * NOW LINKED TO: DeliveryZone (Category A, B, C...)
 */
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "wa_phone_number", nullable = false, unique = true)
    private String waPhoneNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "location_lat")
    private Double locationLat;

    @Column(name = "location_lon")
    private Double locationLon;

    @Column(name = "address")
    private String address;

    @Column(name = "distance_from_business_km")
    private Double distanceFromBusinessKm;

    @NotNull
    @Column(name = "is_pincode_valid", nullable = false)
    private Boolean isPincodeValid;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @Column(name = "last_interaction_at")
    private Instant lastInteractionAt;

    /**
     * One Costumer has many Orders
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "history", "items", "customer", "deliveryPerson" }, allowSetters = true)
    private Set<CustomerOrder> orders = new HashSet<>();

    /**
     * One Customer has one Shopping Cart
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "items", "customer" }, allowSetters = true)
    private Set<ShoppingCart> carts = new HashSet<>();

    /**
     * One Customer has many Returned Orders
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "histories", "items", "order", "customer" }, allowSetters = true)
    private Set<ReturnedOrder> returns = new HashSet<>();

    /**
     * Executive who added the Customer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember addedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "customers", "deliveryPersons" }, allowSetters = true)
    private DeliveryZone zone;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaPhoneNumber() {
        return this.waPhoneNumber;
    }

    public Customer waPhoneNumber(String waPhoneNumber) {
        this.setWaPhoneNumber(waPhoneNumber);
        return this;
    }

    public void setWaPhoneNumber(String waPhoneNumber) {
        this.waPhoneNumber = waPhoneNumber;
    }

    public String getName() {
        return this.name;
    }

    public Customer name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Customer phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getLocationLat() {
        return this.locationLat;
    }

    public Customer locationLat(Double locationLat) {
        this.setLocationLat(locationLat);
        return this;
    }

    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }

    public Double getLocationLon() {
        return this.locationLon;
    }

    public Customer locationLon(Double locationLon) {
        this.setLocationLon(locationLon);
        return this;
    }

    public void setLocationLon(Double locationLon) {
        this.locationLon = locationLon;
    }

    public String getAddress() {
        return this.address;
    }

    public Customer address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getDistanceFromBusinessKm() {
        return this.distanceFromBusinessKm;
    }

    public Customer distanceFromBusinessKm(Double distanceFromBusinessKm) {
        this.setDistanceFromBusinessKm(distanceFromBusinessKm);
        return this;
    }

    public void setDistanceFromBusinessKm(Double distanceFromBusinessKm) {
        this.distanceFromBusinessKm = distanceFromBusinessKm;
    }

    public Boolean getIsPincodeValid() {
        return this.isPincodeValid;
    }

    public Customer isPincodeValid(Boolean isPincodeValid) {
        this.setIsPincodeValid(isPincodeValid);
        return this;
    }

    public void setIsPincodeValid(Boolean isPincodeValid) {
        this.isPincodeValid = isPincodeValid;
    }

    public UserRole getRole() {
        return this.role;
    }

    public Customer role(UserRole role) {
        this.setRole(role);
        return this;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Instant getJoinedAt() {
        return this.joinedAt;
    }

    public Customer joinedAt(Instant joinedAt) {
        this.setJoinedAt(joinedAt);
        return this;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Instant getLastInteractionAt() {
        return this.lastInteractionAt;
    }

    public Customer lastInteractionAt(Instant lastInteractionAt) {
        this.setLastInteractionAt(lastInteractionAt);
        return this;
    }

    public void setLastInteractionAt(Instant lastInteractionAt) {
        this.lastInteractionAt = lastInteractionAt;
    }

    public Set<CustomerOrder> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<CustomerOrder> customerOrders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setCustomer(null));
        }
        if (customerOrders != null) {
            customerOrders.forEach(i -> i.setCustomer(this));
        }
        this.orders = customerOrders;
    }

    public Customer orders(Set<CustomerOrder> customerOrders) {
        this.setOrders(customerOrders);
        return this;
    }

    public Customer addOrders(CustomerOrder customerOrder) {
        this.orders.add(customerOrder);
        customerOrder.setCustomer(this);
        return this;
    }

    public Customer removeOrders(CustomerOrder customerOrder) {
        this.orders.remove(customerOrder);
        customerOrder.setCustomer(null);
        return this;
    }

    public Set<ShoppingCart> getCarts() {
        return this.carts;
    }

    public void setCarts(Set<ShoppingCart> shoppingCarts) {
        if (this.carts != null) {
            this.carts.forEach(i -> i.setCustomer(null));
        }
        if (shoppingCarts != null) {
            shoppingCarts.forEach(i -> i.setCustomer(this));
        }
        this.carts = shoppingCarts;
    }

    public Customer carts(Set<ShoppingCart> shoppingCarts) {
        this.setCarts(shoppingCarts);
        return this;
    }

    public Customer addCart(ShoppingCart shoppingCart) {
        this.carts.add(shoppingCart);
        shoppingCart.setCustomer(this);
        return this;
    }

    public Customer removeCart(ShoppingCart shoppingCart) {
        this.carts.remove(shoppingCart);
        shoppingCart.setCustomer(null);
        return this;
    }

    public Set<ReturnedOrder> getReturns() {
        return this.returns;
    }

    public void setReturns(Set<ReturnedOrder> returnedOrders) {
        if (this.returns != null) {
            this.returns.forEach(i -> i.setCustomer(null));
        }
        if (returnedOrders != null) {
            returnedOrders.forEach(i -> i.setCustomer(this));
        }
        this.returns = returnedOrders;
    }

    public Customer returns(Set<ReturnedOrder> returnedOrders) {
        this.setReturns(returnedOrders);
        return this;
    }

    public Customer addReturns(ReturnedOrder returnedOrder) {
        this.returns.add(returnedOrder);
        returnedOrder.setCustomer(this);
        return this;
    }

    public Customer removeReturns(ReturnedOrder returnedOrder) {
        this.returns.remove(returnedOrder);
        returnedOrder.setCustomer(null);
        return this;
    }

    public TeamMember getAddedBy() {
        return this.addedBy;
    }

    public void setAddedBy(TeamMember teamMember) {
        this.addedBy = teamMember;
    }

    public Customer addedBy(TeamMember teamMember) {
        this.setAddedBy(teamMember);
        return this;
    }

    public DeliveryZone getZone() {
        return this.zone;
    }

    public void setZone(DeliveryZone deliveryZone) {
        this.zone = deliveryZone;
    }

    public Customer zone(DeliveryZone deliveryZone) {
        this.setZone(deliveryZone);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return getId() != null && getId().equals(((Customer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
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
            "}";
    }
}
