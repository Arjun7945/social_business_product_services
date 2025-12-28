package com.aps.repository;

import com.aps.domain.CustomerOrder;
import com.aps.domain.enumeration.OrderStatus;
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

    @Query(value = "select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer left join fetch customerOrder.deliveryPerson", countQuery = "select count(customerOrder) from CustomerOrder customerOrder")
    Page<CustomerOrder> findAllWithToOneRelationships(Pageable pageable);

    @Query("select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer left join fetch customerOrder.deliveryPerson")
    List<CustomerOrder> findAllWithToOneRelationships();

    @Query("select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer left join fetch customerOrder.deliveryPerson where customerOrder.id =:id")
    Optional<CustomerOrder> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("SELECT COUNT(o), SUM(o.totalAmount) FROM CustomerOrder o WHERE o.customer.id = :customerId AND o.status = :status")
    List<Object[]> getCustomerStats(@Param("customerId") Long customerId, @Param("status") OrderStatus status);

    @Query("SELECT COUNT(o), SUM(o.totalAmount), MIN(o.orderTime), MAX(o.orderTime) FROM CustomerOrder o WHERE o.deliveryPerson.id = :dpId AND o.status IN :statuses")
    List<Object[]> findStatsByDeliveryPersonIdAndStatus(@Param("dpId") Long dpId,
            @Param("statuses") List<OrderStatus> statuses);

    @Modifying
    @Query("UPDATE CustomerOrder o SET o.customer = null, o.removedCustomerId = :removedId WHERE o.customer.id = :custId")
    void unlinkCustomer(@Param("custId") Long custId, @Param("removedId") Long removedId);

    @Modifying
    @Query("UPDATE CustomerOrder o SET o.deliveryPerson = null, o.removedDeliveryPersonId = :removedId WHERE o.deliveryPerson.id = :dpId")
    void unlinkDeliveryPerson(@Param("dpId") Long dpId, @Param("removedId") Long removedId);

    List<CustomerOrder> findAllByRemovedCustomerId(Long removedCustomerId);

    List<CustomerOrder> findAllByRemovedDeliveryPersonId(Long removedDeliveryPersonId);

    List<CustomerOrder> findAllByOrderTimeBetween(java.time.Instant start, java.time.Instant end);

    List<CustomerOrder> findAllByStatus(OrderStatus status);
}
