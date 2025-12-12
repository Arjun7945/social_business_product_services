package com.seller.whatsappservice.repository;

import com.seller.whatsappservice.model.FishProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FishProductRepository extends JpaRepository<FishProduct, Long> {
    List<FishProduct> findByIsAvailableTrue();

    @Query("SELECT DISTINCT p FROM FishProduct p LEFT JOIN FETCH p.images WHERE p.isAvailable = true")
    List<FishProduct> findByIsAvailableTrueWithImages();
}
