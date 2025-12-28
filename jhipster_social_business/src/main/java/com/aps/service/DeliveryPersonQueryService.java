package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.DeliveryPerson;
import com.aps.repository.DeliveryPersonRepository;
import com.aps.service.criteria.DeliveryPersonCriteria;
import com.aps.service.dto.DeliveryPersonDTO;
import com.aps.service.mapper.DeliveryPersonMapper;
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
 * Service for executing complex queries for {@link DeliveryPerson} entities in the database.
 * The main input is a {@link DeliveryPersonCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DeliveryPersonDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DeliveryPersonQueryService extends QueryService<DeliveryPerson> {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryPersonQueryService.class);

    private final DeliveryPersonRepository deliveryPersonRepository;

    private final DeliveryPersonMapper deliveryPersonMapper;

    public DeliveryPersonQueryService(DeliveryPersonRepository deliveryPersonRepository, DeliveryPersonMapper deliveryPersonMapper) {
        this.deliveryPersonRepository = deliveryPersonRepository;
        this.deliveryPersonMapper = deliveryPersonMapper;
    }

    /**
     * Return a {@link Page} of {@link DeliveryPersonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DeliveryPersonDTO> findByCriteria(DeliveryPersonCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DeliveryPerson> specification = createSpecification(criteria);
        return deliveryPersonRepository.findAll(specification, page).map(deliveryPersonMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DeliveryPersonCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DeliveryPerson> specification = createSpecification(criteria);
        return deliveryPersonRepository.count(specification);
    }

    /**
     * Function to convert {@link DeliveryPersonCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DeliveryPerson> createSpecification(DeliveryPersonCriteria criteria) {
        Specification<DeliveryPerson> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), DeliveryPerson_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), DeliveryPerson_.name));
            }
            if (criteria.getWaPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getWaPhoneNumber(), DeliveryPerson_.waPhoneNumber));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), DeliveryPerson_.phoneNumber));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), DeliveryPerson_.status));
            }
            if (criteria.getJoinedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getJoinedAt(), DeliveryPerson_.joinedAt));
            }
            if (criteria.getIsActive() != null) {
                specification = specification.and(buildSpecification(criteria.getIsActive(), DeliveryPerson_.isActive));
            }
            if (criteria.getOrdersId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getOrdersId(), root ->
                        root.join(DeliveryPerson_.orders, JoinType.LEFT).get(CustomerOrder_.id)
                    )
                );
            }
            if (criteria.getAddedById() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAddedById(), root ->
                        root.join(DeliveryPerson_.addedBy, JoinType.LEFT).get(TeamMember_.id)
                    )
                );
            }
            if (criteria.getZoneId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getZoneId(), root -> root.join(DeliveryPerson_.zone, JoinType.LEFT).get(DeliveryZone_.id))
                );
            }
        }
        return specification;
    }
}
