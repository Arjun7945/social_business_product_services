package com.aps.repository;

import com.aps.domain.ProductImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductImage entity.
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    default Optional<ProductImage> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProductImage> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProductImage> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select productImage from ProductImage productImage left join fetch productImage.product", countQuery = "select count(productImage) from ProductImage productImage")
    Page<ProductImage> findAllWithToOneRelationships(Pageable pageable);

    @Query("select productImage from ProductImage productImage left join fetch productImage.product")
    List<ProductImage> findAllWithToOneRelationships();

    @Query("select productImage from ProductImage productImage left join fetch productImage.product where productImage.id =:id")
    Optional<ProductImage> findOneWithToOneRelationships(@Param("id") Long id);

    long countByProductId(Long productId);

    Optional<ProductImage> findByImageUrl(String imageUrl);
}
