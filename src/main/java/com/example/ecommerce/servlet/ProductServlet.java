package com.example.ecommerce.servlet;

import com.example.ecommerce.config.ContextAttributes;
import com.example.ecommerce.dto.ProductRequest;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.util.JsonUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class ProductServlet extends HttpServlet {
    private ProductService productService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        DataSource dataSource = (DataSource) context.getAttribute(ContextAttributes.DATA_SOURCE);
        JedisPool jedisPool = (JedisPool) context.getAttribute(ContextAttributes.JEDIS_POOL);
        String cacheKey = (String) context.getAttribute(ContextAttributes.PRODUCT_CACHE_KEY);
        int cacheTtl = (Integer) context.getAttribute(ContextAttributes.PRODUCT_CACHE_TTL);
        productService = new ProductService(new ProductRepository(dataSource), jedisPool, cacheKey, cacheTtl);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                List<Product> products = productService.findAll();
                JsonUtil.sendJson(resp, products);
            } else {
                long id = parseId(pathInfo);
                Product product = productService.findById(id);
                if (product == null) {
                    JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
                } else {
                    JsonUtil.sendJson(resp, product);
                }
            }
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireRole(req, "ADMIN");
            ProductRequest request = JsonUtil.fromJson(req, ProductRequest.class);
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice() == null ? BigDecimal.ZERO : request.getPrice());
            product.setQuantity(request.getQuantity());
            product.setCategoryId(request.getCategoryId());
            Product created = productService.create(product);
            JsonUtil.sendJson(resp, created, HttpServletResponse.SC_CREATED);
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireRole(req, "ADMIN");
            long id = parseId(req.getPathInfo());
            ProductRequest request = JsonUtil.fromJson(req, ProductRequest.class);
            Product product = new Product();
            product.setId(id);
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice() == null ? BigDecimal.ZERO : request.getPrice());
            product.setQuantity(request.getQuantity());
            product.setCategoryId(request.getCategoryId());
            boolean updated = productService.update(product);
            if (!updated) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
            } else {
                JsonUtil.sendJson(resp, product);
            }
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireRole(req, "ADMIN");
            long id = parseId(req.getPathInfo());
            boolean deleted = productService.delete(id);
            if (!deleted) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
            } else {
                JsonUtil.sendJson(resp, "Product deleted successfully");
            }
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    private long parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.isBlank()) {
            throw new IllegalArgumentException("Product ID required");
        }
        String trimmed = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        if (trimmed.contains("/")) {
            trimmed = trimmed.substring(0, trimmed.indexOf('/'));
        }
        return Long.parseLong(trimmed);
    }

    private void requireRole(HttpServletRequest req, String role) {
        String currentRole = (String) req.getAttribute("authenticatedRole");
        if (currentRole == null || !currentRole.equalsIgnoreCase(role)) {
            throw new IllegalArgumentException("Admin role required");
        }
    }
}
