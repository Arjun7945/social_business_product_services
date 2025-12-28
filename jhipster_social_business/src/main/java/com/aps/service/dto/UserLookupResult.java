package com.aps.service.dto;

import com.aps.domain.Customer;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.UserRole;

/**
 * DTO to encapsulate the result of user role lookup
 * Contains the user's role and the actual entity (TeamMember or Customer)
 */
public class UserLookupResult {

    private UserRole role;
    private Object userEntity; // Can be TeamMember or Customer

    public UserLookupResult() {}

    public UserLookupResult(UserRole role, Object userEntity) {
        this.role = role;
        this.userEntity = userEntity;
    }

    public static UserLookupResult builder() {
        return new UserLookupResult();
    }

    public UserLookupResult role(UserRole role) {
        this.role = role;
        return this;
    }

    public UserLookupResult userEntity(Object userEntity) {
        this.userEntity = userEntity;
        return this;
    }

    public UserLookupResult build() {
        return this;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Object getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(Object userEntity) {
        this.userEntity = userEntity;
    }

    /**
     * Check if user is a team member (Executive or Delivery Person)
     */
    public boolean isTeamMember() {
        return role == UserRole.EXECUTIVE || role == UserRole.DELIVERY_PERSON;
    }

    /**
     * Check if user is a customer
     */
    public boolean isCustomer() {
        return role == UserRole.CUSTOMER;
    }

    /**
     * Get the entity as TeamMember
     *
     * @throws ClassCastException if entity is not a TeamMember
     */
    public TeamMember asTeamMember() {
        return (TeamMember) userEntity;
    }

    /**
     * Get the entity as Customer
     *
     * @throws ClassCastException if entity is not a Customer
     */
    public Customer asCustomer() {
        return (Customer) userEntity;
    }
}
