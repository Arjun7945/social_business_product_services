package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.ReturnedOrderItem;
import com.aps.repository.ReturnedOrderItemRepository;
import com.aps.service.criteria.ReturnedOrderItemCriteria;
import com.aps.service.dto.ReturnedOrderItemDTO;
import com.aps.service.mapper.ReturnedOrderItemMapper;
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
 * Service for executing complex queries for {@link ReturnedOrderItem} entities in the database.
 * The main input is a {@link ReturnedOrderItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ReturnedOrderItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReturnedOrderItemQueryService extends QueryService<ReturnedOrderItem> {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnedOrderItemQueryService.class);

    private final ReturnedOrderItemRepository returnedOrderItemRepository;

    private final ReturnedOrderItemMapper returnedOrderItemMapper;

    public ReturnedOrderItemQueryService(
        ReturnedOrderItemRepository returnedOrderItemRepository,
        ReturnedOrderItemMapper returnedOrderItemMapper
    ) {
        this.returnedOrderItemRepository = returnedOrderItemRepository;
        this.returnedOrderItemMapper = returnedOrderItemMapper;
    }

    /**
     * Return a {@link Page} of {@link ReturnedOrderItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReturnedOrderItemDTO> findByCriteria(ReturnedOrderItemCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ReturnedOrderItem> specification = createSpecification(criteria);
        return returnedOrderItemRepository.findAll(specification, page).map(returnedOrderItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReturnedOrderItemCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ReturnedOrderItem> specification = createSpecification(criteria);
        return returnedOrderItemRepository.count(specification);
    }

    /**
     * Function to convert {@link ReturnedOrderItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ReturnedOrderItem> createSpecification(ReturnedOrderItemCriteria criteria) {
        Specification<ReturnedOrderItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ReturnedOrderItem_.id));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), ReturnedOrderItem_.quantity));
            }
            if (criteria.getProductComment() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getProductComment(), ReturnedOrderItem_.productComment)
                );
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getProductId(), root ->
                        root.join(ReturnedOrderItem_.product, JoinType.LEFT).get(FishProduct_.id)
                    )
                );
            }
            if (criteria.getReturnedOrderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getReturnedOrderId(), root ->
                        root.join(ReturnedOrderItem_.returnedOrder, JoinType.LEFT).get(ReturnedOrder_.id)
                    )
                );
            }
        }
        return specification;
    }
}
