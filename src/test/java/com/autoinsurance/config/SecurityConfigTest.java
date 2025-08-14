package com.autoinsurance.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb_security",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("Security Configuration Integration Tests")
class SecurityConfigTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    @DisplayName("Should allow access to actuator health endpoint without authentication")
    void should_AllowAccessToHealthEndpoint() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/actuator/health", String.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should allow access to Swagger UI without authentication")
    void should_AllowAccessToSwaggerUi() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/swagger-ui/index.html", String.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should allow access to API documentation without authentication")
    void should_AllowAccessToApiDocumentation() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/v3/api-docs", String.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should allow access to quote API endpoints without authentication")
    void should_AllowAccessToQuoteApiEndpoints() {
        // When - GET quotes (should return empty list or error for invalid ID)
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/quotes", String.class);
        
        // Then - Should not be forbidden (403), could be 200, 404, or 400
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should not require CSRF token for API requests")
    void should_NotRequireCsrfTokenForApiRequests() {
        // Given - A POST request without CSRF token
        String quoteRequestJson = """
            {
                "vehicle": {
                    "vin": "1HGBH41JXMN109186",
                    "make": "Honda",
                    "model": "Accord",
                    "year": 2020,
                    "currentValue": 25000
                },
                "drivers": [{
                    "firstName": "John",
                    "lastName": "Doe",
                    "dateOfBirth": "1990-01-01",
                    "licenseNumber": "D123456789",
                    "licenseState": "CA"
                }],
                "coverageAmount": 100000,
                "deductible": 500
            }
            """;
        
        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/quotes",
            quoteRequestJson,
            String.class
        );
        
        // Then - Should not fail due to missing CSRF token
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.FORBIDDEN);
        // Could be 400 (bad request), 415 (unsupported media type), etc., but not 403
    }

    @Test
    @DisplayName("Should allow access to non-existent endpoints (not return 403)")
    void should_AllowAccessToNonExistentEndpoints() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/non-existent-endpoint", String.class);
        
        // Then - Should not return 403 (forbidden), could be 404 or 500
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should allow access to error endpoint")
    void should_AllowAccessToErrorEndpoint() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/error", String.class);
        
        // Then
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }
}