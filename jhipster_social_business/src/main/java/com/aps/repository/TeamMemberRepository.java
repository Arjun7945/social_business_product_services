package com.aps.repository;

import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TeamMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long>, JpaSpecificationExecutor<TeamMember> {
    java.util.Optional<TeamMember> findByWaPhoneNumber(String waPhoneNumber);

    boolean existsByWaPhoneNumber(String waPhoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    java.util.Optional<TeamMember> findByName(String name);

    List<TeamMember> findAllByRole(UserRole role);
}
