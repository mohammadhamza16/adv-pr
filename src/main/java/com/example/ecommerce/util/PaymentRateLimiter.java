package com.example.ecommerce.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class PaymentRateLimiter {
    private final JedisPool jedisPool;
    private final int lockSeconds;

    public PaymentRateLimiter(JedisPool jedisPool, int lockSeconds) {
        this.jedisPool = jedisPool;
        this.lockSeconds = lockSeconds;
    }

    public boolean acquireLock(String orderKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            String lockKey = "payment:lock:" + orderKey;
            String result = jedis.set(lockKey, "locked", "NX", "EX", lockSeconds);
            return "OK".equals(result);
        }
    }
}
