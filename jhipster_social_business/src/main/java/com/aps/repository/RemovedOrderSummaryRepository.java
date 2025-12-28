package com.aps.repository;

import com.aps.domain.RemovedOrderSummary;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RemovedOrderSummary entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RemovedOrderSummaryRepository extends JpaRepository<RemovedOrderSummary, Long> {}
