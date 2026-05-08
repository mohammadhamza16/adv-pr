package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.dao.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductRepository productRepository;
    private final JedisPool jedisPool;
    private final String cacheKey;
    private final int cacheTtlSeconds;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductService(ProductRepository productRepository, JedisPool jedisPool, String cacheKey, int cacheTtlSeconds) {
        this.productRepository = productRepository;
        this.jedisPool = jedisPool;
        this.cacheKey = cacheKey;
        this.cacheTtlSeconds = cacheTtlSeconds;
    }

    public List<Product> findAll() throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String payload = jedis.get(cacheKey);
            if (payload != null) {
                return objectMapper.readValue(payload, new TypeReference<>() {
                });
            }
            List<Product> products = productRepository.findAll();
            jedis.setex(cacheKey, cacheTtlSeconds, objectMapper.writeValueAsString(products));
            return products;
        }
    }

    public Product findById(long id) throws SQLException {
        return productRepository.findById(id);
    }

    public Product create(Product product) throws SQLException {
        Product created = productRepository.create(product);
        invalidateCache();
        return created;
    }

    public boolean update(Product product) throws SQLException {
        boolean updated = productRepository.update(product);
        if (updated) {
            invalidateCache();
        }
        return updated;
    }

    public boolean delete(long id) throws SQLException {
        boolean removed = productRepository.delete(id);
        if (removed) {
            invalidateCache();
        }
        return removed;
    }

    private void invalidateCache() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(cacheKey);
        }
    }
}

