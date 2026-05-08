package com.example.ecommerce.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class AppConfigListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        Properties properties = new Properties();
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (stream == null) {
                throw new IllegalStateException("application.properties not found in classpath");
            }
            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load application.properties", e);
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(properties.getProperty("db.url"));
        hikariConfig.setUsername(properties.getProperty("db.user"));
        hikariConfig.setPassword(properties.getProperty("db.password"));
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        context.setAttribute(ContextAttributes.DATA_SOURCE, dataSource);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMinIdle(1);
        JedisPool jedisPool = new JedisPool(
                poolConfig,
                properties.getProperty("redis.host"),
                Integer.parseInt(properties.getProperty("redis.port")),
                Integer.parseInt(properties.getProperty("redis.timeout-ms"))
        );
        context.setAttribute(ContextAttributes.JEDIS_POOL, jedisPool);

        context.setAttribute(ContextAttributes.JWT_SECRET, properties.getProperty("generated.jwt.secret"));
        context.setAttribute(ContextAttributes.JWT_EXPIRATION_MS, Long.parseLong(properties.getProperty("jwt.expiration-ms")));
        context.setAttribute(ContextAttributes.PRODUCT_CACHE_KEY, properties.getProperty("product.cache.key"));
        context.setAttribute(ContextAttributes.PRODUCT_CACHE_TTL, Integer.parseInt(properties.getProperty("product.cache.ttl.seconds")));
        context.setAttribute(ContextAttributes.PAYMENT_LOCK_TTL, Integer.parseInt(properties.getProperty("payment.lock.ttl.seconds")));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        Object dsAttr = context.getAttribute(ContextAttributes.DATA_SOURCE);
        if (dsAttr instanceof HikariDataSource) {
            ((HikariDataSource) dsAttr).close();
        }
        Object jedisAttr = context.getAttribute(ContextAttributes.JEDIS_POOL);
        if (jedisAttr instanceof JedisPool) {
            ((JedisPool) jedisAttr).close();
        }
    }
}

