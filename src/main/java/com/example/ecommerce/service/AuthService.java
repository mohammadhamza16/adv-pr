package com.example.ecommerce.service;

import com.example.ecommerce.dto.AuthResponse;
import com.example.ecommerce.model.AuthToken;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.AuthRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.util.JwtUtil;
import com.example.ecommerce.util.PasswordUtil;

import java.util.Date;

public class AuthService {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final JwtUtil jwtUtil;
    private final long jwtExpirationMs;

    public AuthService(UserRepository userRepository, AuthRepository authRepository, JwtUtil jwtUtil, long jwtExpirationMs) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
        this.jwtUtil = jwtUtil;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public User register(String username, String email, String password, String role) throws Exception {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setRole(role);
        return userRepository.create(user);
    }

    public AuthResponse login(String usernameOrEmail, String password) throws Exception {
        User user = userRepository.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }
        if (user == null || !PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        AuthToken authToken = new AuthToken();
        authToken.setUserId(user.getId());
        authToken.setToken(token);
        authToken.setRevoked(false);
        authToken.setExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs));
        authRepository.saveToken(authToken);
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    public void logout(String token) throws Exception {
        authRepository.revokeToken(token);
    }

    public boolean isTokenActive(String token) throws Exception {
        return !authRepository.isTokenRevoked(token) && jwtUtil.isValid(token);
    }
}
