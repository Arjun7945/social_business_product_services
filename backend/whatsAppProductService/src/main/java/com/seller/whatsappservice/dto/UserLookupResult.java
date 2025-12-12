package com.seller.whatsappservice.dto;

import com.seller.whatsappservice.model.Customer;
import com.seller.whatsappservice.model.TeamMember;
import com.seller.whatsappservice.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to encapsulate the result of user role lookup
 * Contains the user's role and the actual entity (TeamMember or Customer)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLookupResult {

    private UserRole role;
    private Object userEntity; // Can be TeamMember or Customer

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
