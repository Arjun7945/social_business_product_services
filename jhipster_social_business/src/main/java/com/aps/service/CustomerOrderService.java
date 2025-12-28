package com.aps.service;

import com.aps.domain.CustomerOrder;
import com.aps.domain.OrderStatusHistory;
import com.aps.repository.CustomerOrderRepository;
import com.aps.repository.OrderStatusHistoryRepository;
import com.aps.service.dto.CustomerOrderDTO;
import com.aps.service.mapper.CustomerOrderMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.CustomerOrder}.
 */
@Service
@Transactional
public class CustomerOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderService.class);

    private final CustomerOrderRepository customerOrderRepository;

    private final CustomerOrderMapper customerOrderMapper;

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    public CustomerOrderService(
        CustomerOrderRepository customerOrderRepository,
        CustomerOrderMapper customerOrderMapper,
        OrderStatusHistoryRepository orderStatusHistoryRepository
    ) {
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderMapper = customerOrderMapper;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
    }

    /**
     * Save a customerOrder.
     *
     * @param customerOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public CustomerOrderDTO save(CustomerOrderDTO customerOrderDTO) {
        LOG.debug("Request to save CustomerOrder : {}", customerOrderDTO);
        CustomerOrder customerOrder = customerOrderMapper.toEntity(customerOrderDTO);
        customerOrder = customerOrderRepository.save(customerOrder);

        // Create initial status history
        createStatusHistory(customerOrder);

        return customerOrderMapper.toDto(customerOrder);
    }

    /**
     * Update a customerOrder.
     *
     * @param customerOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public CustomerOrderDTO update(CustomerOrderDTO customerOrderDTO) {
        LOG.debug("Request to update CustomerOrder : {}", customerOrderDTO);

        // Fetch existing to check for status change
        // Note: Ideally we should use the existing entity, but for update(DTO) we often
        // overwrite.
        // We will check if the status is different from what's potentially in DB.
        // However, to keep it simple and correct, let's fetch the ID.
        boolean statusChanged = false;
        if (customerOrderDTO.getId() != null) {
            statusChanged = customerOrderRepository
                .findById(customerOrderDTO.getId())
                .map(existing -> !existing.getStatus().equals(customerOrderDTO.getStatus()))
                .orElse(true); // If not found (shouldn't happen in update), assume changed? Or let it proceed.
        }

        CustomerOrder customerOrder = customerOrderMapper.toEntity(customerOrderDTO);
        customerOrder = customerOrderRepository.save(customerOrder);

        if (statusChanged) {
            createStatusHistory(customerOrder);
        }

        return customerOrderMapper.toDto(customerOrder);
    }

    /**
     * Partially update a customerOrder.
     *
     * @param customerOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CustomerOrderDTO> partialUpdate(CustomerOrderDTO customerOrderDTO) {
        LOG.debug("Request to partially update CustomerOrder : {}", customerOrderDTO);

        return customerOrderRepository
            .findById(customerOrderDTO.getId())
            .map(existingCustomerOrder -> {
                var oldStatus = existingCustomerOrder.getStatus();
                customerOrderMapper.partialUpdate(existingCustomerOrder, customerOrderDTO);

                if (!existingCustomerOrder.getStatus().equals(oldStatus)) {
                    createStatusHistory(existingCustomerOrder);
                }

                return existingCustomerOrder;
            })
            .map(customerOrderRepository::save)
            .map(customerOrderMapper::toDto);
    }

    private void createStatusHistory(CustomerOrder customerOrder) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setStatus(customerOrder.getStatus());
        history.setChangeTime(Instant.now());
        history.setCustomerOrder(customerOrder);
        orderStatusHistoryRepository.save(history);
    }

    /**
     * Get all the customerOrders with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CustomerOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return customerOrderRepository.findAllWithEagerRelationships(pageable).map(customerOrderMapper::toDto);
    }

    /**
     * Get one customerOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CustomerOrderDTO> findOne(Long id) {
        LOG.debug("Request to get CustomerOrder : {}", id);
        return customerOrderRepository.findOneWithEagerRelationships(id).map(customerOrderMapper::toDto);
    }

    /**
     * Delete the customerOrder by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CustomerOrder : {}", id);
        customerOrderRepository.deleteById(id);
    }
}
