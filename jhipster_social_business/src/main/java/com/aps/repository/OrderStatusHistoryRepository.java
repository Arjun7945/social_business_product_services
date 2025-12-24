package com.aps.repository;

import com.aps.domain.OrderStatusHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA repository for the OrderStatusHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    List<OrderStatusHistory> findAllByCustomerOrderId(Long customerOrderId);
}
