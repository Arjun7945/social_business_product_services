package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.FishProduct;
import com.aps.repository.FishProductRepository;
import com.aps.service.criteria.FishProductCriteria;
import com.aps.service.dto.FishProductDTO;
import com.aps.service.mapper.FishProductMapper;
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
 * Service for executing complex queries for {@link FishProduct} entities in the database.
 * The main input is a {@link FishProductCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link FishProductDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FishProductQueryService extends QueryService<FishProduct> {

    private static final Logger LOG = LoggerFactory.getLogger(FishProductQueryService.class);

    private final FishProductRepository fishProductRepository;

    private final FishProductMapper fishProductMapper;

    public FishProductQueryService(FishProductRepository fishProductRepository, FishProductMapper fishProductMapper) {
        this.fishProductRepository = fishProductRepository;
        this.fishProductMapper = fishProductMapper;
    }

    /**
     * Return a {@link Page} of {@link FishProductDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FishProductDTO> findByCriteria(FishProductCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FishProduct> specification = createSpecification(criteria);
        return fishProductRepository.findAll(specification, page).map(fishProductMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FishProductCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<FishProduct> specification = createSpecification(criteria);
        return fishProductRepository.count(specification);
    }

    /**
     * Function to convert {@link FishProductCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FishProduct> createSpecification(FishProductCriteria criteria) {
        Specification<FishProduct> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), FishProduct_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), FishProduct_.name));
            }
            if (criteria.getPricePerKg() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPricePerKg(), FishProduct_.pricePerKg));
            }
            if (criteria.getImageUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImageUrl(), FishProduct_.imageUrl));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), FishProduct_.description));
            }
            if (criteria.getIsAvailable() != null) {
                specification = specification.and(buildSpecification(criteria.getIsAvailable(), FishProduct_.isAvailable));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), FishProduct_.createdAt));
            }
            if (criteria.getImagesId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getImagesId(), root -> root.join(FishProduct_.images, JoinType.LEFT).get(ProductImage_.id))
                );
            }
        }
        return specification;
    }
}
