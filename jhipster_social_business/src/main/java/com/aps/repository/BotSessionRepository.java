package com.aps.repository;

import com.aps.domain.BotSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BotSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BotSessionRepository extends JpaRepository<BotSession, Long>, JpaSpecificationExecutor<BotSession> {}
