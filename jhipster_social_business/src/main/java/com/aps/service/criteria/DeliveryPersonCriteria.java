package com.aps.service.criteria;

import com.aps.domain.enumeration.DeliveryStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.DeliveryPerson} entity. This class is used
 * in {@link com.aps.web.rest.DeliveryPersonResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /delivery-people?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryPersonCriteria implements Serializable, Criteria {

    /**
     * Class for filtering DeliveryStatus
     */
    public static class DeliveryStatusFilter extends Filter<DeliveryStatus> {

        public DeliveryStatusFilter() {}

        public DeliveryStatusFilter(DeliveryStatusFilter filter) {
            super(filter);
        }

        @Override
        public DeliveryStatusFilter copy() {
            return new DeliveryStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter waPhoneNumber;

    private StringFilter phoneNumber;

    private DeliveryStatusFilter status;

    private InstantFilter joinedAt;

    private BooleanFilter isActive;

    private LongFilter ordersId;

    private LongFilter addedById;

    private LongFilter zoneId;

    private Boolean distinct;

    public DeliveryPersonCriteria() {}

    public DeliveryPersonCriteria(DeliveryPersonCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.waPhoneNumber = other.optionalWaPhoneNumber().map(StringFilter::copy).orElse(null);
        this.phoneNumber = other.optionalPhoneNumber().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(DeliveryStatusFilter::copy).orElse(null);
        this.joinedAt = other.optionalJoinedAt().map(InstantFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.ordersId = other.optionalOrdersId().map(LongFilter::copy).orElse(null);
        this.addedById = other.optionalAddedById().map(LongFilter::copy).orElse(null);
        this.zoneId = other.optionalZoneId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DeliveryPersonCriteria copy() {
        return new DeliveryPersonCriteria(this);
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

    public DeliveryStatusFilter getStatus() {
        return status;
    }

    public Optional<DeliveryStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public DeliveryStatusFilter status() {
        if (status == null) {
            setStatus(new DeliveryStatusFilter());
        }
        return status;
    }

    public void setStatus(DeliveryStatusFilter status) {
        this.status = status;
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

    public BooleanFilter getIsActive() {
        return isActive;
    }

    public Optional<BooleanFilter> optionalIsActive() {
        return Optional.ofNullable(isActive);
    }

    public BooleanFilter isActive() {
        if (isActive == null) {
            setIsActive(new BooleanFilter());
        }
        return isActive;
    }

    public void setIsActive(BooleanFilter isActive) {
        this.isActive = isActive;
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
        final DeliveryPersonCriteria that = (DeliveryPersonCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(waPhoneNumber, that.waPhoneNumber) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(status, that.status) &&
            Objects.equals(joinedAt, that.joinedAt) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(ordersId, that.ordersId) &&
            Objects.equals(addedById, that.addedById) &&
            Objects.equals(zoneId, that.zoneId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, waPhoneNumber, phoneNumber, status, joinedAt, isActive, ordersId, addedById, zoneId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryPersonCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalWaPhoneNumber().map(f -> "waPhoneNumber=" + f + ", ").orElse("") +
            optionalPhoneNumber().map(f -> "phoneNumber=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalJoinedAt().map(f -> "joinedAt=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalOrdersId().map(f -> "ordersId=" + f + ", ").orElse("") +
            optionalAddedById().map(f -> "addedById=" + f + ", ").orElse("") +
            optionalZoneId().map(f -> "zoneId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
