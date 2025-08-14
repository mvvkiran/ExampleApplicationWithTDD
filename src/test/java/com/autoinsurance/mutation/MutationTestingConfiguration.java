package com.autoinsurance.mutation;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;

/**
 * Mutation Testing Configuration
 * 
 * This configuration class provides optimized settings for mutation testing
 * to improve performance and reduce flakiness during PIT execution.
 * 
 * Key Optimizations:
 * - Minimal Spring context for faster startup
 * - Mock external dependencies
 * - Simplified security configuration
 * - Reduced logging levels
 */
@TestConfiguration
@Profile("mutation")
@TestPropertySource(properties = {
    // Database Settings - Use H2 in-memory for speed
    "spring.datasource.url=jdbc:h2:mem:mutationdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",
    "spring.sql.init.mode=never",
    
    // Security Settings - Simplified for mutation testing
    "spring.security.enabled=true",
    "management.security.enabled=false",
    
    // Logging Settings - Minimal logging for performance
    "logging.level.org.springframework=WARN",
    "logging.level.com.autoinsurance=INFO",
    "logging.level.org.hibernate=WARN",
    "logging.level.org.h2=WARN",
    
    // Application Settings
    "spring.main.lazy-initialization=true",
    "spring.jpa.defer-datasource-initialization=true",
    "spring.jpa.open-in-view=false",
    
    // Test Configuration
    "spring.test.context.cache.maxSize=1"
})
public class MutationTestingConfiguration {

    /**
     * Simplified authentication manager for mutation testing
     */
    @Bean
    @Primary
    public AuthenticationManager authenticationManager() {
        // Use a simple testing authentication provider for mutation testing
        return new ProviderManager(new TestingAuthenticationProvider());
    }
}