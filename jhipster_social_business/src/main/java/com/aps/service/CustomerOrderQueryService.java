package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.CustomerOrder;
import com.aps.repository.CustomerOrderRepository;
import com.aps.service.criteria.CustomerOrderCriteria;
import com.aps.service.dto.CustomerOrderDTO;
import com.aps.service.mapper.CustomerOrderMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link CustomerOrder} entities in
 * the database.
 * The main input is a {@link CustomerOrderCriteria} which gets converted to
 * {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CustomerOrderDTO} which fulfills the
 * criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomerOrderQueryService extends QueryService<CustomerOrder> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderQueryService.class);

    private final CustomerOrderRepository customerOrderRepository;

    private final CustomerOrderMapper customerOrderMapper;

    public CustomerOrderQueryService(CustomerOrderRepository customerOrderRepository,
            CustomerOrderMapper customerOrderMapper) {
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderMapper = customerOrderMapper;
    }

    /**
     * Return a {@link Page} of {@link CustomerOrderDTO} which matches the criteria
     * from the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerOrderDTO> findByCriteria(CustomerOrderCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CustomerOrder> specification = createSpecification(criteria);
        return customerOrderRepository.findAll(specification, page).map(customerOrderMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CustomerOrderCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CustomerOrder> specification = createSpecification(criteria);
        return customerOrderRepository.count(specification);
    }

    /**
     * Function to convert {@link CustomerOrderCriteria} to a {@link Specification}
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CustomerOrder> createSpecification(CustomerOrderCriteria criteria) {
        Specification<CustomerOrder> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CustomerOrder_.id));
            }
            if (criteria.getOrderTime() != null) {
                specification = specification
                        .and(buildRangeSpecification(criteria.getOrderTime(), CustomerOrder_.orderTime));
            }
            if (criteria.getTotalAmount() != null) {
                specification = specification
                        .and(buildRangeSpecification(criteria.getTotalAmount(), CustomerOrder_.totalAmount));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), CustomerOrder_.status));
            }
            if (criteria.getPaymentMethod() != null) {
                specification = specification
                        .and(buildSpecification(criteria.getPaymentMethod(), CustomerOrder_.paymentMethod));
            }
            if (criteria.getConfirmedAt() != null) {
                specification = specification
                        .and(buildRangeSpecification(criteria.getConfirmedAt(), CustomerOrder_.confirmedAt));
            }
            if (criteria.getRemovedCustomerId() != null) {
                specification = specification.and(
                        buildRangeSpecification(criteria.getRemovedCustomerId(), CustomerOrder_.removedCustomerId));
            }
            if (criteria.getRemovedDeliveryPersonId() != null) {
                specification = specification.and(
                        buildRangeSpecification(criteria.getRemovedDeliveryPersonId(),
                                CustomerOrder_.removedDeliveryPersonId));
            }
            if (criteria.getTransactionId() != null) {
                specification = specification
                        .and(buildStringSpecification(criteria.getTransactionId(), CustomerOrder_.transactionId));
            }
            if (criteria.getHistoryId() != null) {
                specification = specification.and(
                        buildSpecification(criteria.getHistoryId(),
                                root -> root.join(CustomerOrder_.history, JoinType.LEFT).get(OrderStatusHistory_.id)));
            }
            if (criteria.getItemsId() != null) {
                specification = specification.and(
                        buildSpecification(criteria.getItemsId(),
                                root -> root.join(CustomerOrder_.items, JoinType.LEFT).get(OrderItem_.id)));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(
                        buildSpecification(criteria.getCustomerId(),
                                root -> root.join(CustomerOrder_.customer, JoinType.LEFT).get(Customer_.id)));
            }
            if (criteria.getDeliveryPersonId() != null) {
                specification = specification.and(
                        buildSpecification(criteria.getDeliveryPersonId(), root -> root
                                .join(CustomerOrder_.deliveryPerson, JoinType.LEFT).get(DeliveryPerson_.id)));
            }
        }
        return specification;
    }
}
