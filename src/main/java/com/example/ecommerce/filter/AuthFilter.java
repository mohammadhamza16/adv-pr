package com.example.ecommerce.filter;

import com.example.ecommerce.config.ContextAttributes;
import com.example.ecommerce.repository.AuthRepository;
import com.example.ecommerce.util.JwtUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Objects;

public class AuthFilter implements Filter {
    private JwtUtil jwtUtil;
    private AuthRepository authRepository;

    @Override
    public void init(jakarta.servlet.FilterConfig filterConfig) {
        ServletContext context = filterConfig.getServletContext();
        String jwtSecret = (String) context.getAttribute(ContextAttributes.JWT_SECRET);
        long jwtExpiration = (Long) context.getAttribute(ContextAttributes.JWT_EXPIRATION_MS);
        this.jwtUtil = new JwtUtil(jwtSecret, jwtExpiration);
        DataSource ds = (DataSource) context.getAttribute(ContextAttributes.DATA_SOURCE);
        this.authRepository = new AuthRepository(ds);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();
        if (isPublicEndpoint(httpRequest.getMethod(), path)) {
            chain.doFilter(request, response);
            return;
        }

        String header = httpRequest.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
            return;
        }

        String token = header.substring(7);
        try {
            if (!jwtUtil.isValid(token) || authRepository.isTokenRevoked(token)) {
                throw new IllegalArgumentException("Invalid or revoked token");
            }
            var claims = jwtUtil.parseToken(token);
            httpRequest.setAttribute("authenticatedUserId", Long.parseLong(claims.getSubject()));
            httpRequest.setAttribute("authenticatedUsername", claims.get("username", String.class));
            httpRequest.setAttribute("authenticatedRole", claims.get("role", String.class));
            httpRequest.setAttribute("authenticatedToken", token);
            chain.doFilter(request, response);
        } catch (Exception ex) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"error\":\"" + Objects.requireNonNullElse(ex.getMessage(), "Unauthorized") + "\"}");
        }
    }

    private boolean isPublicEndpoint(String method, String path) {
        if (path.endsWith("/api/auth/register") || path.endsWith("/api/auth/login")) {
            return true;
        }
        return false;
    }
}
