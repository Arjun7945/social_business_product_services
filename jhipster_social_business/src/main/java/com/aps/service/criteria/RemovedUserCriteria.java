package com.aps.service.criteria;

import com.aps.domain.enumeration.AccountStatus;
import com.aps.domain.enumeration.UserRole;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.RemovedUser} entity. This class is used
 * in {@link com.aps.web.rest.RemovedUserResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /removed-users?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RemovedUserCriteria implements Serializable, Criteria {

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

    /**
     * Class for filtering AccountStatus
     */
    public static class AccountStatusFilter extends Filter<AccountStatus> {

        public AccountStatusFilter() {}

        public AccountStatusFilter(AccountStatusFilter filter) {
            super(filter);
        }

        @Override
        public AccountStatusFilter copy() {
            return new AccountStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter originalId;

    private StringFilter name;

    private UserRoleFilter role;

    private StringFilter whatsappNumber;

    private StringFilter phoneNumber;

    private StringFilter address;

    private DoubleFilter locationLat;

    private DoubleFilter locationLon;

    private InstantFilter joinedAt;

    private InstantFilter removedAt;

    private StringFilter reasonForRemoval;

    private AccountStatusFilter status;

    private LongFilter orderHistoryId;

    private DoubleFilter distanceFromBusinessKm;

    private BooleanFilter isPincodeValid;

    private Boolean distinct;

    public RemovedUserCriteria() {}

    public RemovedUserCriteria(RemovedUserCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.originalId = other.optionalOriginalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.role = other.optionalRole().map(UserRoleFilter::copy).orElse(null);
        this.whatsappNumber = other.optionalWhatsappNumber().map(StringFilter::copy).orElse(null);
        this.phoneNumber = other.optionalPhoneNumber().map(StringFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.locationLat = other.optionalLocationLat().map(DoubleFilter::copy).orElse(null);
        this.locationLon = other.optionalLocationLon().map(DoubleFilter::copy).orElse(null);
        this.joinedAt = other.optionalJoinedAt().map(InstantFilter::copy).orElse(null);
        this.removedAt = other.optionalRemovedAt().map(InstantFilter::copy).orElse(null);
        this.reasonForRemoval = other.optionalReasonForRemoval().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(AccountStatusFilter::copy).orElse(null);
        this.orderHistoryId = other.optionalOrderHistoryId().map(LongFilter::copy).orElse(null);
        this.distanceFromBusinessKm = other.optionalDistanceFromBusinessKm().map(DoubleFilter::copy).orElse(null);
        this.isPincodeValid = other.optionalIsPincodeValid().map(BooleanFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RemovedUserCriteria copy() {
        return new RemovedUserCriteria(this);
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

    public LongFilter getOriginalId() {
        return originalId;
    }

    public Optional<LongFilter> optionalOriginalId() {
        return Optional.ofNullable(originalId);
    }

    public LongFilter originalId() {
        if (originalId == null) {
            setOriginalId(new LongFilter());
        }
        return originalId;
    }

    public void setOriginalId(LongFilter originalId) {
        this.originalId = originalId;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public UserRoleFilter getRole() {
        return role;
    }

    public Optional<UserRoleFilter> optionalRole() {
        return Optional.ofNullable(role);
    }

    public UserRoleFilter role() {
        if (role == null) {
            setRole(new UserRoleFilter());
        }
        return role;
    }

    public void setRole(UserRoleFilter role) {
        this.role = role;
    }

    public StringFilter getWhatsappNumber() {
        return whatsappNumber;
    }

    public Optional<StringFilter> optionalWhatsappNumber() {
        return Optional.ofNullable(whatsappNumber);
    }

    public StringFilter whatsappNumber() {
        if (whatsappNumber == null) {
            setWhatsappNumber(new StringFilter());
        }
        return whatsappNumber;
    }

    public void setWhatsappNumber(StringFilter whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public Optional<StringFilter> optionalPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            setPhoneNumber(new StringFilter());
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public DoubleFilter getLocationLat() {
        return locationLat;
    }

    public Optional<DoubleFilter> optionalLocationLat() {
        return Optional.ofNullable(locationLat);
    }

    public DoubleFilter locationLat() {
        if (locationLat == null) {
            setLocationLat(new DoubleFilter());
        }
        return locationLat;
    }

    public void setLocationLat(DoubleFilter locationLat) {
        this.locationLat = locationLat;
    }

    public DoubleFilter getLocationLon() {
        return locationLon;
    }

    public Optional<DoubleFilter> optionalLocationLon() {
        return Optional.ofNullable(locationLon);
    }

    public DoubleFilter locationLon() {
        if (locationLon == null) {
            setLocationLon(new DoubleFilter());
        }
        return locationLon;
    }

    public void setLocationLon(DoubleFilter locationLon) {
        this.locationLon = locationLon;
    }

    public InstantFilter getJoinedAt() {
        return joinedAt;
    }

    public Optional<InstantFilter> optionalJoinedAt() {
        return Optional.ofNullable(joinedAt);
    }

    public InstantFilter joinedAt() {
        if (joinedAt == null) {
            setJoinedAt(new InstantFilter());
        }
        return joinedAt;
    }

    public void setJoinedAt(InstantFilter joinedAt) {
        this.joinedAt = joinedAt;
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

    public StringFilter getReasonForRemoval() {
        return reasonForRemoval;
    }

    public Optional<StringFilter> optionalReasonForRemoval() {
        return Optional.ofNullable(reasonForRemoval);
    }

    public StringFilter reasonForRemoval() {
        if (reasonForRemoval == null) {
            setReasonForRemoval(new StringFilter());
        }
        return reasonForRemoval;
    }

    public void setReasonForRemoval(StringFilter reasonForRemoval) {
        this.reasonForRemoval = reasonForRemoval;
    }

    public AccountStatusFilter getStatus() {
        return status;
    }

    public Optional<AccountStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public AccountStatusFilter status() {
        if (status == null) {
            setStatus(new AccountStatusFilter());
        }
        return status;
    }

    public void setStatus(AccountStatusFilter status) {
        this.status = status;
    }

    public LongFilter getOrderHistoryId() {
        return orderHistoryId;
    }

    public Optional<LongFilter> optionalOrderHistoryId() {
        return Optional.ofNullable(orderHistoryId);
    }

    public LongFilter orderHistoryId() {
        if (orderHistoryId == null) {
            setOrderHistoryId(new LongFilter());
        }
        return orderHistoryId;
    }

    public void setOrderHistoryId(LongFilter orderHistoryId) {
        this.orderHistoryId = orderHistoryId;
    }

    public DoubleFilter getDistanceFromBusinessKm() {
        return distanceFromBusinessKm;
    }

    public Optional<DoubleFilter> optionalDistanceFromBusinessKm() {
        return Optional.ofNullable(distanceFromBusinessKm);
    }

    public DoubleFilter distanceFromBusinessKm() {
        if (distanceFromBusinessKm == null) {
            setDistanceFromBusinessKm(new DoubleFilter());
        }
        return distanceFromBusinessKm;
    }

    public void setDistanceFromBusinessKm(DoubleFilter distanceFromBusinessKm) {
        this.distanceFromBusinessKm = distanceFromBusinessKm;
    }

    public BooleanFilter getIsPincodeValid() {
        return isPincodeValid;
    }

    public Optional<BooleanFilter> optionalIsPincodeValid() {
        return Optional.ofNullable(isPincodeValid);
    }

    public BooleanFilter isPincodeValid() {
        if (isPincodeValid == null) {
            setIsPincodeValid(new BooleanFilter());
        }
        return isPincodeValid;
    }

    public void setIsPincodeValid(BooleanFilter isPincodeValid) {
        this.isPincodeValid = isPincodeValid;
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
        final RemovedUserCriteria that = (RemovedUserCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(originalId, that.originalId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(role, that.role) &&
            Objects.equals(whatsappNumber, that.whatsappNumber) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(address, that.address) &&
            Objects.equals(locationLat, that.locationLat) &&
            Objects.equals(locationLon, that.locationLon) &&
            Objects.equals(joinedAt, that.joinedAt) &&
            Objects.equals(removedAt, that.removedAt) &&
            Objects.equals(reasonForRemoval, that.reasonForRemoval) &&
            Objects.equals(status, that.status) &&
            Objects.equals(orderHistoryId, that.orderHistoryId) &&
            Objects.equals(distanceFromBusinessKm, that.distanceFromBusinessKm) &&
            Objects.equals(isPincodeValid, that.isPincodeValid) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            originalId,
            name,
            role,
            whatsappNumber,
            phoneNumber,
            address,
            locationLat,
            locationLon,
            joinedAt,
            removedAt,
            reasonForRemoval,
            status,
            orderHistoryId,
            distanceFromBusinessKm,
            isPincodeValid,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RemovedUserCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOriginalId().map(f -> "originalId=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalRole().map(f -> "role=" + f + ", ").orElse("") +
            optionalWhatsappNumber().map(f -> "whatsappNumber=" + f + ", ").orElse("") +
            optionalPhoneNumber().map(f -> "phoneNumber=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalLocationLat().map(f -> "locationLat=" + f + ", ").orElse("") +
            optionalLocationLon().map(f -> "locationLon=" + f + ", ").orElse("") +
            optionalJoinedAt().map(f -> "joinedAt=" + f + ", ").orElse("") +
            optionalRemovedAt().map(f -> "removedAt=" + f + ", ").orElse("") +
            optionalReasonForRemoval().map(f -> "reasonForRemoval=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalOrderHistoryId().map(f -> "orderHistoryId=" + f + ", ").orElse("") +
            optionalDistanceFromBusinessKm().map(f -> "distanceFromBusinessKm=" + f + ", ").orElse("") +
            optionalIsPincodeValid().map(f -> "isPincodeValid=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
