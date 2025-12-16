package com.aps.repository;

import com.aps.domain.CustomerOrder;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CustomerOrder entity.
 */
@Repository
public interface CustomerOrderRepository
        extends JpaRepository<CustomerOrder, Long>, JpaSpecificationExecutor<CustomerOrder> {
    default Optional<CustomerOrder> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CustomerOrder> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CustomerOrder> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.deliveryPerson left join fetch customerOrder.customer", countQuery = "select count(customerOrder) from CustomerOrder customerOrder")
    Page<CustomerOrder> findAllWithToOneRelationships(Pageable pageable);

    @Query("select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.deliveryPerson left join fetch customerOrder.customer")
    List<CustomerOrder> findAllWithToOneRelationships();

    @Query("select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.deliveryPerson left join fetch customerOrder.customer where customerOrder.id =:id")
    Optional<CustomerOrder> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select o from CustomerOrder o left join fetch o.customer where o.orderTime between :start and :end")
    List<CustomerOrder> findAllByOrderTimeBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("select o from CustomerOrder o left join fetch o.customer where o.status = :status")
    List<CustomerOrder> findAllByStatus(@Param("status") com.aps.domain.enumeration.OrderStatus status);
}
