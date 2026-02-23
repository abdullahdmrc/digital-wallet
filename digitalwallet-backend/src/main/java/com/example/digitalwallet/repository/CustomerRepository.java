package com.example.digitalwallet.repository;

import com.example.digitalwallet.model.Customer;
import com.example.digitalwallet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Integer> {

    Optional<Customer> findByUser(User user);
}
