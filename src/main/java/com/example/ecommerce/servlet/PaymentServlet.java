package com.example.ecommerce.servlet;

import com.example.ecommerce.config.ContextAttributes;
import com.example.ecommerce.dto.PaymentRequest;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.service.PaymentService;
import com.example.ecommerce.util.JsonUtil;
import com.example.ecommerce.util.PaymentRateLimiter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

public class PaymentServlet extends HttpServlet {
    private PaymentService paymentService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        DataSource dataSource = (DataSource) context.getAttribute(ContextAttributes.DATA_SOURCE);
        JedisPool jedisPool = (JedisPool) context.getAttribute(ContextAttributes.JEDIS_POOL);
        int lockTtl = (Integer) context.getAttribute(ContextAttributes.PAYMENT_LOCK_TTL);
        paymentService = new PaymentService(new PaymentRepository(dataSource), new PaymentRateLimiter(jedisPool, lockTtl));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long userId = getAuthenticatedUserId(req);
            PaymentRequest request = JsonUtil.fromJson(req, PaymentRequest.class);
            if (request.getAmount() == null) {
                throw new IllegalArgumentException("Amount is required");
            }
            Payment payment = paymentService.payOrder(userId, request.getOrderId(), request.getMethod(), request.getAmount());
            JsonUtil.sendJson(resp, payment, HttpServletResponse.SC_CREATED);
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String orderIdParam = req.getParameter("orderId");
            if (orderIdParam != null) {
                long orderId = Long.parseLong(orderIdParam);
                List<Payment> payments = paymentService.listPaymentsForOrder(orderId);
                JsonUtil.sendJson(resp, payments);
                return;
            }
            String role = getAuthenticatedRole(req);
            if (!"ADMIN".equalsIgnoreCase(role)) {
                throw new IllegalArgumentException("Admin role required to list all payments");
            }
            JsonUtil.sendJson(resp, paymentService.listAllPayments());
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    private long getAuthenticatedUserId(HttpServletRequest req) {
        Object attribute = req.getAttribute("authenticatedUserId");
        if (attribute == null) {
            throw new IllegalArgumentException("User must be authenticated");
        }
        return (Long) attribute;
    }

    private String getAuthenticatedRole(HttpServletRequest req) {
        String role = (String) req.getAttribute("authenticatedRole");
        if (role == null) {
            throw new IllegalArgumentException("User must be authenticated");
        }
        return role;
    }
}
