package com.autoinsurance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) configuration for Auto Insurance API documentation.
 * Provides comprehensive API documentation with security schemes and server configurations.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI autoInsuranceOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServerList())
                .components(createComponents())
                .addSecurityItem(createSecurityRequirement());
    }

    private Info createApiInfo() {
        return new Info()
                .title("Auto Insurance API")
                .description("""
                    Comprehensive Auto Insurance Backend API built with Test Driven Development (TDD).
                    
                    ## Features
                    - **Quote Management**: Generate, retrieve, and calculate insurance quotes
                    - **Premium Calculations**: Advanced risk assessment and discount calculations
                    - **Policy Management**: Complete policy lifecycle management (Coming Soon)
                    - **Claims Processing**: Streamlined claims workflow (Coming Soon)
                    - **Customer Management**: Customer profile and preference management (Coming Soon)
                    
                    ## TDD Methodology
                    This API is built following strict Test Driven Development practices:
                    - **Red Phase**: Write failing tests first
                    - **Green Phase**: Implement minimum code to pass tests
                    - **Blue Phase**: Refactor for quality and performance
                    
                    ## Testing Coverage
                    - Unit Tests: 80%+ coverage
                    - Integration Tests: All endpoints covered
                    - Contract Tests: External service agreements
                    - Performance Tests: Load and stress testing
                    
                    ## Performance Features
                    - Spring Cache integration for frequently accessed data
                    - Optimized entity building with performance-focused patterns
                    - Comprehensive logging and monitoring
                    """)
                .version("1.0.0")
                .contact(createContact())
                .license(createLicense());
    }

    private Contact createContact() {
        return new Contact()
                .name("Auto Insurance API Team")
                .email("api-team@autoinsurance.com")
                .url("https://github.com/autoinsurance/api");
    }

    private License createLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    private List<Server> createServerList() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Development Server"),
                new Server()
                        .url("https://api-staging.autoinsurance.com")
                        .description("Staging Server"),
                new Server()
                        .url("https://api.autoinsurance.com")
                        .description("Production Server")
        );
    }

    private Components createComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", createBearerAuthScheme())
                .addSecuritySchemes("apiKey", createApiKeyScheme());
    }

    private SecurityScheme createBearerAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer token authentication");
    }

    private SecurityScheme createApiKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-API-Key")
                .description("API Key authentication for service-to-service calls");
    }

    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement()
                .addList("bearerAuth")
                .addList("apiKey");
    }
}