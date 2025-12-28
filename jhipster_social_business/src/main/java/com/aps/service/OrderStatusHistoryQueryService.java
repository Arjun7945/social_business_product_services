package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.OrderStatusHistory;
import com.aps.repository.OrderStatusHistoryRepository;
import com.aps.service.criteria.OrderStatusHistoryCriteria;
import com.aps.service.dto.OrderStatusHistoryDTO;
import com.aps.service.mapper.OrderStatusHistoryMapper;
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
 * Service for executing complex queries for {@link OrderStatusHistory} entities in the database.
 * The main input is a {@link OrderStatusHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link OrderStatusHistoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderStatusHistoryQueryService extends QueryService<OrderStatusHistory> {

    private static final Logger LOG = LoggerFactory.getLogger(OrderStatusHistoryQueryService.class);

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    private final OrderStatusHistoryMapper orderStatusHistoryMapper;

    public OrderStatusHistoryQueryService(
        OrderStatusHistoryRepository orderStatusHistoryRepository,
        OrderStatusHistoryMapper orderStatusHistoryMapper
    ) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.orderStatusHistoryMapper = orderStatusHistoryMapper;
    }

    /**
     * Return a {@link Page} of {@link OrderStatusHistoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderStatusHistoryDTO> findByCriteria(OrderStatusHistoryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OrderStatusHistory> specification = createSpecification(criteria);
        return orderStatusHistoryRepository.findAll(specification, page).map(orderStatusHistoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderStatusHistoryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<OrderStatusHistory> specification = createSpecification(criteria);
        return orderStatusHistoryRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderStatusHistoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OrderStatusHistory> createSpecification(OrderStatusHistoryCriteria criteria) {
        Specification<OrderStatusHistory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OrderStatusHistory_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), OrderStatusHistory_.status));
            }
            if (criteria.getChangeTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getChangeTime(), OrderStatusHistory_.changeTime));
            }
            if (criteria.getCustomerOrderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCustomerOrderId(), root ->
                        root.join(OrderStatusHistory_.customerOrder, JoinType.LEFT).get(CustomerOrder_.id)
                    )
                );
            }
        }
        return specification;
    }
}
