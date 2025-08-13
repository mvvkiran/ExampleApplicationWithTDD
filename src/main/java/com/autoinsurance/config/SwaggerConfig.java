package com.autoinsurance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration for Auto Insurance API.
 * This configuration ensures proper API documentation display in Swagger UI.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    @Primary
    public OpenAPI autoInsuranceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auto Insurance API")
                        .description("""
                            **Comprehensive Auto Insurance Backend API built with Test Driven Development (TDD)**
                            
                            ## 🚗 Features
                            - **Quote Management**: Generate, retrieve, and calculate insurance quotes
                            - **Premium Calculations**: Advanced risk assessment with discount calculations
                            - **Policy Management**: Complete policy lifecycle management (Coming Soon)
                            - **Claims Processing**: Streamlined claims workflow (Coming Soon)
                            - **Customer Management**: Profile and preference management (Coming Soon)
                            
                            ## 🔴🟢🔵 TDD Methodology
                            This API follows strict Test Driven Development practices:
                            - **🔴 Red Phase**: Write failing tests first
                            - **🟢 Green Phase**: Implement minimum code to pass tests  
                            - **🔵 Blue Phase**: Refactor for quality and performance
                            
                            ## 📊 Testing Coverage
                            - **Unit Tests**: 80%+ coverage (23 tests)
                            - **Integration Tests**: All endpoints covered (10 tests)
                            - **Contract Tests**: External service agreements
                            - **Performance Tests**: Load and stress testing
                            
                            ## ⚡ Performance Features
                            - Spring Cache integration for frequently accessed data
                            - Optimized entity building with performance-focused patterns
                            - Comprehensive logging and monitoring
                            
                            ## 🔧 Interactive Testing
                            Use the **"Try it out"** buttons below to test all endpoints directly from this interface!
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Auto Insurance API Team")
                                .email("api-team@autoinsurance.com")
                                .url("https://github.com/autoinsurance/api"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("🛠️ Development Server"),
                        new Server()
                                .url("https://api-staging.autoinsurance.com")
                                .description("🧪 Staging Server"),
                        new Server()
                                .url("https://api.autoinsurance.com")
                                .description("🚀 Production Server")
                ));
    }

    @Bean
    public GroupedOpenApi quoteManagementApi() {
        return GroupedOpenApi.builder()
                .group("quote-management")
                .displayName("Quote Management APIs")
                .pathsToMatch("/api/v1/quotes/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all-apis")
                .displayName("All Auto Insurance APIs")
                .pathsToMatch("/api/**")
                .build();
    }
}