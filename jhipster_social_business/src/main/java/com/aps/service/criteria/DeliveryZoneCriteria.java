package com.aps.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.DeliveryZone} entity. This class is used
 * in {@link com.aps.web.rest.DeliveryZoneResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /delivery-zones?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryZoneCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter zoneName;

    private StringFilter pincode;

    private LongFilter customersId;

    private LongFilter deliveryPersonsId;

    private Boolean distinct;

    public DeliveryZoneCriteria() {}

    public DeliveryZoneCriteria(DeliveryZoneCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.zoneName = other.optionalZoneName().map(StringFilter::copy).orElse(null);
        this.pincode = other.optionalPincode().map(StringFilter::copy).orElse(null);
        this.customersId = other.optionalCustomersId().map(LongFilter::copy).orElse(null);
        this.deliveryPersonsId = other.optionalDeliveryPersonsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DeliveryZoneCriteria copy() {
        return new DeliveryZoneCriteria(this);
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

    public StringFilter getZoneName() {
        return zoneName;
    }

    public Optional<StringFilter> optionalZoneName() {
        return Optional.ofNullable(zoneName);
    }

    public StringFilter zoneName() {
        if (zoneName == null) {
            setZoneName(new StringFilter());
        }
        return zoneName;
    }

    public void setZoneName(StringFilter zoneName) {
        this.zoneName = zoneName;
    }

    public StringFilter getPincode() {
        return pincode;
    }

    public Optional<StringFilter> optionalPincode() {
        return Optional.ofNullable(pincode);
    }

    public StringFilter pincode() {
        if (pincode == null) {
            setPincode(new StringFilter());
        }
        return pincode;
    }

    public void setPincode(StringFilter pincode) {
        this.pincode = pincode;
    }

    public LongFilter getCustomersId() {
        return customersId;
    }

    public Optional<LongFilter> optionalCustomersId() {
        return Optional.ofNullable(customersId);
    }

    public LongFilter customersId() {
        if (customersId == null) {
            setCustomersId(new LongFilter());
        }
        return customersId;
    }

    public void setCustomersId(LongFilter customersId) {
        this.customersId = customersId;
    }

    public LongFilter getDeliveryPersonsId() {
        return deliveryPersonsId;
    }

    public Optional<LongFilter> optionalDeliveryPersonsId() {
        return Optional.ofNullable(deliveryPersonsId);
    }

    public LongFilter deliveryPersonsId() {
        if (deliveryPersonsId == null) {
            setDeliveryPersonsId(new LongFilter());
        }
        return deliveryPersonsId;
    }

    public void setDeliveryPersonsId(LongFilter deliveryPersonsId) {
        this.deliveryPersonsId = deliveryPersonsId;
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
        final DeliveryZoneCriteria that = (DeliveryZoneCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(zoneName, that.zoneName) &&
            Objects.equals(pincode, that.pincode) &&
            Objects.equals(customersId, that.customersId) &&
            Objects.equals(deliveryPersonsId, that.deliveryPersonsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, zoneName, pincode, customersId, deliveryPersonsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryZoneCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalZoneName().map(f -> "zoneName=" + f + ", ").orElse("") +
            optionalPincode().map(f -> "pincode=" + f + ", ").orElse("") +
            optionalCustomersId().map(f -> "customersId=" + f + ", ").orElse("") +
            optionalDeliveryPersonsId().map(f -> "deliveryPersonsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
