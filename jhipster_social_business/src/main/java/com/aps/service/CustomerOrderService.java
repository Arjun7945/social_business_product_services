package com.aps.service;

import com.aps.domain.CustomerOrder;
import com.aps.repository.CustomerOrderRepository;
import com.aps.service.dto.CustomerOrderDTO;
import com.aps.service.mapper.CustomerOrderMapper;
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

    public CustomerOrderService(CustomerOrderRepository customerOrderRepository,
            CustomerOrderMapper customerOrderMapper) {
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderMapper = customerOrderMapper;
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
        CustomerOrder customerOrder = customerOrderMapper.toEntity(customerOrderDTO);
        customerOrder = customerOrderRepository.save(customerOrder);
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
                    customerOrderMapper.partialUpdate(existingCustomerOrder, customerOrderDTO);

                    return existingCustomerOrder;
                })
                .map(customerOrderRepository::save)
                .map(customerOrderMapper::toDto);
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

    /**
     * Get all customerOrders by customer phone number.
     *
     * @param phone the phone number of the customer.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public java.util.List<CustomerOrderDTO> findAllByCustomerWaPhoneNumber(String phone) {
        LOG.debug("Request to get CustomerOrders by phone : {}", phone);
        return customerOrderRepository.findAllByCustomerWaPhoneNumber(phone)
                .stream()
                .map(customerOrderMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }
}
