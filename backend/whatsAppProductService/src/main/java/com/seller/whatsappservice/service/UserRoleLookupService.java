package com.seller.whatsappservice.service;

import com.seller.whatsappservice.dto.UserLookupResult;
import com.seller.whatsappservice.model.Customer;
import com.seller.whatsappservice.model.TeamMember;
import com.seller.whatsappservice.repository.CustomerRepository;
import com.seller.whatsappservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service to perform unified user role lookup across all user types
 * This is the single source of truth for determining user roles
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleLookupService {

    private final TeamMemberRepository teamMemberRepository;
    private final CustomerRepository customerRepository;

    /**
     * Lookup user by WhatsApp phone number and determine their role
     * 
     * Priority order:
     * 1. Check TeamMember table (Executives and Delivery Persons)
     * 2. Check Customer table
     * 3. Return null if not found (unknown number)
     * 
     * @param waPhoneNumber WhatsApp phone number (e.g., 919876543210)
     * @return UserLookupResult containing role and entity, or null if not found
     */
    public UserLookupResult lookupUserByWaPhoneNumber(String waPhoneNumber) {
        log.debug("Looking up user role for WhatsApp number: {}", waPhoneNumber);

        // First, check if this is a team member (Executive or Delivery Person)
        Optional<TeamMember> teamMemberOpt = teamMemberRepository.findByWaPhoneNumber(waPhoneNumber);
        if (teamMemberOpt.isPresent()) {
            TeamMember teamMember = teamMemberOpt.get();
            log.info("Found team member: {} with role: {}", teamMember.getName(), teamMember.getRole());
            return UserLookupResult.builder()
                    .role(teamMember.getRole())
                    .userEntity(teamMember)
                    .build();
        }

        // Second, check if this is an existing customer
        Optional<Customer> customerOpt = customerRepository.findByWaPhoneNumber(waPhoneNumber);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            log.info("Found existing customer: {} with flow stage: {}",
                    customer.getName() != null ? customer.getName() : "Unknown",
                    customer.getCurrentFlowStage());
            return UserLookupResult.builder()
                    .role(customer.getRole())
                    .userEntity(customer)
                    .build();
        }

        // Not found in any table - this is an unknown number
        log.info("Unknown number: {} - will be treated as new customer", waPhoneNumber);
        return null;
    }
}
