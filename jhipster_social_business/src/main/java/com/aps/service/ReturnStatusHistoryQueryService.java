package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.ReturnStatusHistory;
import com.aps.repository.ReturnStatusHistoryRepository;
import com.aps.service.criteria.ReturnStatusHistoryCriteria;
import com.aps.service.dto.ReturnStatusHistoryDTO;
import com.aps.service.mapper.ReturnStatusHistoryMapper;
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
 * Service for executing complex queries for {@link ReturnStatusHistory} entities in the database.
 * The main input is a {@link ReturnStatusHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ReturnStatusHistoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReturnStatusHistoryQueryService extends QueryService<ReturnStatusHistory> {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnStatusHistoryQueryService.class);

    private final ReturnStatusHistoryRepository returnStatusHistoryRepository;

    private final ReturnStatusHistoryMapper returnStatusHistoryMapper;

    public ReturnStatusHistoryQueryService(
        ReturnStatusHistoryRepository returnStatusHistoryRepository,
        ReturnStatusHistoryMapper returnStatusHistoryMapper
    ) {
        this.returnStatusHistoryRepository = returnStatusHistoryRepository;
        this.returnStatusHistoryMapper = returnStatusHistoryMapper;
    }

    /**
     * Return a {@link Page} of {@link ReturnStatusHistoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReturnStatusHistoryDTO> findByCriteria(ReturnStatusHistoryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ReturnStatusHistory> specification = createSpecification(criteria);
        return returnStatusHistoryRepository.findAll(specification, page).map(returnStatusHistoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReturnStatusHistoryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ReturnStatusHistory> specification = createSpecification(criteria);
        return returnStatusHistoryRepository.count(specification);
    }

    /**
     * Function to convert {@link ReturnStatusHistoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ReturnStatusHistory> createSpecification(ReturnStatusHistoryCriteria criteria) {
        Specification<ReturnStatusHistory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ReturnStatusHistory_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), ReturnStatusHistory_.status));
            }
            if (criteria.getChangeTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getChangeTime(), ReturnStatusHistory_.changeTime));
            }
            if (criteria.getReturnedOrderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getReturnedOrderId(), root ->
                        root.join(ReturnStatusHistory_.returnedOrder, JoinType.LEFT).get(ReturnedOrder_.id)
                    )
                );
            }
        }
        return specification;
    }
}
