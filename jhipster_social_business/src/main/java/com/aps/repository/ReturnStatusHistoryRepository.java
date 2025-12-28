package com.aps.repository;

import com.aps.domain.ReturnStatusHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReturnStatusHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReturnStatusHistoryRepository
    extends JpaRepository<ReturnStatusHistory, Long>, JpaSpecificationExecutor<ReturnStatusHistory> {}
