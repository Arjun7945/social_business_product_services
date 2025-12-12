package com.seller.whatsappservice.repository;

import com.seller.whatsappservice.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find cart item by cart ID and fish product ID
     */
    Optional<CartItem> findByCartIdAndFishProductId(Long cartId, Long fishProductId);

    /**
     * Delete cart item by cart ID and fish product ID
     */
    void deleteByCartIdAndFishProductId(Long cartId, Long fishProductId);
}
