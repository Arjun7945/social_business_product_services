package com.seller.whatsappservice.repository;

import com.seller.whatsappservice.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    void deleteByProductId(Long productId);

    long countByProductId(Long productId);

    Optional<ProductImage> findFirstByProductIdOrderByDisplayOrderAsc(Long productId);
}
