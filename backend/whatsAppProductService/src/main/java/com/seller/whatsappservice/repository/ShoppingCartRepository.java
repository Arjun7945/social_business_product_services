package com.seller.whatsappservice.repository;

import com.seller.whatsappservice.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    /**
     * Find shopping cart by customer ID
     */
    Optional<ShoppingCart> findByCustomerId(Long customerId);

    /**
     * Delete shopping cart by customer ID
     */
    void deleteByCustomerId(Long customerId);
}
