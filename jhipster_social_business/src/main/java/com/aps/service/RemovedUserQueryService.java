package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.RemovedUser;
import com.aps.repository.RemovedUserRepository;
import com.aps.service.criteria.RemovedUserCriteria;
import com.aps.service.dto.RemovedUserDTO;
import com.aps.service.mapper.RemovedUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link RemovedUser} entities in the database.
 * The main input is a {@link RemovedUserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RemovedUserDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RemovedUserQueryService extends QueryService<RemovedUser> {

    private static final Logger LOG = LoggerFactory.getLogger(RemovedUserQueryService.class);

    private final RemovedUserRepository removedUserRepository;

    private final RemovedUserMapper removedUserMapper;

    public RemovedUserQueryService(RemovedUserRepository removedUserRepository, RemovedUserMapper removedUserMapper) {
        this.removedUserRepository = removedUserRepository;
        this.removedUserMapper = removedUserMapper;
    }

    /**
     * Return a {@link Page} of {@link RemovedUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RemovedUserDTO> findByCriteria(RemovedUserCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RemovedUser> specification = createSpecification(criteria);
        return removedUserRepository.findAll(specification, page).map(removedUserMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RemovedUserCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<RemovedUser> specification = createSpecification(criteria);
        return removedUserRepository.count(specification);
    }

    /**
     * Function to convert {@link RemovedUserCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RemovedUser> createSpecification(RemovedUserCriteria criteria) {
        Specification<RemovedUser> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), RemovedUser_.id));
            }
            if (criteria.getOriginalId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOriginalId(), RemovedUser_.originalId));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), RemovedUser_.name));
            }
            if (criteria.getRole() != null) {
                specification = specification.and(buildSpecification(criteria.getRole(), RemovedUser_.role));
            }
            if (criteria.getWhatsappNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getWhatsappNumber(), RemovedUser_.whatsappNumber));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), RemovedUser_.phoneNumber));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), RemovedUser_.address));
            }
            if (criteria.getLocationLat() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLocationLat(), RemovedUser_.locationLat));
            }
            if (criteria.getLocationLon() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLocationLon(), RemovedUser_.locationLon));
            }
            if (criteria.getJoinedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getJoinedAt(), RemovedUser_.joinedAt));
            }
            if (criteria.getRemovedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRemovedAt(), RemovedUser_.removedAt));
            }
            if (criteria.getReasonForRemoval() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReasonForRemoval(), RemovedUser_.reasonForRemoval));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), RemovedUser_.status));
            }
            if (criteria.getOrderHistoryId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrderHistoryId(), RemovedUser_.orderHistoryId));
            }
            if (criteria.getDistanceFromBusinessKm() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getDistanceFromBusinessKm(), RemovedUser_.distanceFromBusinessKm)
                );
            }
            if (criteria.getIsPincodeValid() != null) {
                specification = specification.and(buildSpecification(criteria.getIsPincodeValid(), RemovedUser_.isPincodeValid));
            }
        }
        return specification;
    }
}
