package com.kevdev.oms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevdev.oms.dto.CreateOrderRequest;
import com.kevdev.oms.dto.OrderResponse;
import com.kevdev.oms.exception.ResourceNotFoundException;
import com.kevdev.oms.service.OrderService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = com.kevdev.oms.controller.OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOrder_existingOrder_returnsOk() throws Exception {
        // arrange
        OrderResponse response = new OrderResponse();
        // adjust these setters or constructor to match your OrderResponse
        response.setCustomerId(1L);

        Mockito.when(orderService.getOrder(100L)).thenReturn(response);

        // act + assert
        mockMvc.perform(get("/api/orders/100")) // adjust path to match your controller
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1L));
    }

    @Test
    void getOrder_missingOrder_returnsNotFoundWithApiError() throws Exception {
        Mockito.when(orderService.getOrder(999L))
                .thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/api/orders/999")) // adjust path to match your controller
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
        // adjust jsonPath fields to match your ApiError structure
    }

    @Test
    void createOrder_validRequest_returnsCreated() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);
        request.setProductIds(List.of(10L));

        OrderResponse response = new OrderResponse();
        response.setCustomerId(1L);

        Mockito.when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/orders")                     // adjust path to match your controller
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(1L));
    }

    @Test
    void createOrder_invalidRequest_returnsBadRequest() throws Exception {
        // arrange  missing customerId and empty productIds violate @NotNull and @NotEmpty
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(null);
        request.setProductIds(java.util.Collections.emptyList());

        // act and assert
        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}

