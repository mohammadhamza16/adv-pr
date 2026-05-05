package com.example.ecommerce.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private long orderId;
    private String method;
    private BigDecimal amount;

    public PaymentRequest() {
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
