package com.example.ecommerce.dto;

public class OrderRequest {
    private String shippingAddress;

    public OrderRequest() {
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
