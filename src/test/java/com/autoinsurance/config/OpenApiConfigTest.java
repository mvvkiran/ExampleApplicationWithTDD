package com.autoinsurance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OpenAPI Configuration Tests")
class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    @DisplayName("Should create OpenAPI configuration with all required components")
    void should_CreateOpenApiConfigurationWithAllRequiredComponents() {
        // When
        OpenAPI openAPI = openApiConfig.autoInsuranceOpenAPI();

        // Then
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getServers()).isNotNull();
        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getSecurity()).isNotNull();
    }

    @Test
    @DisplayName("Should configure API info correctly")
    void should_ConfigureApiInfoCorrectly() {
        // When
        OpenAPI openAPI = openApiConfig.autoInsuranceOpenAPI();
        Info info = openAPI.getInfo();

        // Then
        assertThat(info.getTitle()).isEqualTo("Auto Insurance API");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
        assertThat(info.getDescription()).contains("Comprehensive Auto Insurance Backend API");
        assertThat(info.getDescription()).contains("Test Driven Development");
        assertThat(info.getDescription()).contains("TDD Methodology");
        assertThat(info.getDescription()).contains("Red Phase");
        assertThat(info.getDescription()).contains("Green Phase");
        assertThat(info.getDescription()).contains("Blue Phase");
    }

    @Test
    @DisplayName("Should configure contact information correctly")
    void should_ConfigureContactInformationCorrectly() {
        // When
        OpenAPI openAPI = openApiConfig.autoInsuranceOpenAPI();
        Contact contact = openAPI.getInfo().getContact();

        // Then
        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("Auto Insurance API Team");
        assertThat(contact.getEmail()).isEqualTo("api-team@autoinsurance.com");
        assertThat(contact.getUrl()).isEqualTo("https://github.com/autoinsurance/api");
    }

    @Test
    @DisplayName("Should configure license information correctly")
    void should_ConfigureLicenseInformationCorrectly() {
        // When
        OpenAPI openAPI = openApiConfig.autoInsuranceOpenAPI();
        License license = openAPI.getInfo().getLicense();

        // Then
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("MIT License");
        assertThat(license.getUrl()).isEqualTo("https://opensource.org/licenses/MIT");
    }

    @Test
    @DisplayName("Should configure servers correctly")
    void should_ConfigureServersCorrectly() {
        // When
        OpenAPI openAPI = openApiConfig.autoInsuranceOpenAPI();
        List<Server> servers = openAPI.getServers();

        // Then
        assertThat(servers).hasSize(3);

        Server devServer = servers.get(0);
        assertThat(devServer.getUrl()).isEqualTo("http://localhost:8080");
        assertThat(devServer.getDescription()).isEqualTo("Development Server");

        Server stagingServer = servers.get(1);
        assertThat(stagingServer.getUrl()).isEqualTo("https://api-staging.autoinsurance.com");
        assertThat(stagingServer.getDescription()).isEqualTo("Staging Server");

        Server prodServer = servers.get(2);
        assertThat(prodServer.getUrl()).isEqualTo("https://api.autoinsurance.com");
        assertThat(prodServer.getDescription()).isEqualTo("Production Server");
    }

    @Test
    @DisplayName("Should configure security schemes correctly")
    void should_ConfigureSecuritySchemesCorrectly() {
        // When
        OpenAPI openAPI = openApiConfig.autoInsuranceOpenAPI();
        var components = openAPI.getComponents();

        // Then
        assertThat(components.getSecuritySchemes()).containsKeys("bearerAuth", "apiKey");

        SecurityScheme bearerAuth = components.getSecuritySchemes().get("bearerAuth");
        assertThat(bearerAuth.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(bearerAuth.getScheme()).isEqualTo("bearer");
        assertThat(bearerAuth.getBearerFormat()).isEqualTo("JWT");
        assertThat(bearerAuth.getDescription()).isEqualTo("JWT Bearer token authentication");

        SecurityScheme apiKey = components.getSecuritySchemes().get("apiKey");
        assertThat(apiKey.getType()).isEqualTo(SecurityScheme.Type.APIKEY);
        assertThat(apiKey.getIn()).isEqualTo(SecurityScheme.In.HEADER);
        assertThat(apiKey.getName()).isEqualTo("X-API-Key");
        assertThat(apiKey.getDescription()).isEqualTo("API Key authentication for service-to-service calls");
    }

    @Test
    @DisplayName("Should configure security requirements correctly")
    void should_ConfigureSecurityRequirementsCorrectly() {
        // When
        OpenAPI openAPI = openApiConfig.autoInsuranceOpenAPI();
        var securityRequirements = openAPI.getSecurity();

        // Then
        assertThat(securityRequirements).hasSize(1);
        var securityRequirement = securityRequirements.get(0);
        assertThat(securityRequirement).containsKeys("bearerAuth", "apiKey");
    }

    @Test
    @DisplayName("Should include comprehensive API description with features")
    void should_IncludeComprehensiveApiDescriptionWithFeatures() {
        // When
        OpenAPI openAPI = openApiConfig.autoInsuranceOpenAPI();
        String description = openAPI.getInfo().getDescription();

        // Then
        assertThat(description).contains("Quote Management");
        assertThat(description).contains("Premium Calculations");
        assertThat(description).contains("Policy Management");
        assertThat(description).contains("Claims Processing");
        assertThat(description).contains("Customer Management");
    }

    @Test
    @DisplayName("Should include testing coverage information in description")
    void should_IncludeTestingCoverageInformationInDescription() {
        // When
        OpenAPI openAPI = openApiConfig.autoInsuranceOpenAPI();
        String description = openAPI.getInfo().getDescription();

        // Then
        assertThat(description).contains("Testing Coverage");
        assertThat(description).contains("Unit Tests: 80%+ coverage");
        assertThat(description).contains("Integration Tests");
        assertThat(description).contains("Contract Tests");
        assertThat(description).contains("Performance Tests");
    }

    @Test
    @DisplayName("Should include performance features in description")
    void should_IncludePerformanceFeaturesInDescription() {
        // When
        OpenAPI openAPI = openApiConfig.autoInsuranceOpenAPI();
        String description = openAPI.getInfo().getDescription();

        // Then
        assertThat(description).contains("Performance Features");
        assertThat(description).contains("Spring Cache");
        assertThat(description).contains("Optimized entity building");
        assertThat(description).contains("logging and monitoring");
    }

    @Test
    @DisplayName("Should maintain consistent configuration structure")
    void should_MaintainConsistentConfigurationStructure() {
        // When
        OpenAPI openAPI1 = openApiConfig.autoInsuranceOpenAPI();
        OpenAPI openAPI2 = openApiConfig.autoInsuranceOpenAPI();

        // Then - Should create consistent configurations (not same object but same content)
        assertThat(openAPI1.getInfo().getTitle()).isEqualTo(openAPI2.getInfo().getTitle());
        assertThat(openAPI1.getServers().size()).isEqualTo(openAPI2.getServers().size());
        assertThat(openAPI1.getComponents().getSecuritySchemes().keySet())
                .isEqualTo(openAPI2.getComponents().getSecuritySchemes().keySet());
    }
}