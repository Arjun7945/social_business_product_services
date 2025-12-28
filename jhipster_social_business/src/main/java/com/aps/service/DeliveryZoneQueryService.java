package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.DeliveryZone;
import com.aps.repository.DeliveryZoneRepository;
import com.aps.service.criteria.DeliveryZoneCriteria;
import com.aps.service.dto.DeliveryZoneDTO;
import com.aps.service.mapper.DeliveryZoneMapper;
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
 * Service for executing complex queries for {@link DeliveryZone} entities in the database.
 * The main input is a {@link DeliveryZoneCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DeliveryZoneDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DeliveryZoneQueryService extends QueryService<DeliveryZone> {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryZoneQueryService.class);

    private final DeliveryZoneRepository deliveryZoneRepository;

    private final DeliveryZoneMapper deliveryZoneMapper;

    public DeliveryZoneQueryService(DeliveryZoneRepository deliveryZoneRepository, DeliveryZoneMapper deliveryZoneMapper) {
        this.deliveryZoneRepository = deliveryZoneRepository;
        this.deliveryZoneMapper = deliveryZoneMapper;
    }

    /**
     * Return a {@link Page} of {@link DeliveryZoneDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DeliveryZoneDTO> findByCriteria(DeliveryZoneCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DeliveryZone> specification = createSpecification(criteria);
        return deliveryZoneRepository.findAll(specification, page).map(deliveryZoneMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DeliveryZoneCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DeliveryZone> specification = createSpecification(criteria);
        return deliveryZoneRepository.count(specification);
    }

    /**
     * Function to convert {@link DeliveryZoneCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DeliveryZone> createSpecification(DeliveryZoneCriteria criteria) {
        Specification<DeliveryZone> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), DeliveryZone_.id));
            }
            if (criteria.getZoneName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getZoneName(), DeliveryZone_.zoneName));
            }
            if (criteria.getPincode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPincode(), DeliveryZone_.pincode));
            }
            if (criteria.getCustomersId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCustomersId(), root ->
                        root.join(DeliveryZone_.customers, JoinType.LEFT).get(Customer_.id)
                    )
                );
            }
            if (criteria.getDeliveryPersonsId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getDeliveryPersonsId(), root ->
                        root.join(DeliveryZone_.deliveryPersons, JoinType.LEFT).get(DeliveryPerson_.id)
                    )
                );
            }
        }
        return specification;
    }
}
