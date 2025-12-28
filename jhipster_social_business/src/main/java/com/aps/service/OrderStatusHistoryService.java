package com.aps.service;

import com.aps.domain.OrderStatusHistory;
import com.aps.repository.OrderStatusHistoryRepository;
import com.aps.service.dto.OrderStatusHistoryDTO;
import com.aps.service.mapper.OrderStatusHistoryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link OrderStatusHistory}.
 */
@Service
@Transactional
public class OrderStatusHistoryService {

    private final Logger log = LoggerFactory.getLogger(OrderStatusHistoryService.class);

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    private final OrderStatusHistoryMapper orderStatusHistoryMapper;

    public OrderStatusHistoryService(
        OrderStatusHistoryRepository orderStatusHistoryRepository,
        OrderStatusHistoryMapper orderStatusHistoryMapper
    ) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.orderStatusHistoryMapper = orderStatusHistoryMapper;
    }

    /**
     * Get all the orderStatusHistories by order id.
     *
     * @param orderId the id of the order.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryDTO> findAllByOrderId(Long orderId) {
        log.debug("Request to get OrderStatusHistory by orderId : {}", orderId);
        return orderStatusHistoryRepository
            .findAllByCustomerOrderId(orderId)
            .stream()
            .map(orderStatusHistoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Creates a new status history record.
     *
     * @param customerOrder the customer order
     */
    public void addEvent(com.aps.domain.CustomerOrder customerOrder) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setStatus(customerOrder.getStatus());
        history.setChangeTime(java.time.Instant.now());
        history.setCustomerOrder(customerOrder);
        orderStatusHistoryRepository.save(history);
    }
}
