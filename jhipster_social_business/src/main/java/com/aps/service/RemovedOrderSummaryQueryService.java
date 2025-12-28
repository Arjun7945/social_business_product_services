package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.RemovedOrderSummary;
import com.aps.repository.RemovedOrderSummaryRepository;
import com.aps.service.criteria.RemovedOrderSummaryCriteria;
import com.aps.service.dto.RemovedOrderSummaryDTO;
import com.aps.service.mapper.RemovedOrderSummaryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link RemovedOrderSummary} entities in the database.
 * The main input is a {@link RemovedOrderSummaryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RemovedOrderSummaryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RemovedOrderSummaryQueryService extends QueryService<RemovedOrderSummary> {

    private static final Logger LOG = LoggerFactory.getLogger(RemovedOrderSummaryQueryService.class);

    private final RemovedOrderSummaryRepository removedOrderSummaryRepository;

    private final RemovedOrderSummaryMapper removedOrderSummaryMapper;

    public RemovedOrderSummaryQueryService(
        RemovedOrderSummaryRepository removedOrderSummaryRepository,
        RemovedOrderSummaryMapper removedOrderSummaryMapper
    ) {
        this.removedOrderSummaryRepository = removedOrderSummaryRepository;
        this.removedOrderSummaryMapper = removedOrderSummaryMapper;
    }

    /**
     * Return a {@link Page} of {@link RemovedOrderSummaryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RemovedOrderSummaryDTO> findByCriteria(RemovedOrderSummaryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RemovedOrderSummary> specification = createSpecification(criteria);
        return removedOrderSummaryRepository.findAll(specification, page).map(removedOrderSummaryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RemovedOrderSummaryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<RemovedOrderSummary> specification = createSpecification(criteria);
        return removedOrderSummaryRepository.count(specification);
    }

    /**
     * Function to convert {@link RemovedOrderSummaryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RemovedOrderSummary> createSpecification(RemovedOrderSummaryCriteria criteria) {
        Specification<RemovedOrderSummary> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), RemovedOrderSummary_.id));
            }
            if (criteria.getUserOriginalId() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getUserOriginalId(), RemovedOrderSummary_.userOriginalId)
                );
            }
            if (criteria.getUserName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserName(), RemovedOrderSummary_.userName));
            }
            if (criteria.getUserRole() != null) {
                specification = specification.and(buildSpecification(criteria.getUserRole(), RemovedOrderSummary_.userRole));
            }
            if (criteria.getTotalOrders() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalOrders(), RemovedOrderSummary_.totalOrders));
            }
            if (criteria.getTotalAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalAmount(), RemovedOrderSummary_.totalAmount));
            }
            if (criteria.getFirstInteractionAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getFirstInteractionAt(), RemovedOrderSummary_.firstInteractionAt)
                );
            }
            if (criteria.getLastInteractionAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getLastInteractionAt(), RemovedOrderSummary_.lastInteractionAt)
                );
            }
            if (criteria.getRemovedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRemovedAt(), RemovedOrderSummary_.removedAt));
            }
        }
        return specification;
    }
}
