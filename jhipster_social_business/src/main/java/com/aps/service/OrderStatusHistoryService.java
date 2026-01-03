package com.aps.service;

import com.aps.domain.OrderStatusHistory;
import com.aps.repository.OrderStatusHistoryRepository;
import com.aps.repository.CustomerOrderRepository;
import com.aps.service.dto.OrderStatusHistoryDTO;
import com.aps.service.mapper.OrderStatusHistoryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.aps.domain.OrderStatusHistory}.
 */
@Service
@Transactional
public class OrderStatusHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderStatusHistoryService.class);

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    private final CustomerOrderRepository customerOrderRepository;

    private final OrderStatusHistoryMapper orderStatusHistoryMapper;

    public OrderStatusHistoryService(
            OrderStatusHistoryRepository orderStatusHistoryRepository,
            CustomerOrderRepository customerOrderRepository,
            OrderStatusHistoryMapper orderStatusHistoryMapper) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.orderStatusHistoryMapper = orderStatusHistoryMapper;
    }

    /**
     * Save a orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderStatusHistoryDTO save(OrderStatusHistoryDTO orderStatusHistoryDTO) {
        LOG.debug("Request to save OrderStatusHistory : {}", orderStatusHistoryDTO);
        OrderStatusHistory orderStatusHistory = orderStatusHistoryMapper.toEntity(orderStatusHistoryDTO);
        orderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory);
        return orderStatusHistoryMapper.toDto(orderStatusHistory);
    }

    /**
     * Update a orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderStatusHistoryDTO update(OrderStatusHistoryDTO orderStatusHistoryDTO) {
        LOG.debug("Request to update OrderStatusHistory : {}", orderStatusHistoryDTO);
        OrderStatusHistory orderStatusHistory = orderStatusHistoryMapper.toEntity(orderStatusHistoryDTO);
        orderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory);
        return orderStatusHistoryMapper.toDto(orderStatusHistory);
    }

    /**
     * Partially update a orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrderStatusHistoryDTO> partialUpdate(OrderStatusHistoryDTO orderStatusHistoryDTO) {
        LOG.debug("Request to partially update OrderStatusHistory : {}", orderStatusHistoryDTO);

        return orderStatusHistoryRepository
                .findById(orderStatusHistoryDTO.getId())
                .map(existingOrderStatusHistory -> {
                    orderStatusHistoryMapper.partialUpdate(existingOrderStatusHistory, orderStatusHistoryDTO);

                    return existingOrderStatusHistory;
                })
                .map(orderStatusHistoryRepository::save)
                .map(orderStatusHistoryMapper::toDto);
    }

    /**
     * Get all the orderStatusHistories where CustomerOrder is {@code null}.
     * 
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryDTO> findAllWhereCustomerOrderIsNull() {
        LOG.debug("Request to get all orderStatusHistories where CustomerOrder is null");
        return StreamSupport.stream(orderStatusHistoryRepository.findAll().spliterator(), false)
                .filter(orderStatusHistory -> orderStatusHistory.getCustomerOrder() == null)
                .map(orderStatusHistoryMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one orderStatusHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderStatusHistoryDTO> findOne(Long id) {
        LOG.debug("Request to get OrderStatusHistory : {}", id);
        return orderStatusHistoryRepository.findById(id).map(orderStatusHistoryMapper::toDto);
    }

    /**
     * Delete the orderStatusHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete OrderStatusHistory : {}", id);
        orderStatusHistoryRepository.deleteById(id);
    }

    /**
     * Add a status change event.
     *
     * @param order the order to record status for.
     */
    public void addEvent(com.aps.domain.CustomerOrder order) {
        OrderStatusHistory history = order.getHistory();
        if (history == null) {
            history = new OrderStatusHistory();
            history.setCustomerOrder(order);
        }
        history.setStatus(order.getStatus());
        history.setChangeTime(java.time.Instant.now());

        // Columnar Logic for specific stages

        // Stage 2: On Way (Taken by Delivery Person)
        if (order.getStatus() == com.aps.domain.enumeration.OrderStatus.DELIVERY_ONWAY) {
            if (history.getOnWayTime() == null) {
                history.setOnWayTime(java.time.Instant.now());
            }
        }

        // Stage 3: Payment Pending Logic
        // Triggered when Payment Method is selected (changed from default NOT_SELECTED)
        if (order.getPaymentMethod() != null && !order.getPaymentMethod().equals("NOT_SELECTED")) {
            if (history.getPaymentPendingTime() == null) {
                history.setPaymentPendingTime(java.time.Instant.now());
            }
        }

        // Stage 5: Delivered Logic handled via confirmedAt in Order, but we can also
        // sync if needed.
        // For strictness, we leave Delievered Time to confirmedAt as per plan.

        orderStatusHistoryRepository.save(history);

        // Ensure the order's FK to history is persisted (As CustomerOrder owns the
        // relationship)
        customerOrderRepository.save(order);
    }
}
