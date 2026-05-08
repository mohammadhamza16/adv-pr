package com.example.ecommerce.controller;

import com.example.ecommerce.database.ContextAttributes;
import com.example.ecommerce.service.ResetService;
import com.example.ecommerce.util.JsonUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;
import java.io.IOException;

public class ResetServlet extends HttpServlet {
    private ResetService resetService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        DataSource dataSource = (DataSource) context.getAttribute(ContextAttributes.DATA_SOURCE);
        JedisPool jedisPool = (JedisPool) context.getAttribute(ContextAttributes.JEDIS_POOL);
        String productCacheKey = (String) context.getAttribute(ContextAttributes.PRODUCT_CACHE_KEY);
        resetService = new ResetService(new com.example.ecommerce.dao.ResetRepository(dataSource), jedisPool, productCacheKey);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireAdmin(req);
            resetService.resetAll();
            JsonUtil.sendJson(resp, "System reset completed successfully");
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    private void requireAdmin(HttpServletRequest req) {
        String role = (String) req.getAttribute("authenticatedRole");
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new IllegalArgumentException("Admin role required");
        }
    }
}

