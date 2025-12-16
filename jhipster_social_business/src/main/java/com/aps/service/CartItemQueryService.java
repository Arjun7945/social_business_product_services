package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.CartItem;
import com.aps.repository.CartItemRepository;
import com.aps.service.criteria.CartItemCriteria;
import com.aps.service.dto.CartItemDTO;
import com.aps.service.mapper.CartItemMapper;
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
 * Service for executing complex queries for {@link CartItem} entities in the database.
 * The main input is a {@link CartItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CartItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CartItemQueryService extends QueryService<CartItem> {

    private static final Logger LOG = LoggerFactory.getLogger(CartItemQueryService.class);

    private final CartItemRepository cartItemRepository;

    private final CartItemMapper cartItemMapper;

    public CartItemQueryService(CartItemRepository cartItemRepository, CartItemMapper cartItemMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
    }

    /**
     * Return a {@link Page} of {@link CartItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CartItemDTO> findByCriteria(CartItemCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CartItem> specification = createSpecification(criteria);
        return cartItemRepository.findAll(specification, page).map(cartItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CartItemCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CartItem> specification = createSpecification(criteria);
        return cartItemRepository.count(specification);
    }

    /**
     * Function to convert {@link CartItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CartItem> createSpecification(CartItemCriteria criteria) {
        Specification<CartItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CartItem_.id));
            }
            if (criteria.getQuantityKg() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantityKg(), CartItem_.quantityKg));
            }
            if (criteria.getAddedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAddedAt(), CartItem_.addedAt));
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getProductId(), root -> root.join(CartItem_.product, JoinType.LEFT).get(FishProduct_.id))
                );
            }
            if (criteria.getCartId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCartId(), root -> root.join(CartItem_.cart, JoinType.LEFT).get(ShoppingCart_.id))
                );
            }
        }
        return specification;
    }
}
