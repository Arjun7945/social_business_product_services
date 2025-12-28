package com.aps.domain;

import com.aps.domain.enumeration.UserRole;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * RemovedOrderSummary.
 */
@Entity
@Table(name = "removed_order_summary")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RemovedOrderSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "user_original_id")
    private Long userOriginalId;

    @Column(name = "user_name")
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "first_interaction_at")
    private Instant firstInteractionAt;

    @Column(name = "last_interaction_at")
    private Instant lastInteractionAt;

    @Column(name = "removed_at")
    private Instant removedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RemovedOrderSummary id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserOriginalId() {
        return this.userOriginalId;
    }

    public RemovedOrderSummary userOriginalId(Long userOriginalId) {
        this.setUserOriginalId(userOriginalId);
        return this;
    }

    public void setUserOriginalId(Long userOriginalId) {
        this.userOriginalId = userOriginalId;
    }

    public String getUserName() {
        return this.userName;
    }

    public RemovedOrderSummary userName(String userName) {
        this.setUserName(userName);
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserRole getUserRole() {
        return this.userRole;
    }

    public RemovedOrderSummary userRole(UserRole userRole) {
        this.setUserRole(userRole);
        return this;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Integer getTotalOrders() {
        return this.totalOrders;
    }

    public RemovedOrderSummary totalOrders(Integer totalOrders) {
        this.setTotalOrders(totalOrders);
        return this;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Double getTotalAmount() {
        return this.totalAmount;
    }

    public RemovedOrderSummary totalAmount(Double totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getFirstInteractionAt() {
        return this.firstInteractionAt;
    }

    public RemovedOrderSummary firstInteractionAt(Instant firstInteractionAt) {
        this.setFirstInteractionAt(firstInteractionAt);
        return this;
    }

    public void setFirstInteractionAt(Instant firstInteractionAt) {
        this.firstInteractionAt = firstInteractionAt;
    }

    public Instant getLastInteractionAt() {
        return this.lastInteractionAt;
    }

    public RemovedOrderSummary lastInteractionAt(Instant lastInteractionAt) {
        this.setLastInteractionAt(lastInteractionAt);
        return this;
    }

    public void setLastInteractionAt(Instant lastInteractionAt) {
        this.lastInteractionAt = lastInteractionAt;
    }

    public Instant getRemovedAt() {
        return this.removedAt;
    }

    public RemovedOrderSummary removedAt(Instant removedAt) {
        this.setRemovedAt(removedAt);
        return this;
    }

    public void setRemovedAt(Instant removedAt) {
        this.removedAt = removedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemovedOrderSummary)) {
            return false;
        }
        return getId() != null && getId().equals(((RemovedOrderSummary) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RemovedOrderSummary{" +
            "id=" + getId() +
            ", userOriginalId=" + getUserOriginalId() +
            ", userName='" + getUserName() + "'" +
            ", userRole='" + getUserRole() + "'" +
            ", totalOrders=" + getTotalOrders() +
            ", totalAmount=" + getTotalAmount() +
            ", firstInteractionAt='" + getFirstInteractionAt() + "'" +
            ", lastInteractionAt='" + getLastInteractionAt() + "'" +
            ", removedAt='" + getRemovedAt() + "'" +
            "}";
    }
}
