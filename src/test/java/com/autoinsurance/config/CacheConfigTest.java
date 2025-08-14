package com.autoinsurance.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cache Configuration Tests")
class CacheConfigTest {

    private CacheConfig cacheConfig;

    @BeforeEach
    void setUp() {
        cacheConfig = new CacheConfig();
    }

    @Test
    @DisplayName("Should create cache manager with all expected caches")
    void should_CreateCacheManagerWithAllExpectedCaches() {
        // When
        CacheManager cacheManager = cacheConfig.cacheManager();
        
        // Then
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager).isInstanceOf(ConcurrentMapCacheManager.class);
        
        Collection<String> cacheNames = cacheManager.getCacheNames();
        assertThat(cacheNames).containsExactlyInAnyOrder(
            CacheConfig.RISK_CALCULATION_CACHE,
            CacheConfig.DISCOUNT_CALCULATION_CACHE,
            CacheConfig.QUOTE_CACHE,
            CacheConfig.VALIDATION_PATTERN_CACHE
        );
    }

    @Test
    @DisplayName("Should have correct cache name constants")
    void should_HaveCorrectCacheNameConstants() {
        // Then
        assertThat(CacheConfig.RISK_CALCULATION_CACHE).isEqualTo("riskCalculationCache");
        assertThat(CacheConfig.DISCOUNT_CALCULATION_CACHE).isEqualTo("discountCalculationCache");
        assertThat(CacheConfig.QUOTE_CACHE).isEqualTo("quoteCache");
        assertThat(CacheConfig.VALIDATION_PATTERN_CACHE).isEqualTo("validationPatternCache");
    }

    @Test
    @DisplayName("Should create cache manager that can retrieve individual caches")
    void should_CreateCacheManagerThatCanRetrieveIndividualCaches() {
        // Given
        CacheManager cacheManager = cacheConfig.cacheManager();
        
        // Then
        assertThat(cacheManager.getCache(CacheConfig.RISK_CALCULATION_CACHE)).isNotNull();
        assertThat(cacheManager.getCache(CacheConfig.DISCOUNT_CALCULATION_CACHE)).isNotNull();
        assertThat(cacheManager.getCache(CacheConfig.QUOTE_CACHE)).isNotNull();
        assertThat(cacheManager.getCache(CacheConfig.VALIDATION_PATTERN_CACHE)).isNotNull();
    }

    @Test
    @DisplayName("Should create cache manager customizer")
    void should_CreateCacheManagerCustomizer() {
        // When
        var customizer = cacheConfig.cacheManagerCustomizer();
        
        // Then
        assertThat(customizer).isNotNull();
    }

    @Test
    @DisplayName("Should apply cache manager customization correctly")
    void should_ApplyCacheManagerCustomizationCorrectly() {
        // Given
        ConcurrentMapCacheManager cacheManager = (ConcurrentMapCacheManager) cacheConfig.cacheManager();
        var customizer = cacheConfig.cacheManagerCustomizer();
        
        // When
        customizer.customize(cacheManager);
        
        // Then
        assertThat(cacheManager.isAllowNullValues()).isFalse();
    }

    @Test
    @DisplayName("Should create functional cache instances")
    void should_CreateFunctionalCacheInstances() {
        // Given
        CacheManager cacheManager = cacheConfig.cacheManager();
        var riskCache = cacheManager.getCache(CacheConfig.RISK_CALCULATION_CACHE);
        var discountCache = cacheManager.getCache(CacheConfig.DISCOUNT_CALCULATION_CACHE);
        
        // When
        riskCache.put("testKey", "testValue");
        discountCache.put("discountKey", 0.15);
        
        // Then
        assertThat(riskCache.get("testKey", String.class)).isEqualTo("testValue");
        assertThat(discountCache.get("discountKey", Double.class)).isEqualTo(0.15);
    }

    @Test
    @DisplayName("Should handle cache eviction properly")
    void should_HandleCacheEvictionProperly() {
        // Given
        CacheManager cacheManager = cacheConfig.cacheManager();
        var cache = cacheManager.getCache(CacheConfig.QUOTE_CACHE);
        
        // When
        cache.put("quote1", "Quote data");
        assertThat(cache.get("quote1", String.class)).isEqualTo("Quote data");
        
        cache.evict("quote1");
        
        // Then
        assertThat(cache.get("quote1")).isNull();
    }

    @Test
    @DisplayName("Should support cache clearing")
    void should_SupportCacheClearing() {
        // Given
        CacheManager cacheManager = cacheConfig.cacheManager();
        var cache = cacheManager.getCache(CacheConfig.VALIDATION_PATTERN_CACHE);
        
        // When
        cache.put("pattern1", "^[A-Z]+$");
        cache.put("pattern2", "^[0-9]+$");
        assertThat(cache.get("pattern1")).isNotNull();
        assertThat(cache.get("pattern2")).isNotNull();
        
        cache.clear();
        
        // Then
        assertThat(cache.get("pattern1")).isNull();
        assertThat(cache.get("pattern2")).isNull();
    }
}