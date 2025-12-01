package com.kevdev.oms.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CreateOrderRequest {

    private Long customerId;
    private List<Long> productIds;
    private LocalDateTime orderDate; // optional

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}
