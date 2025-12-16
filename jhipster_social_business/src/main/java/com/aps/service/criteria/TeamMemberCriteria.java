package com.aps.service.criteria;

import com.aps.domain.enumeration.UserRole;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.aps.domain.TeamMember} entity. This class is used
 * in {@link com.aps.web.rest.TeamMemberResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /team-members?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamMemberCriteria implements Serializable, Criteria {

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

    private StringFilter name;

    private StringFilter waPhoneNumber;

    private StringFilter phoneNumber;

    private UserRoleFilter role;

    private BooleanFilter isActive;

    private Boolean distinct;

    public TeamMemberCriteria() {}

    public TeamMemberCriteria(TeamMemberCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.waPhoneNumber = other.optionalWaPhoneNumber().map(StringFilter::copy).orElse(null);
        this.phoneNumber = other.optionalPhoneNumber().map(StringFilter::copy).orElse(null);
        this.role = other.optionalRole().map(UserRoleFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TeamMemberCriteria copy() {
        return new TeamMemberCriteria(this);
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
        final TeamMemberCriteria that = (TeamMemberCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(waPhoneNumber, that.waPhoneNumber) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(role, that.role) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, waPhoneNumber, phoneNumber, role, isActive, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamMemberCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalWaPhoneNumber().map(f -> "waPhoneNumber=" + f + ", ").orElse("") +
            optionalPhoneNumber().map(f -> "phoneNumber=" + f + ", ").orElse("") +
            optionalRole().map(f -> "role=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
