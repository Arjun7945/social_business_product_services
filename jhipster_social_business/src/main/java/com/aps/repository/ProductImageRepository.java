package com.aps.repository;

import com.aps.domain.ProductImage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductImage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductImageRepository
        extends JpaRepository<ProductImage, Long>, JpaSpecificationExecutor<ProductImage> {
    long countByProductId(Long productId);
}
