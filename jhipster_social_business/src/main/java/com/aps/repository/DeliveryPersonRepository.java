package com.aps.repository;

import com.aps.domain.DeliveryPerson;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DeliveryPerson entity.
 */
@Repository
public interface DeliveryPersonRepository
        extends JpaRepository<DeliveryPerson, Long>, JpaSpecificationExecutor<DeliveryPerson> {

    List<DeliveryPerson> findByIsActive(Boolean isActive);

    Optional<DeliveryPerson> findByWaPhoneNumber(String waPhoneNumber);

    default Optional<DeliveryPerson> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<DeliveryPerson> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<DeliveryPerson> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select deliveryPerson from DeliveryPerson deliveryPerson left join fetch deliveryPerson.addedBy left join fetch deliveryPerson.zone", countQuery = "select count(deliveryPerson) from DeliveryPerson deliveryPerson")
    Page<DeliveryPerson> findAllWithToOneRelationships(Pageable pageable);

    @Query("select deliveryPerson from DeliveryPerson deliveryPerson left join fetch deliveryPerson.addedBy left join fetch deliveryPerson.zone")
    List<DeliveryPerson> findAllWithToOneRelationships();

    @Query("select deliveryPerson from DeliveryPerson deliveryPerson left join fetch deliveryPerson.addedBy left join fetch deliveryPerson.zone where deliveryPerson.id =:id")
    Optional<DeliveryPerson> findOneWithToOneRelationships(@Param("id") Long id);
}
