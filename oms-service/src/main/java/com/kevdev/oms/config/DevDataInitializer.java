package com.kevdev.oms.config;

import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.entity.Order;
import com.kevdev.oms.entity.Product;
import com.kevdev.oms.repository.CustomerRepository;
import com.kevdev.oms.repository.OrderRepository;
import com.kevdev.oms.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public DevDataInitializer(
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository
    ) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0 || productRepository.count() > 0 || orderRepository.count() > 0) {
            return;
        }

        Customer alice = new Customer();
        alice.setName("Alice Example");
        alice.setEmail("alice@example.com");

        Customer bob = new Customer();
        bob.setName("Bob Example");
        bob.setEmail("bob@example.com");

        customerRepository.saveAll(List.of(alice, bob));

        Product widget = new Product();
        widget.setName("Widget");
        widget.setSku("WIDGET001");
        widget.setPrice(new BigDecimal("19.99"));

        Product gadget = new Product();
        gadget.setName("Gadget");
        gadget.setSku("GADGET001");
        gadget.setPrice(new BigDecimal("49.50"));

        productRepository.saveAll(List.of(widget, gadget));

        Order order = new Order();
        order.setCustomer(alice);
        order.setProducts(List.of(widget, gadget));
        order.setOrderDate(LocalDateTime.now());

        orderRepository.save(order);
    }
}
