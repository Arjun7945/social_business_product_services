package com.aps.repository;

import com.aps.domain.ButtonAction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ButtonAction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ButtonActionRepository
        extends JpaRepository<ButtonAction, Long>, JpaSpecificationExecutor<ButtonAction> {
    java.util.Optional<ButtonAction> findByWaMessageId(String waMessageId);
}
