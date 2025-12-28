package com.aps.domain;

import com.aps.domain.enumeration.ReturnStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Track the lifecycle of a return (Requested -> Picked Up -> Refunded).
 * Enables frontend tracking for customers.
 */
@Entity
@Table(name = "return_status_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnStatusHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReturnStatus status;

    @NotNull
    @Column(name = "change_time", nullable = false)
    private Instant changeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "histories", "items", "order", "customer" }, allowSetters = true)
    private ReturnedOrder returnedOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReturnStatusHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReturnStatus getStatus() {
        return this.status;
    }

    public ReturnStatusHistory status(ReturnStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public Instant getChangeTime() {
        return this.changeTime;
    }

    public ReturnStatusHistory changeTime(Instant changeTime) {
        this.setChangeTime(changeTime);
        return this;
    }

    public void setChangeTime(Instant changeTime) {
        this.changeTime = changeTime;
    }

    public ReturnedOrder getReturnedOrder() {
        return this.returnedOrder;
    }

    public void setReturnedOrder(ReturnedOrder returnedOrder) {
        this.returnedOrder = returnedOrder;
    }

    public ReturnStatusHistory returnedOrder(ReturnedOrder returnedOrder) {
        this.setReturnedOrder(returnedOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnStatusHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((ReturnStatusHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnStatusHistory{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", changeTime='" + getChangeTime() + "'" +
            "}";
    }
}
