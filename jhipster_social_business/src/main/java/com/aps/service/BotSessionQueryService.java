package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.BotSession;
import com.aps.repository.BotSessionRepository;
import com.aps.service.criteria.BotSessionCriteria;
import com.aps.service.dto.BotSessionDTO;
import com.aps.service.mapper.BotSessionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BotSession} entities in the database.
 * The main input is a {@link BotSessionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BotSessionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BotSessionQueryService extends QueryService<BotSession> {

    private static final Logger LOG = LoggerFactory.getLogger(BotSessionQueryService.class);

    private final BotSessionRepository botSessionRepository;

    private final BotSessionMapper botSessionMapper;

    public BotSessionQueryService(BotSessionRepository botSessionRepository, BotSessionMapper botSessionMapper) {
        this.botSessionRepository = botSessionRepository;
        this.botSessionMapper = botSessionMapper;
    }

    /**
     * Return a {@link Page} of {@link BotSessionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BotSessionDTO> findByCriteria(BotSessionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BotSession> specification = createSpecification(criteria);
        return botSessionRepository.findAll(specification, page).map(botSessionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BotSessionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<BotSession> specification = createSpecification(criteria);
        return botSessionRepository.count(specification);
    }

    /**
     * Function to convert {@link BotSessionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BotSession> createSpecification(BotSessionCriteria criteria) {
        Specification<BotSession> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BotSession_.id));
            }
            if (criteria.getWaPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getWaPhoneNumber(), BotSession_.waPhoneNumber));
            }
            if (criteria.getCurrentState() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCurrentState(), BotSession_.currentState));
            }
            if (criteria.getLastActiveAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastActiveAt(), BotSession_.lastActiveAt));
            }
        }
        return specification;
    }
}
