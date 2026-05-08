package com.example.ecommerce.service;

import com.example.ecommerce.model.Payment;
import com.example.ecommerce.dao.PaymentRepository;
import com.example.ecommerce.util.PaymentRateLimiter;

import java.sql.SQLException;
import java.util.List;

public class PaymentService {
    private final PaymentRepository repository;
    private final PaymentRateLimiter rateLimiter;

    public PaymentService(PaymentRepository repository, PaymentRateLimiter rateLimiter) {
        this.repository = repository;
        this.rateLimiter = rateLimiter;
    }

    public Payment payOrder(long userId, long orderId, String method, java.math.BigDecimal amount) throws SQLException {
        String lockKey = String.format("%d:%d", userId, orderId);
        if (!rateLimiter.acquireLock(lockKey)) {
            throw new IllegalStateException("Repeated payment attempt detected. Please try again later.");
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setMethod(method);
        payment.setAmount(amount);
        payment.setStatus("SUCCESS");
        payment.setTransactionRef("TXN-" + System.currentTimeMillis());
        return repository.create(payment);
    }

    public List<Payment> listPaymentsForOrder(long orderId) throws SQLException {
        return repository.findByOrderId(orderId);
    }

    public List<Payment> listAllPayments() throws SQLException {
        return repository.findAll();
    }
}

