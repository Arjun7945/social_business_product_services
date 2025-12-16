package com.aps.repository;

import com.aps.domain.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CartItem entity.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long>, JpaSpecificationExecutor<CartItem> {

    java.util.Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    default Optional<CartItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CartItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CartItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select cartItem from CartItem cartItem left join fetch cartItem.product", countQuery = "select count(cartItem) from CartItem cartItem")
    Page<CartItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select cartItem from CartItem cartItem left join fetch cartItem.product")
    List<CartItem> findAllWithToOneRelationships();

    @Query("select cartItem from CartItem cartItem left join fetch cartItem.product where cartItem.id =:id")
    Optional<CartItem> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select cartItem from CartItem cartItem left join fetch cartItem.product where cartItem.cart.id = :cartId")
    List<CartItem> findByCartId(@Param("cartId") Long cartId);

    @Modifying
    @Query("delete from CartItem c where c.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);
}
