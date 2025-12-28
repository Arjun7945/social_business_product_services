package com.aps.repository;

import com.aps.domain.ButtonAction;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ButtonAction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ButtonActionRepository extends JpaRepository<ButtonAction, Long> {
    Optional<ButtonAction> findByWaMessageId(String waMessageId);

    boolean existsByWaMessageId(String waMessageId);
}
