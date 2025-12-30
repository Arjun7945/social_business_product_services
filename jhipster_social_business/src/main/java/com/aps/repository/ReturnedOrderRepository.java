package com.aps.repository;

import com.aps.domain.ReturnedOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReturnedOrder entity.
 */
@Repository
public interface ReturnedOrderRepository
        extends JpaRepository<ReturnedOrder, Long>, JpaSpecificationExecutor<ReturnedOrder> {
    default Optional<ReturnedOrder> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ReturnedOrder> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ReturnedOrder> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select returnedOrder from ReturnedOrder returnedOrder left join fetch returnedOrder.customer", countQuery = "select count(returnedOrder) from ReturnedOrder returnedOrder")
    Page<ReturnedOrder> findAllWithToOneRelationships(Pageable pageable);

    @Query("select returnedOrder from ReturnedOrder returnedOrder left join fetch returnedOrder.customer")
    List<ReturnedOrder> findAllWithToOneRelationships();

    @Query("select returnedOrder from ReturnedOrder returnedOrder left join fetch returnedOrder.customer where returnedOrder.id =:id")
    Optional<ReturnedOrder> findOneWithToOneRelationships(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ReturnedOrder o SET o.customer = null, o.removedCustomerId = :removedId WHERE o.customer.id = :custId")
    void unlinkCustomer(@Param("custId") Long custId, @Param("removedId") Long removedId);

    List<ReturnedOrder> findAllByRemovedCustomerId(Long removedCustomerId);
}
