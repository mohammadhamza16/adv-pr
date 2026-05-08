package com.example.ecommerce.controller;

import com.example.ecommerce.database.ContextAttributes;
import com.example.ecommerce.dto.AuthRequest;
import com.example.ecommerce.dto.AuthResponse;
import com.example.ecommerce.dto.SimpleResponse;
import com.example.ecommerce.model.User;
import com.example.ecommerce.dao.AuthRepository;
import com.example.ecommerce.dao.UserRepository;
import com.example.ecommerce.service.AuthService;
import com.example.ecommerce.util.JsonUtil;
import com.example.ecommerce.util.JwtUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;

public class AuthServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        DataSource dataSource = (DataSource) context.getAttribute(ContextAttributes.DATA_SOURCE);
        String jwtSecret = (String) context.getAttribute(ContextAttributes.JWT_SECRET);
        long jwtExpiration = (Long) context.getAttribute(ContextAttributes.JWT_EXPIRATION_MS);
        UserRepository userRepository = new UserRepository(dataSource);
        AuthRepository authRepository = new AuthRepository(dataSource);
        authService = new AuthService(userRepository, authRepository, new JwtUtil(jwtSecret, jwtExpiration), jwtExpiration);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            return;
        }
        try {
            if (path.equals("/register")) {
                AuthRequest request = JsonUtil.fromJson(req, AuthRequest.class);
                if (request.getUsername() == null || request.getPassword() == null || request.getEmail() == null) {
                    throw new IllegalArgumentException("username, email, and password are required");
                }
                String role = "USER";
                if ("ADMIN".equalsIgnoreCase(request.getRole())) {
                    String currentRole = (String) req.getAttribute("authenticatedRole");
                    if (currentRole != null && currentRole.equalsIgnoreCase("ADMIN")) {
                        role = "ADMIN";
                    } else if (new UserRepository((DataSource) getServletContext().getAttribute(ContextAttributes.DATA_SOURCE)).countUsers() == 0) {
                        role = "ADMIN";
                    }
                }
                User created = authService.register(request.getUsername(), request.getEmail(), request.getPassword(), role);
                JsonUtil.sendJson(resp, new SimpleResponse("User registered with id " + created.getId()), HttpServletResponse.SC_CREATED);
            } else if (path.equals("/login")) {
                AuthRequest request = JsonUtil.fromJson(req, AuthRequest.class);
                if (request.getUsername() == null || request.getPassword() == null) {
                    throw new IllegalArgumentException("username/email and password are required");
                }
                AuthResponse authResponse = authService.login(request.getUsername(), request.getPassword());
                JsonUtil.sendJson(resp, authResponse);
            } else if (path.equals("/logout")) {
                String authHeader = req.getHeader("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new IllegalArgumentException("Authorization token required for logout");
                }
                String token = authHeader.substring(7);
                authService.logout(token);
                JsonUtil.sendJson(resp, new SimpleResponse("Logout successful"));
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            }
        } catch (Exception ex) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }
}

