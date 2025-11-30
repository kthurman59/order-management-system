package com.kevdev.oms;

import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.entity.Order;
import com.kevdev.oms.entity.Product;
import com.kevdev.oms.repository.CustomerRepository;
import com.kevdev.oms.repository.OrderRepository;
import com.kevdev.oms.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderPersistenceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void persistsCustomerOrderAndProducts() {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail("test.user@example.com");
        Customer savedCustomer = customerRepository.save(customer);

        Product p1 = new Product();
        p1.setName("Test Product One");
        p1.setPrice(new BigDecimal("19.99"));

        Product p2 = new Product();
        p2.setName("Test Product Two");
        p2.setPrice(new BigDecimal("5.50"));

        List<Product> savedProducts = productRepository.saveAll(List.of(p1, p2));

        Order order = new Order();
        order.setCustomer(savedCustomer);
        order.setProducts(savedProducts);
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        Order reloaded = orderRepository.findById(savedOrder.getId())
                .orElseThrow();

        assertThat(reloaded.getCustomer().getId()).isEqualTo(savedCustomer.getId());
        assertThat(reloaded.getProducts()).hasSize(2);
    }
}
