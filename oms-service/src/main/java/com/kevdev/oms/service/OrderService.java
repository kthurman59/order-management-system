package com.kevdev.oms.service;

import com.kevdev.oms.dto.CreateOrderRequest;
import com.kevdev.oms.dto.OrderProductSummary;
import com.kevdev.oms.dto.OrderResponse;
import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.entity.Order;
import com.kevdev.oms.entity.Product;
import com.kevdev.oms.repository.CustomerRepository;
import com.kevdev.oms.repository.OrderRepository;
import com.kevdev.oms.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id " + id));
        return toResponse(order);
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id " + request.getCustomerId()));

        List<Product> products = productRepository.findAllById(request.getProductIds());
        if (products.isEmpty()) {
            throw new IllegalArgumentException("No products found for ids " + request.getProductIds());
        }

        BigDecimal total = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(products);
        order.setOrderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDateTime.now());
        order.setStatus("NEW");
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public OrderResponse updateOrder(Long id, CreateOrderRequest request) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id " + id));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id " + request.getCustomerId()));

        List<Product> products = productRepository.findAllById(request.getProductIds());
        if (products.isEmpty()) {
            throw new IllegalArgumentException("No products found for ids " + request.getProductIds());
        }

        BigDecimal total = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        existing.setCustomer(customer);
        existing.setProducts(products);
        existing.setOrderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDateTime.now());
        existing.setTotalAmount(total);

        Order saved = orderRepository.save(existing);
        return toResponse(saved);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomer().getId());
        response.setCustomerName(order.getCustomer().getName());
        response.setStatus(order.getStatus());
        response.setOrderDate(order.getOrderDate());
        response.setTotalAmount(order.getTotalAmount());

        List<OrderProductSummary> productSummaries = order.getProducts()
                .stream()
                .map(p -> new OrderProductSummary(
                        p.getId(),
                        p.getName(),
                        p.getSku(),
                        p.getPrice()
                ))
                .collect(Collectors.toList());
        response.setProducts(productSummaries);

        return response;
    }
}
