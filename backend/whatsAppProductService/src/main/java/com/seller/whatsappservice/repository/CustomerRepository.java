package com.seller.whatsappservice.repository;

import com.seller.whatsappservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByWaPhoneNumber(String waPhoneNumber);

    List<Customer> findByAddedByExecutiveId(Long executiveId);
}
