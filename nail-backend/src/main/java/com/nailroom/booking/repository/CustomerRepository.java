package com.nailroom.booking.repository;

import com.nailroom.booking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findFirstByPhoneOrEmail(String phone, String email);
    boolean existsByPhone(String phone);
}
