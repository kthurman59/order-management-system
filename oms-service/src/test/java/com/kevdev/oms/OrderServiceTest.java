package com.kevdev.oms;

import com.kevdev.oms.dto.CreateOrderRequest;
import com.kevdev.oms.dto.OrderResponse;
import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.entity.Order;
import com.kevdev.oms.entity.Product;
import com.kevdev.oms.exception.ResourceNotFoundException;
import com.kevdev.oms.repository.CustomerRepository;
import com.kevdev.oms.repository.OrderRepository;
import com.kevdev.oms.repository.ProductRepository;
import com.kevdev.oms.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");

        product = new Product();
        product.setId(10L);
        product.setName("Widget");
        product.setPrice(new BigDecimal("9.99"));
    }

    @Test
    void createOrder_happyPath_persistsOrderAndReturnsResponse() {
        // arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);
        request.setProductIds(List.of(10L));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of(product));

        Order savedOrder = new Order();
        savedOrder.setId(100L);
        savedOrder.setCustomer(customer);
        savedOrder.setProducts(List.of(product));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // act
        OrderResponse response = orderService.createOrder(request);

        // assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order orderToSave = orderCaptor.getValue();

        assertThat(orderToSave.getCustomer()).isSameAs(customer);
        assertThat(orderToSave.getProducts()).containsExactly(product);

        // Response level assertions: only use getters that actually exist
        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(1L);
    }

    @Test
    void createOrder_missingCustomer_throwsResourceNotFound() {
        // arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(999L);
        request.setProductIds(List.of(10L));

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // act + assert
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer");
        verifyNoInteractions(productRepository, orderRepository);
    }

    @Test
    void getOrder_missingOrder_throwsResourceNotFound() {
        when(orderRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(123L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order");
    }
}

