package com.autoinsurance.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * External Service Configuration
 * 
 * Configures beans required for external service integration.
 * Supports service virtualization via WireMock during testing.
 * 
 * Following TDD Red-Green-Blue cycle:
 * RED: External service calls fail without proper configuration
 * GREEN: RestTemplate and ObjectMapper provide service integration
 * BLUE: Refactor configuration while maintaining service compatibility
 */
@Configuration
public class ExternalServiceConfig {

    /**
     * RestTemplate bean for external API calls
     * 
     * Configured for use with both real services and WireMock virtualization.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * ObjectMapper bean configured for external service DTOs
     * 
     * Includes JSR310 (Java Time) support for LocalDate serialization
     * and consistent date formatting for external API compatibility.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Add JSR310 module for LocalDate support
        mapper.registerModule(new JavaTimeModule());
        
        // Configure date serialization to strings instead of arrays
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Additional configurations for external service compatibility
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return mapper;
    }
}