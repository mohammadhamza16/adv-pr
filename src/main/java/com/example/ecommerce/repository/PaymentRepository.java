package com.example.ecommerce.repository;

import com.example.ecommerce.model.Payment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {
    private final DataSource dataSource;

    public PaymentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Payment create(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (order_id, method, amount, status, transaction_ref) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, payment.getOrderId());
            ps.setString(2, payment.getMethod());
            ps.setBigDecimal(3, payment.getAmount());
            ps.setString(4, payment.getStatus());
            ps.setString(5, payment.getTransactionRef());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    payment.setId(rs.getLong(1));
                }
            }
            return payment;
        }
    }

    public List<Payment> findByOrderId(long orderId) throws SQLException {
        String sql = "SELECT id, order_id, method, amount, status, transaction_ref, created_at, updated_at FROM payments WHERE order_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Payment> payments = new ArrayList<>();
                while (rs.next()) {
                    payments.add(mapPayment(rs));
                }
                return payments;
            }
        }
    }

    public List<Payment> findAll() throws SQLException {
        String sql = "SELECT id, order_id, method, amount, status, transaction_ref, created_at, updated_at FROM payments ORDER BY created_at DESC";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Payment> payments = new ArrayList<>();
            while (rs.next()) {
                payments.add(mapPayment(rs));
            }
            return payments;
        }
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getLong("id"));
        payment.setOrderId(rs.getLong("order_id"));
        payment.setMethod(rs.getString("method"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setStatus(rs.getString("status"));
        payment.setTransactionRef(rs.getString("transaction_ref"));
        payment.setCreatedAt(rs.getTimestamp("created_at"));
        payment.setUpdatedAt(rs.getTimestamp("updated_at"));
        return payment;
    }
}
