package com.kevdev.oms.controller;

import com.kevdev.oms.dto.CreateOrderRequest;
import com.kevdev.oms.dto.OrderResponse;
import com.kevdev.oms.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Operations for creating and managing orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "List all orders")
    public List<OrderResponse> getAll() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single order by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(schema = @Schema(implementation = com.kevdev.oms.web.ApiError.class))
            )
    })
    public OrderResponse getById(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new order")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error or bad request",
                    content = @Content(schema = @Schema(implementation = com.kevdev.oms.web.ApiError.class))
            )
    })
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order updated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error or bad request",
                    content = @Content(schema = @Schema(implementation = com.kevdev.oms.web.ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(schema = @Schema(implementation = com.kevdev.oms.web.ApiError.class))
            )
    })
    public OrderResponse update(@PathVariable Long id,
                                @Valid @RequestBody CreateOrderRequest request) {
        return orderService.updateOrder(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an order")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(schema = @Schema(implementation = com.kevdev.oms.web.ApiError.class))
            )
    })
    public void delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}

