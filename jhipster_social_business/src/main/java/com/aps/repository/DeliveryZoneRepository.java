package com.aps.repository;

import com.aps.domain.DeliveryZone;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DeliveryZone entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeliveryZoneRepository extends JpaRepository<DeliveryZone, Long>, JpaSpecificationExecutor<DeliveryZone> {}
