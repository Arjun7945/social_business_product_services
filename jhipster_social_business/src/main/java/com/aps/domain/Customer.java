package com.aps.domain;

import com.aps.domain.enumeration.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private Set<CustomerOrder> orders = new HashSet<>();

    /**
     * One Customer has one Shopping Cart
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "items", "customer" }, allowSetters = true)
    @Builder.Default
    private Set<ShoppingCart> carts = new HashSet<>();

    /**
     * One Customer has many Returned Orders
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "histories", "items", "order", "customer" }, allowSetters = true)
    @Builder.Default
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

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }

    public Customer waPhoneNumber(String waPhoneNumber) {
        this.setWaPhoneNumber(waPhoneNumber);
        return this;
    }

    public Customer name(String name) {
        this.setName(name);
        return this;
    }

    public Customer phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public Customer locationLat(Double locationLat) {
        this.setLocationLat(locationLat);
        return this;
    }

    public Customer locationLon(Double locationLon) {
        this.setLocationLon(locationLon);
        return this;
    }

    public Customer address(String address) {
        this.setAddress(address);
        return this;
    }

    public Customer distanceFromBusinessKm(Double distanceFromBusinessKm) {
        this.setDistanceFromBusinessKm(distanceFromBusinessKm);
        return this;
    }

    public Customer isPincodeValid(Boolean isPincodeValid) {
        this.setIsPincodeValid(isPincodeValid);
        return this;
    }

    public Customer role(UserRole role) {
        this.setRole(role);
        return this;
    }

    public Customer joinedAt(Instant joinedAt) {
        this.setJoinedAt(joinedAt);
        return this;
    }

    public Customer lastInteractionAt(Instant lastInteractionAt) {
        this.setLastInteractionAt(lastInteractionAt);
        return this;
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

    public Customer addedBy(TeamMember teamMember) {
        this.setAddedBy(teamMember);
        return this;
    }

    public Customer zone(DeliveryZone deliveryZone) {
        this.setZone(deliveryZone);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here
}
