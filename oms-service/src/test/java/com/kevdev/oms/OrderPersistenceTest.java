package com.kevdev.oms;

import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.entity.Order;
import com.kevdev.oms.entity.Product;
import com.kevdev.oms.repository.CustomerRepository;
import com.kevdev.oms.repository.OrderRepository;
import com.kevdev.oms.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderPersistenceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void persistsCustomerOrderAndProducts() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        Customer savedCustomer = customerRepository.save(customer);

        Product productOne = new Product();
        productOne.setSku("SKU_1");
        productOne.setName("Product One");
        productOne.setPrice(new BigDecimal("10.00"));

        Product productTwo = new Product();
        productTwo.setSku("SKU_2");
        productTwo.setName("Product Two");
        productTwo.setPrice(new BigDecimal("20.00"));

        productRepository.saveAll(List.of(productOne, productTwo));

        Order order = new Order();
        order.setCustomer(savedCustomer);
        order.setProducts(List.of(productOne, productTwo));
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        entityManager.flush();
        entityManager.clear();

        Order reloaded = orderRepository.findById(savedOrder.getId()).orElseThrow();

        assertThat(reloaded.getCustomer().getId()).isEqualTo(savedCustomer.getId());
        assertThat(reloaded.getProducts()).hasSize(2);
        assertThat(reloaded.getProducts())
                .extracting(Product::getSku)
                .containsExactlyInAnyOrder("SKU_1", "SKU_2");
    }
}
