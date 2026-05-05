package com.example.ecommerce.service;

import com.example.ecommerce.repository.ResetRepository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class ResetService {
    private final ResetRepository resetRepository;
    private final JedisPool jedisPool;
    private final String productCacheKey;

    public ResetService(ResetRepository resetRepository, JedisPool jedisPool, String productCacheKey) {
        this.resetRepository = resetRepository;
        this.jedisPool = jedisPool;
        this.productCacheKey = productCacheKey;
    }

    public void resetAll() throws Exception {
        resetRepository.resetAll();
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(productCacheKey);
            var cursor = "0";
            do {
                var scanResult = jedis.scan(cursor, "MATCH", "payment:lock:*", "COUNT", "100");
                cursor = scanResult.getCursor();
                if (!scanResult.getResult().isEmpty()) {
                    jedis.del(scanResult.getResult().toArray(new String[0]));
                }
            } while (!cursor.equals("0"));
        }
    }
}
