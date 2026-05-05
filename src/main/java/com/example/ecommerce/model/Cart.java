package com.example.ecommerce.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private long id;
    private long userId;
    private final List<CartItem> items = new ArrayList<>();

    public Cart() {
    }

    public Cart(long id, long userId) {
        this.id = id;
        this.userId = userId;
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

    public List<CartItem> getItems() {
        return items;
    }
}
