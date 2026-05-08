package com.example.ecommerce.controller;

import com.example.ecommerce.database.ContextAttributes;
import com.example.ecommerce.dto.OrderRequest;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.dao.OrderRepository;
import com.example.ecommerce.dao.ProductRepository;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.util.JsonUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

public class OrderServlet extends HttpServlet {
    private OrderService orderService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        DataSource dataSource = (DataSource) context.getAttribute(ContextAttributes.DATA_SOURCE);
        orderService = new OrderService(new OrderRepository(dataSource), new CartService(new com.example.ecommerce.dao.CartRepository(dataSource)), new ProductRepository(dataSource));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long userId = getAuthenticatedUserId(req);
            OrderRequest request = JsonUtil.fromJson(req, OrderRequest.class);
            Order order = orderService.placeOrder(userId, request.getShippingAddress());
            JsonUtil.sendJson(resp, order, HttpServletResponse.SC_CREATED);
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String role = getAuthenticatedRole(req);
            List<Order> orders;
            if (req.getPathInfo() != null && req.getPathInfo().equals("/all")) {
                if (!"ADMIN".equalsIgnoreCase(role)) {
                    throw new IllegalArgumentException("Admin role required");
                }
                orders = orderService.getAllOrders();
            } else {
                orders = orderService.getOrdersForUser(getAuthenticatedUserId(req));
            }
            JsonUtil.sendJson(resp, orders);
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

