package com.example.ecommerce.controller;

import com.example.ecommerce.database.ContextAttributes;
import com.example.ecommerce.dto.CategoryRequest;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.dao.CategoryRepository;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.util.JsonUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

public class CategoryServlet extends HttpServlet {
    private CategoryService categoryService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        DataSource dataSource = (DataSource) context.getAttribute(ContextAttributes.DATA_SOURCE);
        categoryService = new CategoryService(new CategoryRepository(dataSource));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Category> categories = categoryService.findAll();
            JsonUtil.sendJson(resp, categories);
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireAdmin(req);
            CategoryRequest request = JsonUtil.fromJson(req, CategoryRequest.class);
            Category category = new Category();
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            Category created = categoryService.create(category);
            JsonUtil.sendJson(resp, created, HttpServletResponse.SC_CREATED);
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireAdmin(req);
            long id = parseId(req.getPathInfo());
            CategoryRequest request = JsonUtil.fromJson(req, CategoryRequest.class);
            Category category = new Category();
            category.setId(id);
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            boolean updated = categoryService.update(category);
            if (!updated) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Category not found");
            } else {
                JsonUtil.sendJson(resp, category);
            }
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            requireAdmin(req);
            long id = parseId(req.getPathInfo());
            boolean deleted = categoryService.delete(id);
            if (!deleted) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Category not found");
            } else {
                JsonUtil.sendJson(resp, "Category deleted successfully");
            }
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    private long parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.isBlank()) {
            throw new IllegalArgumentException("Category ID required");
        }
        String trimmed = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        return Long.parseLong(trimmed);
    }

    private void requireAdmin(HttpServletRequest req) {
        String role = (String) req.getAttribute("authenticatedRole");
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new IllegalArgumentException("Admin role required");
        }
    }
}

