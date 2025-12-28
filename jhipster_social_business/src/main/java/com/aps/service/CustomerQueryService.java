package com.aps.service;

import com.aps.domain.*; // for static metamodels
import com.aps.domain.Customer;
import com.aps.repository.CustomerRepository;
import com.aps.service.criteria.CustomerCriteria;
import com.aps.service.dto.CustomerDTO;
import com.aps.service.mapper.CustomerMapper;
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
 * Service for executing complex queries for {@link Customer} entities in the database.
 * The main input is a {@link CustomerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CustomerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomerQueryService extends QueryService<Customer> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerQueryService.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    public CustomerQueryService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    /**
     * Return a {@link Page} of {@link CustomerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findByCriteria(CustomerCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.findAll(specification, page).map(customerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CustomerCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.count(specification);
    }

    /**
     * Function to convert {@link CustomerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Customer> createSpecification(CustomerCriteria criteria) {
        Specification<Customer> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Customer_.id));
            }
            if (criteria.getWaPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getWaPhoneNumber(), Customer_.waPhoneNumber));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Customer_.name));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), Customer_.phoneNumber));
            }
            if (criteria.getLocationLat() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLocationLat(), Customer_.locationLat));
            }
            if (criteria.getLocationLon() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLocationLon(), Customer_.locationLon));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Customer_.address));
            }
            if (criteria.getDistanceFromBusinessKm() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getDistanceFromBusinessKm(), Customer_.distanceFromBusinessKm)
                );
            }
            if (criteria.getIsPincodeValid() != null) {
                specification = specification.and(buildSpecification(criteria.getIsPincodeValid(), Customer_.isPincodeValid));
            }
            if (criteria.getRole() != null) {
                specification = specification.and(buildSpecification(criteria.getRole(), Customer_.role));
            }
            if (criteria.getJoinedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getJoinedAt(), Customer_.joinedAt));
            }
            if (criteria.getLastInteractionAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastInteractionAt(), Customer_.lastInteractionAt));
            }
            if (criteria.getOrdersId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getOrdersId(), root -> root.join(Customer_.orders, JoinType.LEFT).get(CustomerOrder_.id))
                );
            }
            if (criteria.getCartId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCartId(), root -> root.join(Customer_.carts, JoinType.LEFT).get(ShoppingCart_.id))
                );
            }
            if (criteria.getReturnsId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getReturnsId(), root -> root.join(Customer_.returns, JoinType.LEFT).get(ReturnedOrder_.id))
                );
            }
            if (criteria.getAddedById() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAddedById(), root -> root.join(Customer_.addedBy, JoinType.LEFT).get(TeamMember_.id))
                );
            }
            if (criteria.getZoneId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getZoneId(), root -> root.join(Customer_.zone, JoinType.LEFT).get(DeliveryZone_.id))
                );
            }
        }
        return specification;
    }
}
