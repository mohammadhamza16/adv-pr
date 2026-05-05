package com.example.ecommerce.config;

public class ContextAttributes {
    public static final String DATA_SOURCE = "app.datasource";
    public static final String JEDIS_POOL = "app.jedisPool";
    public static final String JWT_SECRET = "app.jwtSecret";
    public static final String JWT_EXPIRATION_MS = "app.jwtExpirationMs";
    public static final String PRODUCT_CACHE_KEY = "app.productCacheKey";
    public static final String PRODUCT_CACHE_TTL = "app.productCacheTtl";
    public static final String PAYMENT_LOCK_TTL = "app.paymentLockTtl";
}
