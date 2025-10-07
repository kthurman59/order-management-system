package com.kevdev.oms.repository;

import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {}