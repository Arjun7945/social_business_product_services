package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.ButtonAction;
import com.aps.repository.ButtonActionRepository;
import com.aps.service.criteria.ButtonActionCriteria;
import com.aps.service.dto.ButtonActionDTO;
import com.aps.service.mapper.ButtonActionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ButtonAction} entities in the database.
 * The main input is a {@link ButtonActionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ButtonActionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ButtonActionQueryService extends QueryService<ButtonAction> {

    private static final Logger LOG = LoggerFactory.getLogger(ButtonActionQueryService.class);

    private final ButtonActionRepository buttonActionRepository;

    private final ButtonActionMapper buttonActionMapper;

    public ButtonActionQueryService(ButtonActionRepository buttonActionRepository, ButtonActionMapper buttonActionMapper) {
        this.buttonActionRepository = buttonActionRepository;
        this.buttonActionMapper = buttonActionMapper;
    }

    /**
     * Return a {@link Page} of {@link ButtonActionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ButtonActionDTO> findByCriteria(ButtonActionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ButtonAction> specification = createSpecification(criteria);
        return buttonActionRepository.findAll(specification, page).map(buttonActionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ButtonActionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ButtonAction> specification = createSpecification(criteria);
        return buttonActionRepository.count(specification);
    }

    /**
     * Function to convert {@link ButtonActionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ButtonAction> createSpecification(ButtonActionCriteria criteria) {
        Specification<ButtonAction> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ButtonAction_.id));
            }
            if (criteria.getWaMessageId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getWaMessageId(), ButtonAction_.waMessageId));
            }
            if (criteria.getButtonId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getButtonId(), ButtonAction_.buttonId));
            }
            if (criteria.getClickedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getClickedAt(), ButtonAction_.clickedAt));
            }
            if (criteria.getClickedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getClickedBy(), ButtonAction_.clickedBy));
            }
        }
        return specification;
    }
}
