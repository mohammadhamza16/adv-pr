package com.example.ecommerce.controller;

import com.example.ecommerce.database.ContextAttributes;
import com.example.ecommerce.dto.CartRequest;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.dao.CartRepository;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.util.JsonUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;

public class CartServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        DataSource dataSource = (DataSource) context.getAttribute(ContextAttributes.DATA_SOURCE);
        cartService = new CartService(new CartRepository(dataSource));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long userId = getAuthenticatedUserId(req);
            Cart cart = cartService.getCart(userId);
            JsonUtil.sendJson(resp, cart);
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (!req.getPathInfo().equals("/add")) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
                return;
            }
            long userId = getAuthenticatedUserId(req);
            CartRequest request = JsonUtil.fromJson(req, CartRequest.class);
            cartService.addToCart(userId, request.getProductId(), request.getQuantity());
            JsonUtil.sendJson(resp, "Product added to cart successfully");
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
}

