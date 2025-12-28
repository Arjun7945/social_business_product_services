package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.ReturnedOrder;
import com.aps.repository.ReturnedOrderRepository;
import com.aps.service.criteria.ReturnedOrderCriteria;
import com.aps.service.dto.ReturnedOrderDTO;
import com.aps.service.mapper.ReturnedOrderMapper;
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
 * Service for executing complex queries for {@link ReturnedOrder} entities in the database.
 * The main input is a {@link ReturnedOrderCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ReturnedOrderDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReturnedOrderQueryService extends QueryService<ReturnedOrder> {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnedOrderQueryService.class);

    private final ReturnedOrderRepository returnedOrderRepository;

    private final ReturnedOrderMapper returnedOrderMapper;

    public ReturnedOrderQueryService(ReturnedOrderRepository returnedOrderRepository, ReturnedOrderMapper returnedOrderMapper) {
        this.returnedOrderRepository = returnedOrderRepository;
        this.returnedOrderMapper = returnedOrderMapper;
    }

    /**
     * Return a {@link Page} of {@link ReturnedOrderDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReturnedOrderDTO> findByCriteria(ReturnedOrderCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ReturnedOrder> specification = createSpecification(criteria);
        return returnedOrderRepository.findAll(specification, page).map(returnedOrderMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReturnedOrderCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ReturnedOrder> specification = createSpecification(criteria);
        return returnedOrderRepository.count(specification);
    }

    /**
     * Function to convert {@link ReturnedOrderCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ReturnedOrder> createSpecification(ReturnedOrderCriteria criteria) {
        Specification<ReturnedOrder> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ReturnedOrder_.id));
            }
            if (criteria.getReturnDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReturnDate(), ReturnedOrder_.returnDate));
            }
            if (criteria.getPaymentReceivedMode() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getPaymentReceivedMode(), ReturnedOrder_.paymentReceivedMode)
                );
            }
            if (criteria.getPaymentReturnedMode() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getPaymentReturnedMode(), ReturnedOrder_.paymentReturnedMode)
                );
            }
            if (criteria.getProductClaimStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getProductClaimStatus(), ReturnedOrder_.productClaimStatus));
            }
            if (criteria.getRefundAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRefundAmount(), ReturnedOrder_.refundAmount));
            }
            if (criteria.getHistoryId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getHistoryId(), root ->
                        root.join(ReturnedOrder_.histories, JoinType.LEFT).get(ReturnStatusHistory_.id)
                    )
                );
            }
            if (criteria.getItemsId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getItemsId(), root ->
                        root.join(ReturnedOrder_.items, JoinType.LEFT).get(ReturnedOrderItem_.id)
                    )
                );
            }
            if (criteria.getOrderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getOrderId(), root -> root.join(ReturnedOrder_.order, JoinType.LEFT).get(CustomerOrder_.id))
                );
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCustomerId(), root -> root.join(ReturnedOrder_.customer, JoinType.LEFT).get(Customer_.id)
                    )
                );
            }
        }
        return specification;
    }
}
