package com.kevdev.oms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevdev.oms.controller.CustomerController;
import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.exception.ResourceNotFoundException;
import com.kevdev.oms.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer buildCustomer(Long id, String name, String email) {
        Customer c = new Customer();
        c.setId(id);
        c.setName(name);
        c.setEmail(email);
        return c;
    }

    @Test
    void getAll_returnsListOfCustomers() throws Exception {
        Customer c1 = buildCustomer(1L, "Alice", "alice@example.com");

        Mockito.when(customerRepository.findAll()).thenReturn(List.of(c1));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    void getById_existingCustomer_returnsCustomer() throws Exception {
        Customer c1 = buildCustomer(1L, "Alice", "alice@example.com");

        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(c1));

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void getById_missingCustomer_returnsNotFound() throws Exception {
        Mockito.when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validCustomer_returnsCreated() throws Exception {
        Customer toSave = buildCustomer(null, "Bob", "bob@example.com");
        Customer saved = buildCustomer(10L, "Bob", "bob@example.com");

        Mockito.when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        mockMvc.perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(toSave))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Bob"));
    }

    @Test
    void delete_existingCustomer_returnsNoContent() throws Exception {
        Mockito.when(customerRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_missingCustomer_returnsNotFound() throws Exception {
        Mockito.when(customerRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/customers/999"))
                .andExpect(status().isNotFound());
    }
}

