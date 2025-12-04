package com.kevdev.oms;

import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.repository.CustomerRepository;
import com.kevdev.oms.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");
        customer.setEmail("alice@example.com");
    }

    @Test
    void getCustomerById_existingCustomer_returnsCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(customer);
    }

    @Test
    void getCustomerById_missingCustomer_returnsEmptyOptional() {
        when(customerRepository.findById(42L)).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.getCustomerById(42L);

        assertThat(result).isEmpty();
    }

    @Test
    void createCustomer_persistsAndReturnsCustomer() {
        Customer toCreate = new Customer();
        toCreate.setName("Bob");
        toCreate.setEmail("bob@example.com");

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(10L);
            return c;
        });

        Customer result = customerService.createCustomer(toCreate);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        Customer saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("Bob");
        assertThat(saved.getEmail()).isEqualTo("bob@example.com");

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Bob");
    }
}

