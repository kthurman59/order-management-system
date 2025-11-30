package com.kevdev.oms;

import java.math.BigDecimal;
import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.entity.Order;
import com.kevdev.oms.entity.Product;
import com.kevdev.oms.repository.CustomerRepository;
import com.kevdev.oms.repository.OrderRepository;
import com.kevdev.oms.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional
    void findByIdWithCustomerAndProducts_returns_fully_initialized_order() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("customer@example.com");
        customer = customerRepository.save(customer);

        Product p1 = new Product();
        p1.setName("First product");
        p1.setPrice(new BigDecimal("10.00"));
        p1 = productRepository.save(p1);

        Product p2 = new Product();
        p2.setName("Second product");
        p2.setPrice(new BigDecimal("20.00"));
        p2 = productRepository.save(p2);

        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(Arrays.asList(p1, p2));
        order.setOrderDate(LocalDateTime.now());
        order = orderRepository.save(order);

        Order found = orderRepository.findByIdWithCustomerAndProducts(order.getId())
                .orElseThrow();

        assertThat(found.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(found.getProducts()).hasSize(2);
    }
}
