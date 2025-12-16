package com.aps.repository;

import com.aps.domain.FishProduct;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FishProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FishProductRepository extends JpaRepository<FishProduct, Long>, JpaSpecificationExecutor<FishProduct> {

    @EntityGraph(attributePaths = "images")
    java.util.List<FishProduct> findByIsAvailableTrue();
}
