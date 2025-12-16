package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.ShoppingCart;
import com.aps.repository.ShoppingCartRepository;
import com.aps.service.criteria.ShoppingCartCriteria;
import com.aps.service.dto.ShoppingCartDTO;
import com.aps.service.mapper.ShoppingCartMapper;
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
 * Service for executing complex queries for {@link ShoppingCart} entities in the database.
 * The main input is a {@link ShoppingCartCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ShoppingCartDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShoppingCartQueryService extends QueryService<ShoppingCart> {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartQueryService.class);

    private final ShoppingCartRepository shoppingCartRepository;

    private final ShoppingCartMapper shoppingCartMapper;

    public ShoppingCartQueryService(ShoppingCartRepository shoppingCartRepository, ShoppingCartMapper shoppingCartMapper) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartMapper = shoppingCartMapper;
    }

    /**
     * Return a {@link Page} of {@link ShoppingCartDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ShoppingCartDTO> findByCriteria(ShoppingCartCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ShoppingCart> specification = createSpecification(criteria);
        return shoppingCartRepository.findAll(specification, page).map(shoppingCartMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShoppingCartCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ShoppingCart> specification = createSpecification(criteria);
        return shoppingCartRepository.count(specification);
    }

    /**
     * Function to convert {@link ShoppingCartCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ShoppingCart> createSpecification(ShoppingCartCriteria criteria) {
        Specification<ShoppingCart> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ShoppingCart_.id));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), ShoppingCart_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), ShoppingCart_.updatedAt));
            }
            if (criteria.getItemsId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getItemsId(), root -> root.join(ShoppingCart_.items, JoinType.LEFT).get(CartItem_.id))
                );
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCustomerId(), root -> root.join(ShoppingCart_.customer, JoinType.LEFT).get(Customer_.id))
                );
            }
        }
        return specification;
    }
}
