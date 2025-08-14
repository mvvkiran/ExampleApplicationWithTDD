package com.autoinsurance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Swagger Configuration Tests")
class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
    }

    @Test
    @DisplayName("Should create OpenAPI configuration with complete info")
    void should_CreateOpenApiConfigurationWithCompleteInfo() {
        // When
        OpenAPI openAPI = swaggerConfig.autoInsuranceAPI();

        // Then
        assertThat(openAPI).isNotNull();
        
        Info info = openAPI.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Auto Insurance API");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
        assertThat(info.getDescription()).isNotNull();
    }

    @Test
    @DisplayName("Should include comprehensive API description with TDD methodology")
    void should_IncludeComprehensiveApiDescriptionWithTddMethodology() {
        // When
        OpenAPI openAPI = swaggerConfig.autoInsuranceAPI();
        String description = openAPI.getInfo().getDescription();

        // Then
        assertThat(description).contains("Test Driven Development");
        assertThat(description).contains("TDD Methodology");
        assertThat(description).contains("🔴 Red Phase");
        assertThat(description).contains("🟢 Green Phase");
        assertThat(description).contains("🔵 Blue Phase");
        assertThat(description).contains("Write failing tests first");
        assertThat(description).contains("Implement minimum code to pass tests");
        assertThat(description).contains("Refactor for quality and performance");
    }

    @Test
    @DisplayName("Should include feature descriptions with emojis")
    void should_IncludeFeatureDescriptionsWithEmojis() {
        // When
        OpenAPI openAPI = swaggerConfig.autoInsuranceAPI();
        String description = openAPI.getInfo().getDescription();

        // Then
        assertThat(description).contains("🚗 Features");
        assertThat(description).contains("Quote Management");
        assertThat(description).contains("Premium Calculations");
        assertThat(description).contains("Policy Management");
        assertThat(description).contains("Claims Processing");
        assertThat(description).contains("Customer Management");
    }

    @Test
    @DisplayName("Should include testing coverage information")
    void should_IncludeTestingCoverageInformation() {
        // When
        OpenAPI openAPI = swaggerConfig.autoInsuranceAPI();
        String description = openAPI.getInfo().getDescription();

        // Then
        assertThat(description).contains("📊 Testing Coverage");
        assertThat(description).contains("Unit Tests**: 80%+ coverage");
        assertThat(description).contains("23 tests");
        assertThat(description).contains("Integration Tests");
        assertThat(description).contains("10 tests");
        assertThat(description).contains("Contract Tests");
        assertThat(description).contains("Performance Tests");
    }

    @Test
    @DisplayName("Should include performance features section")
    void should_IncludePerformanceFeaturesSection() {
        // When
        OpenAPI openAPI = swaggerConfig.autoInsuranceAPI();
        String description = openAPI.getInfo().getDescription();

        // Then
        assertThat(description).contains("⚡ Performance Features");
        assertThat(description).contains("Spring Cache integration");
        assertThat(description).contains("Optimized entity building");
        assertThat(description).contains("logging and monitoring");
    }

    @Test
    @DisplayName("Should include interactive testing instructions")
    void should_IncludeInteractiveTestingInstructions() {
        // When
        OpenAPI openAPI = swaggerConfig.autoInsuranceAPI();
        String description = openAPI.getInfo().getDescription();

        // Then
        assertThat(description).contains("🔧 Interactive Testing");
        assertThat(description).contains("Try it out");
        assertThat(description).contains("test all endpoints");
    }

    @Test
    @DisplayName("Should configure contact information correctly")
    void should_ConfigureContactInformationCorrectly() {
        // When
        OpenAPI openAPI = swaggerConfig.autoInsuranceAPI();
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
        OpenAPI openAPI = swaggerConfig.autoInsuranceAPI();
        License license = openAPI.getInfo().getLicense();

        // Then
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("MIT License");
        assertThat(license.getUrl()).isEqualTo("https://opensource.org/licenses/MIT");
    }

    @Test
    @DisplayName("Should configure servers with emojis and descriptions")
    void should_ConfigureServersWithEmojisAndDescriptions() {
        // When
        OpenAPI openAPI = swaggerConfig.autoInsuranceAPI();
        List<Server> servers = openAPI.getServers();

        // Then
        assertThat(servers).hasSize(3);

        Server devServer = servers.get(0);
        assertThat(devServer.getUrl()).isEqualTo("http://localhost:8080");
        assertThat(devServer.getDescription()).isEqualTo("🛠️ Development Server");

        Server stagingServer = servers.get(1);
        assertThat(stagingServer.getUrl()).isEqualTo("https://api-staging.autoinsurance.com");
        assertThat(stagingServer.getDescription()).isEqualTo("🧪 Staging Server");

        Server prodServer = servers.get(2);
        assertThat(prodServer.getUrl()).isEqualTo("https://api.autoinsurance.com");
        assertThat(prodServer.getDescription()).isEqualTo("🚀 Production Server");
    }

    @Test
    @DisplayName("Should create quote management API group")
    void should_CreateQuoteManagementApiGroup() {
        // When
        GroupedOpenApi quoteApi = swaggerConfig.quoteManagementApi();

        // Then
        assertThat(quoteApi).isNotNull();
        assertThat(quoteApi.getGroup()).isEqualTo("quote-management");
        assertThat(quoteApi.getDisplayName()).isEqualTo("Quote Management APIs");
        assertThat(quoteApi.getPathsToMatch()).contains("/api/v1/quotes/**");
    }

    @Test
    @DisplayName("Should create all APIs group")
    void should_CreateAllApisGroup() {
        // When
        GroupedOpenApi allApis = swaggerConfig.allApis();

        // Then
        assertThat(allApis).isNotNull();
        assertThat(allApis.getGroup()).isEqualTo("all-apis");
        assertThat(allApis.getDisplayName()).isEqualTo("All Auto Insurance APIs");
        assertThat(allApis.getPathsToMatch()).contains("/api/**");
    }

    @Test
    @DisplayName("Should create consistent OpenAPI instances")
    void should_CreateConsistentOpenApiInstances() {
        // When
        OpenAPI openAPI1 = swaggerConfig.autoInsuranceAPI();
        OpenAPI openAPI2 = swaggerConfig.autoInsuranceAPI();

        // Then - Should have same content (not necessarily same object)
        assertThat(openAPI1.getInfo().getTitle()).isEqualTo(openAPI2.getInfo().getTitle());
        assertThat(openAPI1.getInfo().getVersion()).isEqualTo(openAPI2.getInfo().getVersion());
        assertThat(openAPI1.getServers().size()).isEqualTo(openAPI2.getServers().size());
    }

    @Test
    @DisplayName("Should create distinct grouped API instances")
    void should_CreateDistinctGroupedApiInstances() {
        // When
        GroupedOpenApi quoteApi = swaggerConfig.quoteManagementApi();
        GroupedOpenApi allApis = swaggerConfig.allApis();

        // Then
        assertThat(quoteApi.getGroup()).isNotEqualTo(allApis.getGroup());
        assertThat(quoteApi.getDisplayName()).isNotEqualTo(allApis.getDisplayName());
        assertThat(quoteApi.getPathsToMatch()).isNotEqualTo(allApis.getPathsToMatch());
    }
}