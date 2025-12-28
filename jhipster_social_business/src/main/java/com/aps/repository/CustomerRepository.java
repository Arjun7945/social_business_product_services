package com.aps.repository;

import com.aps.domain.Customer;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Customer entity.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    default Optional<Customer> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Customer> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Customer> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select customer from Customer customer left join fetch customer.addedBy left join fetch customer.zone", countQuery = "select count(customer) from Customer customer")
    Page<Customer> findAllWithToOneRelationships(Pageable pageable);

    @Query("select customer from Customer customer left join fetch customer.addedBy left join fetch customer.zone")
    List<Customer> findAllWithToOneRelationships();

    @Query("select customer from Customer customer left join fetch customer.addedBy left join fetch customer.zone where customer.id =:id")
    Optional<Customer> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<Customer> findByWaPhoneNumber(String waPhoneNumber);

    boolean existsByWaPhoneNumber(String waPhoneNumber);

    List<Customer> findByAddedBy(com.aps.domain.TeamMember teamMember);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000") })
    @Query("SELECT c FROM Customer c WHERE c.id = :id")
    Optional<Customer> findByIdForUpdate(@Param("id") Long id);

    boolean existsByPhoneNumber(String phoneNumber);
}
