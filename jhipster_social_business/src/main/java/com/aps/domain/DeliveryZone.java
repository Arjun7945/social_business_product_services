package com.aps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * DeliveryZone
 * Represents the \"Category\" (A, B, C, etc.) based on pincodes.
 * Used to group Customers and DeliveryPersons.
 */
@Entity
@Table(name = "delivery_zone")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class DeliveryZone implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * e.g., \"A\", \"B\", \"North-1\"
     */
    @NotNull
    @Column(name = "zone_name", nullable = false, unique = true)
    private String zoneName;

    /**
     * specific pincode or pattern linked to this zone
     */
    @Column(name = "pincode")
    private String pincode;

    /**
     * One Zone has many Customers
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "zone")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "orders", "carts", "returns", "addedBy", "zone" }, allowSetters = true)
    private Set<Customer> customers = new HashSet<>();

    /**
     * One Zone has many Delivery Persons
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "zone")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "orders", "addedBy", "zone" }, allowSetters = true)
    private Set<DeliveryPerson> deliveryPersons = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DeliveryZone id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZoneName() {
        return this.zoneName;
    }

    public DeliveryZone zoneName(String zoneName) {
        this.setZoneName(zoneName);
        return this;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getPincode() {
        return this.pincode;
    }

    public DeliveryZone pincode(String pincode) {
        this.setPincode(pincode);
        return this;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Set<Customer> getCustomers() {
        return this.customers;
    }

    public void setCustomers(Set<Customer> customers) {
        if (this.customers != null) {
            this.customers.forEach(i -> i.setZone(null));
        }
        if (customers != null) {
            customers.forEach(i -> i.setZone(this));
        }
        this.customers = customers;
    }

    public DeliveryZone customers(Set<Customer> customers) {
        this.setCustomers(customers);
        return this;
    }

    public DeliveryZone addCustomers(Customer customer) {
        this.customers.add(customer);
        customer.setZone(this);
        return this;
    }

    public DeliveryZone removeCustomers(Customer customer) {
        this.customers.remove(customer);
        customer.setZone(null);
        return this;
    }

    public Set<DeliveryPerson> getDeliveryPersons() {
        return this.deliveryPersons;
    }

    public void setDeliveryPersons(Set<DeliveryPerson> deliveryPeople) {
        if (this.deliveryPersons != null) {
            this.deliveryPersons.forEach(i -> i.setZone(null));
        }
        if (deliveryPeople != null) {
            deliveryPeople.forEach(i -> i.setZone(this));
        }
        this.deliveryPersons = deliveryPeople;
    }

    public DeliveryZone deliveryPersons(Set<DeliveryPerson> deliveryPeople) {
        this.setDeliveryPersons(deliveryPeople);
        return this;
    }

    public DeliveryZone addDeliveryPersons(DeliveryPerson deliveryPerson) {
        this.deliveryPersons.add(deliveryPerson);
        deliveryPerson.setZone(this);
        return this;
    }

    public DeliveryZone removeDeliveryPersons(DeliveryPerson deliveryPerson) {
        this.deliveryPersons.remove(deliveryPerson);
        deliveryPerson.setZone(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryZone)) {
            return false;
        }
        return getId() != null && getId().equals(((DeliveryZone) o).getId());
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

}
