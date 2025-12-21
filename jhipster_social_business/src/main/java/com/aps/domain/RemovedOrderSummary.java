package com.aps.domain;

import com.aps.domain.enumeration.UserRole;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RemovedOrderSummary.
 * <p>
 * Captures a consolidated snapshot of a user's operational history at the
 * moment of removal.
 * <br>
 * Semantics vary by role:
 * <ul>
 * <li><b>Customer:</b> totalOrders = Placed, totalAmount = Spent</li>
 * <li><b>Delivery:</b> totalOrders = Delivered, totalAmount = Collected</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "removed_order_summary")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    // Direct Getters and Setters

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserOriginalId() {
        return this.userOriginalId;
    }

    public void setUserOriginalId(Long userOriginalId) {
        this.userOriginalId = userOriginalId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserRole getUserRole() {
        return this.userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Integer getTotalOrders() {
        return this.totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Double getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getFirstInteractionAt() {
        return this.firstInteractionAt;
    }

    public void setFirstInteractionAt(Instant firstInteractionAt) {
        this.firstInteractionAt = firstInteractionAt;
    }

    public Instant getLastInteractionAt() {
        return this.lastInteractionAt;
    }

    public void setLastInteractionAt(Instant lastInteractionAt) {
        this.lastInteractionAt = lastInteractionAt;
    }

    public Instant getRemovedAt() {
        return this.removedAt;
    }

    public void setRemovedAt(Instant removedAt) {
        this.removedAt = removedAt;
    }

    // Fluent Setters

    public RemovedOrderSummary id(Long id) {
        this.id = id;
        return this;
    }

    public RemovedOrderSummary userName(String userName) {
        this.userName = userName;
        return this;
    }

    public RemovedOrderSummary totalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
        return this;
    }

    public RemovedOrderSummary totalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemovedOrderSummary)) {
            return false;
        }
        return id != null && id.equals(((RemovedOrderSummary) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "RemovedOrderSummary{" +
                "id=" + getId() +
                ", userName='" + getUserName() + "'" +
                ", userRole='" + getUserRole() + "'" +
                ", totalOrders=" + getTotalOrders() +
                ", totalAmount=" + getTotalAmount() +
                "}";
    }
}
