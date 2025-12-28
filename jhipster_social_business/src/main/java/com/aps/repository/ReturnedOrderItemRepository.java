package com.aps.repository;

import com.aps.domain.ReturnedOrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReturnedOrderItem entity.
 */
@Repository
public interface ReturnedOrderItemRepository extends JpaRepository<ReturnedOrderItem, Long>, JpaSpecificationExecutor<ReturnedOrderItem> {
    default Optional<ReturnedOrderItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ReturnedOrderItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ReturnedOrderItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select returnedOrderItem from ReturnedOrderItem returnedOrderItem left join fetch returnedOrderItem.product",
        countQuery = "select count(returnedOrderItem) from ReturnedOrderItem returnedOrderItem"
    )
    Page<ReturnedOrderItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select returnedOrderItem from ReturnedOrderItem returnedOrderItem left join fetch returnedOrderItem.product")
    List<ReturnedOrderItem> findAllWithToOneRelationships();

    @Query(
        "select returnedOrderItem from ReturnedOrderItem returnedOrderItem left join fetch returnedOrderItem.product where returnedOrderItem.id =:id"
    )
    Optional<ReturnedOrderItem> findOneWithToOneRelationships(@Param("id") Long id);
}
