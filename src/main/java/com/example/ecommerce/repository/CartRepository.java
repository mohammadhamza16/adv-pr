package com.example.ecommerce.repository;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartRepository {
    private final DataSource dataSource;
    private final ProductRepository productRepository;

    public CartRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.productRepository = new ProductRepository(dataSource);
    }

    public Cart ensureCart(long userId) throws SQLException {
        String sql = "SELECT id FROM carts WHERE user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cart(rs.getLong("id"), userId);
                }
            }
        }
        String insert = "INSERT INTO carts (user_id) VALUES (?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Cart(rs.getLong(1), userId);
                }
                throw new SQLException("Unable to create cart");
            }
        }
    }

    public void addItem(long userId, long productId, int quantity) throws SQLException {
        Cart cart = ensureCart(userId);
        String select = "SELECT id, quantity FROM cart_items WHERE cart_id = ? AND product_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setLong(1, cart.getId());
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int existing = rs.getInt("quantity");
                    String update = "UPDATE cart_items SET quantity = ? WHERE id = ?";
                    try (PreparedStatement ps2 = connection.prepareStatement(update)) {
                        ps2.setInt(1, existing + quantity);
                        ps2.setLong(2, rs.getLong("id"));
                        ps2.executeUpdate();
                    }
                    return;
                }
            }
            String insert = "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement ps2 = connection.prepareStatement(insert)) {
                ps2.setLong(1, cart.getId());
                ps2.setLong(2, productId);
                ps2.setInt(3, quantity);
                ps2.executeUpdate();
            }
        }
    }

    public Cart getCart(long userId) throws SQLException {
        Cart cart = ensureCart(userId);
        String sql = "SELECT id, product_id, quantity FROM cart_items WHERE cart_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cart.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getLong("id"));
                    item.setCartId(cart.getId());
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    Product product = productRepository.findById(item.getProductId());
                    item.setProduct(product);
                    cart.getItems().add(item);
                }
            }
        }
        return cart;
    }

    public void clearCart(long userId) throws SQLException {
        Cart cart = ensureCart(userId);
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cart.getId());
            ps.executeUpdate();
        }
    }
}
