package com.autoinsurance.config;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Cache configuration for performance optimization.
 * Enables caching for frequently accessed data and calculations.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache names used throughout the application.
     */
    public static final String RISK_CALCULATION_CACHE = "riskCalculationCache";
    public static final String DISCOUNT_CALCULATION_CACHE = "discountCalculationCache";
    public static final String QUOTE_CACHE = "quoteCache";
    public static final String VALIDATION_PATTERN_CACHE = "validationPatternCache";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                RISK_CALCULATION_CACHE,
                DISCOUNT_CALCULATION_CACHE,
                QUOTE_CACHE,
                VALIDATION_PATTERN_CACHE
        );
    }

    @Bean
    public CacheManagerCustomizer<ConcurrentMapCacheManager> cacheManagerCustomizer() {
        return cacheManager -> {
            // Configure cache settings if needed
            cacheManager.setAllowNullValues(false);
        };
    }
}