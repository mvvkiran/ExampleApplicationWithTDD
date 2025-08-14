package com.autoinsurance.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.web.SecurityFilterChain;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Configuration Beans Test Suite
 * 
 * Following TDD Red-Green-Blue methodology:
 * RED: Tests fail when configuration beans return null or aren't properly configured
 * GREEN: Configuration beans are properly instantiated and configured
 * BLUE: Optimize configuration for performance and maintainability
 * 
 * Tests Configuration Bean Creation:
 * - CacheConfig bean creation and configuration
 * - SecurityConfig filter chain creation
 * - ExternalServiceConfig RestTemplate and ObjectMapper creation
 * - Bean null return mutations (addressing lines 21, 29, 31, 39, 53 mutations)
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb_config",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false"
})
@DisplayName("Configuration Beans Tests")
class ConfigurationBeansTest {

    @Nested
    @DisplayName("Cache Configuration Tests")
    class CacheConfigurationTests {

        @Autowired
        private CacheManager cacheManager;

        @Test
        @DisplayName("Should create non-null cache manager bean - line 29 mutation")
        void should_CreateNonNullCacheManagerBean() {
            // Given & When - Spring context loads and injects CacheManager
            
            // Then - CacheManager should not be null (tests NullReturnValsMutator on line 29)
            assertThat(cacheManager).isNotNull();
            
            // Verify it's properly configured
            assertThat(cacheManager.getCacheNames()).isNotNull();
        }

        @Test
        @DisplayName("Should create functional cache operations")
        void should_CreateFunctionalCacheOperations() {
            // Given - CacheManager is configured
            
            // When - We try to get or create caches using actual cache names
            var riskCalculationCache = cacheManager.getCache("riskCalculationCache");
            var validationPatternCache = cacheManager.getCache("validationPatternCache");
            var quoteCache = cacheManager.getCache("quoteCache");
            var discountCalculationCache = cacheManager.getCache("discountCalculationCache");
            
            // Then - Caches should be accessible and functional
            assertThat(riskCalculationCache).isNotNull();
            assertThat(validationPatternCache).isNotNull();
            assertThat(quoteCache).isNotNull();
            assertThat(discountCalculationCache).isNotNull();
            
            // Test cache operations
            validationPatternCache.put("test", "value");
            assertThat(validationPatternCache.get("test")).isNotNull();
            assertThat(validationPatternCache.get("test").get()).isEqualTo("value");
        }
    }

    @Nested
    @DisplayName("Security Configuration Tests")
    class SecurityConfigurationTests {

        @Autowired
        private SecurityFilterChain filterChain;

        @Test
        @DisplayName("Should create non-null security filter chain - line 21 mutation")
        void should_CreateNonNullSecurityFilterChain() {
            // Given & When - Spring Security context loads
            
            // Then - SecurityFilterChain should not be null (tests NullReturnValsMutator on line 21)
            assertThat(filterChain).isNotNull();
            
            // Verify it has the expected configuration
            assertThat(filterChain.getFilters()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("External Service Configuration Tests")
    class ExternalServiceConfigurationTests {

        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("Should create non-null RestTemplate bean - line 31 mutation")
        void should_CreateNonNullRestTemplateBean() {
            // Given & When - Spring context loads and injects RestTemplate
            
            // Then - RestTemplate should not be null (tests NullReturnValsMutator on line 31)
            assertThat(restTemplate).isNotNull();
            
            // Verify it's properly configured
            assertThat(restTemplate.getInterceptors()).isNotNull();
            assertThat(restTemplate.getErrorHandler()).isNotNull();
        }

        @Test
        @DisplayName("Should create non-null ObjectMapper bean - line 53 mutation")
        void should_CreateNonNullObjectMapperBean() {
            // Given & When - Spring context loads and injects ObjectMapper
            
            // Then - ObjectMapper should not be null (tests NullReturnValsMutator on line 53)
            assertThat(objectMapper).isNotNull();
            
            // Verify it's properly configured for JSON processing
            assertThat(objectMapper.getFactory()).isNotNull();
            assertThat(objectMapper.getSerializationConfig()).isNotNull();
            assertThat(objectMapper.getDeserializationConfig()).isNotNull();
        }

        @Test
        @DisplayName("Should configure ObjectMapper with proper settings")
        void should_ConfigureObjectMapperWithProperSettings() {
            // Given - ObjectMapper is configured
            
            // When - We test JSON serialization/deserialization
            var testObject = new TestData("test", 123);
            
            // Then - ObjectMapper should handle JSON operations correctly
            assertThat(objectMapper).satisfies(mapper -> {
                try {
                    String json = mapper.writeValueAsString(testObject);
                    assertThat(json).isNotNull().contains("test").contains("123");
                    
                    TestData deserializedObject = mapper.readValue(json, TestData.class);
                    assertThat(deserializedObject.name).isEqualTo("test");
                    assertThat(deserializedObject.value).isEqualTo(123);
                } catch (Exception e) {
                    throw new AssertionError("ObjectMapper should handle JSON operations", e);
                }
            });
        }
    }

    /**
     * Simple test data class for ObjectMapper testing
     */
    public static class TestData {
        public String name;
        public int value;

        public TestData() {} // Default constructor for Jackson

        public TestData(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}