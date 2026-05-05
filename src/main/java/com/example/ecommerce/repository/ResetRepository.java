package com.example.ecommerce.repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ResetRepository {
    private final DataSource dataSource;

    public ResetRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void resetAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS=0");
            statement.execute("DELETE FROM auth_tokens");
            statement.execute("DELETE FROM payments");
            statement.execute("DELETE FROM order_items");
            statement.execute("DELETE FROM orders");
            statement.execute("DELETE FROM cart_items");
            statement.execute("DELETE FROM carts");
            statement.execute("DELETE FROM products");
            statement.execute("DELETE FROM categories");
            statement.execute("DELETE FROM users");
            statement.execute("SET FOREIGN_KEY_CHECKS=1");
        }
    }
}
