package com.aps.service.criteria;

import com.aps.domain.enumeration.UserRole;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.RemovedOrderSummary} entity. This class is used
 * in {@link com.aps.web.rest.RemovedOrderSummaryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /removed-order-summaries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RemovedOrderSummaryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering UserRole
     */
    public static class UserRoleFilter extends Filter<UserRole> {

        public UserRoleFilter() {}

        public UserRoleFilter(UserRoleFilter filter) {
            super(filter);
        }

        @Override
        public UserRoleFilter copy() {
            return new UserRoleFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter userOriginalId;

    private StringFilter userName;

    private UserRoleFilter userRole;

    private IntegerFilter totalOrders;

    private DoubleFilter totalAmount;

    private InstantFilter firstInteractionAt;

    private InstantFilter lastInteractionAt;

    private InstantFilter removedAt;

    private Boolean distinct;

    public RemovedOrderSummaryCriteria() {}

    public RemovedOrderSummaryCriteria(RemovedOrderSummaryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.userOriginalId = other.optionalUserOriginalId().map(LongFilter::copy).orElse(null);
        this.userName = other.optionalUserName().map(StringFilter::copy).orElse(null);
        this.userRole = other.optionalUserRole().map(UserRoleFilter::copy).orElse(null);
        this.totalOrders = other.optionalTotalOrders().map(IntegerFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(DoubleFilter::copy).orElse(null);
        this.firstInteractionAt = other.optionalFirstInteractionAt().map(InstantFilter::copy).orElse(null);
        this.lastInteractionAt = other.optionalLastInteractionAt().map(InstantFilter::copy).orElse(null);
        this.removedAt = other.optionalRemovedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RemovedOrderSummaryCriteria copy() {
        return new RemovedOrderSummaryCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getUserOriginalId() {
        return userOriginalId;
    }

    public Optional<LongFilter> optionalUserOriginalId() {
        return Optional.ofNullable(userOriginalId);
    }

    public LongFilter userOriginalId() {
        if (userOriginalId == null) {
            setUserOriginalId(new LongFilter());
        }
        return userOriginalId;
    }

    public void setUserOriginalId(LongFilter userOriginalId) {
        this.userOriginalId = userOriginalId;
    }

    public StringFilter getUserName() {
        return userName;
    }

    public Optional<StringFilter> optionalUserName() {
        return Optional.ofNullable(userName);
    }

    public StringFilter userName() {
        if (userName == null) {
            setUserName(new StringFilter());
        }
        return userName;
    }

    public void setUserName(StringFilter userName) {
        this.userName = userName;
    }

    public UserRoleFilter getUserRole() {
        return userRole;
    }

    public Optional<UserRoleFilter> optionalUserRole() {
        return Optional.ofNullable(userRole);
    }

    public UserRoleFilter userRole() {
        if (userRole == null) {
            setUserRole(new UserRoleFilter());
        }
        return userRole;
    }

    public void setUserRole(UserRoleFilter userRole) {
        this.userRole = userRole;
    }

    public IntegerFilter getTotalOrders() {
        return totalOrders;
    }

    public Optional<IntegerFilter> optionalTotalOrders() {
        return Optional.ofNullable(totalOrders);
    }

    public IntegerFilter totalOrders() {
        if (totalOrders == null) {
            setTotalOrders(new IntegerFilter());
        }
        return totalOrders;
    }

    public void setTotalOrders(IntegerFilter totalOrders) {
        this.totalOrders = totalOrders;
    }

    public DoubleFilter getTotalAmount() {
        return totalAmount;
    }

    public Optional<DoubleFilter> optionalTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }

    public DoubleFilter totalAmount() {
        if (totalAmount == null) {
            setTotalAmount(new DoubleFilter());
        }
        return totalAmount;
    }

    public void setTotalAmount(DoubleFilter totalAmount) {
        this.totalAmount = totalAmount;
    }

    public InstantFilter getFirstInteractionAt() {
        return firstInteractionAt;
    }

    public Optional<InstantFilter> optionalFirstInteractionAt() {
        return Optional.ofNullable(firstInteractionAt);
    }

    public InstantFilter firstInteractionAt() {
        if (firstInteractionAt == null) {
            setFirstInteractionAt(new InstantFilter());
        }
        return firstInteractionAt;
    }

    public void setFirstInteractionAt(InstantFilter firstInteractionAt) {
        this.firstInteractionAt = firstInteractionAt;
    }

    public InstantFilter getLastInteractionAt() {
        return lastInteractionAt;
    }

    public Optional<InstantFilter> optionalLastInteractionAt() {
        return Optional.ofNullable(lastInteractionAt);
    }

    public InstantFilter lastInteractionAt() {
        if (lastInteractionAt == null) {
            setLastInteractionAt(new InstantFilter());
        }
        return lastInteractionAt;
    }

    public void setLastInteractionAt(InstantFilter lastInteractionAt) {
        this.lastInteractionAt = lastInteractionAt;
    }

    public InstantFilter getRemovedAt() {
        return removedAt;
    }

    public Optional<InstantFilter> optionalRemovedAt() {
        return Optional.ofNullable(removedAt);
    }

    public InstantFilter removedAt() {
        if (removedAt == null) {
            setRemovedAt(new InstantFilter());
        }
        return removedAt;
    }

    public void setRemovedAt(InstantFilter removedAt) {
        this.removedAt = removedAt;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RemovedOrderSummaryCriteria that = (RemovedOrderSummaryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(userOriginalId, that.userOriginalId) &&
            Objects.equals(userName, that.userName) &&
            Objects.equals(userRole, that.userRole) &&
            Objects.equals(totalOrders, that.totalOrders) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(firstInteractionAt, that.firstInteractionAt) &&
            Objects.equals(lastInteractionAt, that.lastInteractionAt) &&
            Objects.equals(removedAt, that.removedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            userOriginalId,
            userName,
            userRole,
            totalOrders,
            totalAmount,
            firstInteractionAt,
            lastInteractionAt,
            removedAt,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RemovedOrderSummaryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUserOriginalId().map(f -> "userOriginalId=" + f + ", ").orElse("") +
            optionalUserName().map(f -> "userName=" + f + ", ").orElse("") +
            optionalUserRole().map(f -> "userRole=" + f + ", ").orElse("") +
            optionalTotalOrders().map(f -> "totalOrders=" + f + ", ").orElse("") +
            optionalTotalAmount().map(f -> "totalAmount=" + f + ", ").orElse("") +
            optionalFirstInteractionAt().map(f -> "firstInteractionAt=" + f + ", ").orElse("") +
            optionalLastInteractionAt().map(f -> "lastInteractionAt=" + f + ", ").orElse("") +
            optionalRemovedAt().map(f -> "removedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
