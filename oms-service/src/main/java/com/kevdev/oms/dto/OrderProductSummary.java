package com.kevdev.oms.dto;

import java.math.BigDecimal;

public class OrderProductSummary {

    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;

    public OrderProductSummary() {
    }

    public OrderProductSummary(Long id, String name, String sku, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
