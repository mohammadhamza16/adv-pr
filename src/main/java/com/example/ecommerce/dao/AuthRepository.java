package com.example.ecommerce.dao;

import com.example.ecommerce.model.AuthToken;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class AuthRepository {
    private final DataSource dataSource;

    public AuthRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveToken(AuthToken token) throws SQLException {
        String sql = "INSERT INTO auth_tokens (user_id, token, revoked, expires_at) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, token.getUserId());
            ps.setString(2, token.getToken());
            ps.setBoolean(3, token.isRevoked());
            ps.setTimestamp(4, new Timestamp(token.getExpiresAt().getTime()));
            ps.executeUpdate();
        }
    }

    public void revokeToken(String tokenValue) throws SQLException {
        String sql = "UPDATE auth_tokens SET revoked = TRUE WHERE token = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tokenValue);
            ps.executeUpdate();
        }
    }

    public boolean isTokenRevoked(String tokenValue) throws SQLException {
        String sql = "SELECT revoked FROM auth_tokens WHERE token = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tokenValue);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("revoked");
                }
                return true;
            }
        }
    }
}

