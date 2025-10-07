package com.kevdev.oms.repository;

import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {}
