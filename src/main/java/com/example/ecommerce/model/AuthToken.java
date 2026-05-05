package com.example.ecommerce.model;

import java.util.Date;

public class AuthToken {
    private long id;
    private long userId;
    private String token;
    private boolean revoked;
    private Date expiresAt;

    public AuthToken() {
    }

    public AuthToken(long id, long userId, String token, boolean revoked, Date expiresAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.revoked = revoked;
        this.expiresAt = expiresAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
