package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.TeamMember;
import com.aps.repository.TeamMemberRepository;
import com.aps.service.criteria.TeamMemberCriteria;
import com.aps.service.dto.TeamMemberDTO;
import com.aps.service.mapper.TeamMemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TeamMember} entities in the database.
 * The main input is a {@link TeamMemberCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TeamMemberDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TeamMemberQueryService extends QueryService<TeamMember> {

    private static final Logger LOG = LoggerFactory.getLogger(TeamMemberQueryService.class);

    private final TeamMemberRepository teamMemberRepository;

    private final TeamMemberMapper teamMemberMapper;

    public TeamMemberQueryService(TeamMemberRepository teamMemberRepository, TeamMemberMapper teamMemberMapper) {
        this.teamMemberRepository = teamMemberRepository;
        this.teamMemberMapper = teamMemberMapper;
    }

    /**
     * Return a {@link Page} of {@link TeamMemberDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TeamMemberDTO> findByCriteria(TeamMemberCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TeamMember> specification = createSpecification(criteria);
        return teamMemberRepository.findAll(specification, page).map(teamMemberMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TeamMemberCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TeamMember> specification = createSpecification(criteria);
        return teamMemberRepository.count(specification);
    }

    /**
     * Function to convert {@link TeamMemberCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TeamMember> createSpecification(TeamMemberCriteria criteria) {
        Specification<TeamMember> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TeamMember_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), TeamMember_.name));
            }
            if (criteria.getWaPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getWaPhoneNumber(), TeamMember_.waPhoneNumber));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), TeamMember_.phoneNumber));
            }
            if (criteria.getRole() != null) {
                specification = specification.and(buildSpecification(criteria.getRole(), TeamMember_.role));
            }
            if (criteria.getIsActive() != null) {
                specification = specification.and(buildSpecification(criteria.getIsActive(), TeamMember_.isActive));
            }
        }
        return specification;
    }
}
