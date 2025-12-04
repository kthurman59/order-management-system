package com.kevdev.oms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevdev.oms.dto.CreateOrderRequest;
import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_andFetchOrder_endToEnd() throws Exception {
        // create customer
        Customer customer = new Customer();
        customer.setName("Integration User");
        customer.setEmail("integration@example.com");

        String customerJson = objectMapper.writeValueAsString(customer);

        String customerResponse = mockMvc.perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(customerJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Customer createdCustomer = objectMapper.readValue(customerResponse, Customer.class);
        Long customerId = createdCustomer.getId();

        // create product
        Product product = new Product();
        product.setName("Integration Widget");
        product.setSku("INTW001");
        product.setPrice(new BigDecimal("9.99"));

        String productJson = objectMapper.writeValueAsString(product);

        String productResponse = mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Product createdProduct = objectMapper.readValue(productResponse, Product.class);
        Long productId = createdProduct.getId();

        // create order using the created customer and product
        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setCustomerId(customerId);
        orderRequest.setProductIds(List.of(productId));

        String orderJson = objectMapper.writeValueAsString(orderRequest);

        String orderResponse = mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(orderJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // get all orders and verify at least one exists
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}

