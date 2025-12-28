package com.aps.repository;

import com.aps.domain.RemovedUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RemovedUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RemovedUserRepository extends JpaRepository<RemovedUser, Long> {}
