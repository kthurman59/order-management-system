package com.kevdev.oms.repository;

import com.kevdev.oms.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
           select distinct o
           from Order o
           left join fetch o.customer
           left join fetch o.products
           where o.id = :id
           """)
    Optional<Order> findByIdWithCustomerAndProducts(@Param("id") Long id);
}
