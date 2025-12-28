package com.aps.service.criteria;

import com.aps.domain.enumeration.UserRole;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.Customer} entity. This class is used
 * in {@link com.aps.web.rest.CustomerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerCriteria implements Serializable, Criteria {

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

    private StringFilter waPhoneNumber;

    private StringFilter name;

    private StringFilter phoneNumber;

    private DoubleFilter locationLat;

    private DoubleFilter locationLon;

    private StringFilter address;

    private DoubleFilter distanceFromBusinessKm;

    private BooleanFilter isPincodeValid;

    private UserRoleFilter role;

    private InstantFilter joinedAt;

    private InstantFilter lastInteractionAt;

    private LongFilter ordersId;

    private LongFilter cartId;

    private LongFilter returnsId;

    private LongFilter addedById;

    private LongFilter zoneId;

    private Boolean distinct;

    public CustomerCriteria() {}

    public CustomerCriteria(CustomerCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.waPhoneNumber = other.optionalWaPhoneNumber().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.phoneNumber = other.optionalPhoneNumber().map(StringFilter::copy).orElse(null);
        this.locationLat = other.optionalLocationLat().map(DoubleFilter::copy).orElse(null);
        this.locationLon = other.optionalLocationLon().map(DoubleFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.distanceFromBusinessKm = other.optionalDistanceFromBusinessKm().map(DoubleFilter::copy).orElse(null);
        this.isPincodeValid = other.optionalIsPincodeValid().map(BooleanFilter::copy).orElse(null);
        this.role = other.optionalRole().map(UserRoleFilter::copy).orElse(null);
        this.joinedAt = other.optionalJoinedAt().map(InstantFilter::copy).orElse(null);
        this.lastInteractionAt = other.optionalLastInteractionAt().map(InstantFilter::copy).orElse(null);
        this.ordersId = other.optionalOrdersId().map(LongFilter::copy).orElse(null);
        this.cartId = other.optionalCartId().map(LongFilter::copy).orElse(null);
        this.returnsId = other.optionalReturnsId().map(LongFilter::copy).orElse(null);
        this.addedById = other.optionalAddedById().map(LongFilter::copy).orElse(null);
        this.zoneId = other.optionalZoneId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CustomerCriteria copy() {
        return new CustomerCriteria(this);
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

    public StringFilter getWaPhoneNumber() {
        return waPhoneNumber;
    }

    public Optional<StringFilter> optionalWaPhoneNumber() {
        return Optional.ofNullable(waPhoneNumber);
    }

    public StringFilter waPhoneNumber() {
        if (waPhoneNumber == null) {
            setWaPhoneNumber(new StringFilter());
        }
        return waPhoneNumber;
    }

    public void setWaPhoneNumber(StringFilter waPhoneNumber) {
        this.waPhoneNumber = waPhoneNumber;
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

    public LongFilter getOrdersId() {
        return ordersId;
    }

    public Optional<LongFilter> optionalOrdersId() {
        return Optional.ofNullable(ordersId);
    }

    public LongFilter ordersId() {
        if (ordersId == null) {
            setOrdersId(new LongFilter());
        }
        return ordersId;
    }

    public void setOrdersId(LongFilter ordersId) {
        this.ordersId = ordersId;
    }

    public LongFilter getCartId() {
        return cartId;
    }

    public Optional<LongFilter> optionalCartId() {
        return Optional.ofNullable(cartId);
    }

    public LongFilter cartId() {
        if (cartId == null) {
            setCartId(new LongFilter());
        }
        return cartId;
    }

    public void setCartId(LongFilter cartId) {
        this.cartId = cartId;
    }

    public LongFilter getReturnsId() {
        return returnsId;
    }

    public Optional<LongFilter> optionalReturnsId() {
        return Optional.ofNullable(returnsId);
    }

    public LongFilter returnsId() {
        if (returnsId == null) {
            setReturnsId(new LongFilter());
        }
        return returnsId;
    }

    public void setReturnsId(LongFilter returnsId) {
        this.returnsId = returnsId;
    }

    public LongFilter getAddedById() {
        return addedById;
    }

    public Optional<LongFilter> optionalAddedById() {
        return Optional.ofNullable(addedById);
    }

    public LongFilter addedById() {
        if (addedById == null) {
            setAddedById(new LongFilter());
        }
        return addedById;
    }

    public void setAddedById(LongFilter addedById) {
        this.addedById = addedById;
    }

    public LongFilter getZoneId() {
        return zoneId;
    }

    public Optional<LongFilter> optionalZoneId() {
        return Optional.ofNullable(zoneId);
    }

    public LongFilter zoneId() {
        if (zoneId == null) {
            setZoneId(new LongFilter());
        }
        return zoneId;
    }

    public void setZoneId(LongFilter zoneId) {
        this.zoneId = zoneId;
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
        final CustomerCriteria that = (CustomerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(waPhoneNumber, that.waPhoneNumber) &&
            Objects.equals(name, that.name) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(locationLat, that.locationLat) &&
            Objects.equals(locationLon, that.locationLon) &&
            Objects.equals(address, that.address) &&
            Objects.equals(distanceFromBusinessKm, that.distanceFromBusinessKm) &&
            Objects.equals(isPincodeValid, that.isPincodeValid) &&
            Objects.equals(role, that.role) &&
            Objects.equals(joinedAt, that.joinedAt) &&
            Objects.equals(lastInteractionAt, that.lastInteractionAt) &&
            Objects.equals(ordersId, that.ordersId) &&
            Objects.equals(cartId, that.cartId) &&
            Objects.equals(returnsId, that.returnsId) &&
            Objects.equals(addedById, that.addedById) &&
            Objects.equals(zoneId, that.zoneId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            waPhoneNumber,
            name,
            phoneNumber,
            locationLat,
            locationLon,
            address,
            distanceFromBusinessKm,
            isPincodeValid,
            role,
            joinedAt,
            lastInteractionAt,
            ordersId,
            cartId,
            returnsId,
            addedById,
            zoneId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalWaPhoneNumber().map(f -> "waPhoneNumber=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalPhoneNumber().map(f -> "phoneNumber=" + f + ", ").orElse("") +
            optionalLocationLat().map(f -> "locationLat=" + f + ", ").orElse("") +
            optionalLocationLon().map(f -> "locationLon=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalDistanceFromBusinessKm().map(f -> "distanceFromBusinessKm=" + f + ", ").orElse("") +
            optionalIsPincodeValid().map(f -> "isPincodeValid=" + f + ", ").orElse("") +
            optionalRole().map(f -> "role=" + f + ", ").orElse("") +
            optionalJoinedAt().map(f -> "joinedAt=" + f + ", ").orElse("") +
            optionalLastInteractionAt().map(f -> "lastInteractionAt=" + f + ", ").orElse("") +
            optionalOrdersId().map(f -> "ordersId=" + f + ", ").orElse("") +
            optionalCartId().map(f -> "cartId=" + f + ", ").orElse("") +
            optionalReturnsId().map(f -> "returnsId=" + f + ", ").orElse("") +
            optionalAddedById().map(f -> "addedById=" + f + ", ").orElse("") +
            optionalZoneId().map(f -> "zoneId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
