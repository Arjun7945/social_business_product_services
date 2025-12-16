package com.aps.service;

import com.aps.domain.Customer;
import com.aps.domain.TeamMember;
import com.aps.repository.CustomerRepository;
import com.aps.repository.TeamMemberRepository;
import com.aps.service.dto.UserLookupResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service to perform unified user role lookup across all user types.
 */
@Service
@Transactional(readOnly = true)
public class UserRoleLookupService {

    private final Logger log = LoggerFactory.getLogger(UserRoleLookupService.class);

    private final TeamMemberRepository teamMemberRepository;
    private final CustomerRepository customerRepository;

    public UserRoleLookupService(TeamMemberRepository teamMemberRepository, CustomerRepository customerRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Lookup user by WhatsApp phone number and determine their role.
     */
    public UserLookupResult lookupUserByWaPhoneNumber(String waPhoneNumber) {
        log.debug("Looking up user role for WhatsApp number: {}", waPhoneNumber);

        // 1. Check TeamMember table
        Optional<TeamMember> teamMemberOpt = teamMemberRepository.findByWaPhoneNumber(waPhoneNumber);
        if (teamMemberOpt.isPresent()) {
            TeamMember teamMember = teamMemberOpt.get();
            log.info("Found team member: {} with role: {}", teamMember.getName(), teamMember.getRole());
            return UserLookupResult.builder()
                    .role(teamMember.getRole())
                    .userEntity(teamMember)
                    .build();
        }

        // 2. Check Customer table
        Optional<Customer> customerOpt = customerRepository.findByWaPhoneNumber(waPhoneNumber);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            log.info("Found existing customer: {}", customer.getName());
            return UserLookupResult.builder()
                    .role(customer.getRole())
                    .userEntity(customer)
                    .build();
        }

        // 3. Not found
        log.info("Unknown number: {} - will be treated as new customer", waPhoneNumber);
        return null;
    }
}
