package com.example.ecommerce.dao;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Product;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final DataSource dataSource;
    private final ProductRepository productRepository;

    public OrderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.productRepository = new ProductRepository(dataSource);
    }

    public Order create(Order order) throws SQLException {
        String insertOrder = "INSERT INTO orders (user_id, shipping_address, total_amount, status) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertOrder, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, order.getUserId());
            ps.setString(2, order.getShippingAddress());
            ps.setBigDecimal(3, order.getTotalAmount());
            ps.setString(4, order.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Failed to create order");
                }
            }
        }

        String insertItem = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertItem)) {
            for (OrderItem item : order.getItems()) {
                ps.setLong(1, order.getId());
                ps.setLong(2, item.getProductId());
                ps.setInt(3, item.getQuantity());
                ps.setBigDecimal(4, item.getUnitPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        }

        return order;
    }

    public List<Order> findByUserId(long userId) throws SQLException {
        String sql = "SELECT id, user_id, shipping_address, total_amount, status, created_at, updated_at FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        return findOrders(sql, userId);
    }

    public List<Order> findAll() throws SQLException {
        String sql = "SELECT id, user_id, shipping_address, total_amount, status, created_at, updated_at FROM orders ORDER BY created_at DESC";
        return findOrders(sql);
    }

    private List<Order> findOrders(String sql, Object... params) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Order> orders = new ArrayList<>();
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
                return orders;
            }
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setUpdatedAt(rs.getTimestamp("updated_at"));
        order.getItems().addAll(findItems(order.getId()));
        return order;
    }

    private List<OrderItem> findItems(long orderId) throws SQLException {
        String sql = "SELECT id, product_id, quantity, unit_price FROM order_items WHERE order_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderItem> items = new ArrayList<>();
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(orderId);
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    Product product = productRepository.findById(item.getProductId());
                    item.setProduct(product);
                    items.add(item);
                }
                return items;
            }
        }
    }
}

