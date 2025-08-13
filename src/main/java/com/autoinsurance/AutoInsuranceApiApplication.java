package com.autoinsurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main Spring Boot application class for Auto Insurance API.
 * 
 * This application provides comprehensive auto insurance services including:
 * - Quote generation and management
 * - Premium calculations with risk assessment
 * - Policy lifecycle management (planned)
 * - Claims processing (planned)
 * - Customer management (planned)
 * 
 * Built with Test Driven Development (TDD) methodology following Red-Green-Blue cycles.
 * 
 * API Documentation available at: http://localhost:8080/swagger-ui/index.html
 */
@SpringBootApplication
@EnableCaching
public class AutoInsuranceApiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AutoInsuranceApiApplication.class, args);
    }
}