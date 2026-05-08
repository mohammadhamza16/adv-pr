package com.example.ecommerce.service;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.dao.CartRepository;

import java.sql.SQLException;

public class CartService {
    private final CartRepository repository;

    public CartService(CartRepository repository) {
        this.repository = repository;
    }

    public void addToCart(long userId, long productId, int quantity) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        repository.addItem(userId, productId, quantity);
    }

    public Cart getCart(long userId) throws SQLException {
        return repository.getCart(userId);
    }

    public void clearCart(long userId) throws SQLException {
        repository.clearCart(userId);
    }
}

