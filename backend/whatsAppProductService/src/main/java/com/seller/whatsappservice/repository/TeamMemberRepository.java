package com.seller.whatsappservice.repository;

import com.seller.whatsappservice.model.TeamMember;
import com.seller.whatsappservice.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    /**
     * Find team member by WhatsApp phone number
     */
    Optional<TeamMember> findByWaPhoneNumber(String waPhoneNumber);

    List<TeamMember> findByRole(UserRole role);

    Optional<TeamMember> findByPhoneNumber(String phoneNumber);

    List<TeamMember> findByIsActiveTrue();

    /**
     * Find team member by WhatsApp phone number and role
     */
    Optional<TeamMember> findByWaPhoneNumberAndRole(String waPhoneNumber, UserRole role);

    /**
     * Find active team members by role
     */
    List<TeamMember> findByRoleAndIsActive(UserRole role, boolean isActive);
}
