package com.autoinsurance.contract.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Consumer contract test for Risk Assessment Service
 * 
 * This test defines the contract between our Auto Insurance API (consumer)
 * and an external Risk Assessment Service (provider).
 * 
 * Following TDD Red-Green-Blue cycle:
 * RED: Write failing test that defines expected interaction
 * GREEN: Implement minimal consumer code to make test pass
 * BLUE: Refactor and improve while keeping contract valid
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "risk-assessment-service")
class RiskAssessmentServiceConsumerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final RestTemplate restTemplate = new RestTemplate() {{
        setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            protected boolean hasError(org.springframework.http.HttpStatusCode statusCode) {
                return false; // Never treat any response as error for contract testing
            }
        });
    }};

    @Pact(consumer = "auto-insurance-api")
    public V4Pact riskAssessmentForYoungDriver(PactBuilder builder) {
        return builder
            .expectsToReceiveHttpInteraction("a request for risk assessment of a young driver", httpBuilder -> 
                httpBuilder
                    .withRequest(request -> 
                        request
                            .method("POST")
                            .path("/api/v1/risk-assessment")
                            .headers(Map.of("Content-Type", "application/json"))
                            .body("{"
                                + "\"driverAge\":22,"
                                + "\"vehicleAge\":5,"
                                + "\"yearsOfExperience\":3,"
                                + "\"vehicleValue\":25000.00"
                                + "}")
                    )
                    .willRespondWith(response -> 
                        response
                            .status(200)
                            .headers(Map.of("Content-Type", "application/json"))
                            .body("{"
                                + "\"riskScore\":7.5,"
                                + "\"riskCategory\":\"HIGH\","
                                + "\"riskFactors\":[\"Young driver\",\"Limited experience\"],"
                                + "\"baseMultiplier\":1.8"
                                + "}")
                    )
            )
            .toPact();
    }

    @Pact(consumer = "auto-insurance-api")
    public V4Pact riskAssessmentForExperiencedDriver(PactBuilder builder) {
        return builder
            .expectsToReceiveHttpInteraction("a request for risk assessment of an experienced driver", httpBuilder -> 
                httpBuilder
                    .withRequest(request -> 
                        request
                            .method("POST")
                            .path("/api/v1/risk-assessment")
                            .headers(Map.of("Content-Type", "application/json"))
                            .body("{"
                                + "\"driverAge\":35,"
                                + "\"vehicleAge\":3,"
                                + "\"yearsOfExperience\":15,"
                                + "\"vehicleValue\":40000.00"
                                + "}")
                    )
                    .willRespondWith(response -> 
                        response
                            .status(200)
                            .headers(Map.of("Content-Type", "application/json"))
                            .body("{"
                                + "\"riskScore\":3.2,"
                                + "\"riskCategory\":\"LOW\","
                                + "\"riskFactors\":[\"Experienced driver\",\"New vehicle\"],"
                                + "\"baseMultiplier\":0.9"
                                + "}")
                    )
            )
            .toPact();
    }

    @Pact(consumer = "auto-insurance-api")
    public V4Pact riskAssessmentValidationError(PactBuilder builder) {
        return builder
            .expectsToReceiveHttpInteraction("a request with invalid driver age", httpBuilder -> 
                httpBuilder
                    .withRequest(request -> 
                        request
                            .method("POST")
                            .path("/api/v1/risk-assessment")
                            .headers(Map.of("Content-Type", "application/json"))
                            .body("{"
                                + "\"driverAge\":16,"
                                + "\"vehicleAge\":10,"
                                + "\"yearsOfExperience\":0,"
                                + "\"vehicleValue\":15000.00"
                                + "}")
                    )
                    .willRespondWith(response -> 
                        response
                            .status(400)
                            .headers(Map.of("Content-Type", "application/json"))
                            .body("{"
                                + "\"error\":\"VALIDATION_ERROR\","
                                + "\"message\":\"Driver must be at least 18 years old\","
                                + "\"timestamp\":\"2024-08-14T10:30:00Z\""
                                + "}")
                    )
            )
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "riskAssessmentForYoungDriver")
    void shouldCalculateHighRiskForYoungDriver(MockServer mockServer) throws JsonProcessingException {
        // RED: Define the test for high-risk assessment
        
        // Arrange
        RiskAssessmentRequest request = new RiskAssessmentRequest(
            22, 5, 3, new BigDecimal("25000.00")
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
            objectMapper.writeValueAsString(request), 
            headers
        );
        
        // Act
        ResponseEntity<RiskAssessmentResponse> response = restTemplate.exchange(
            mockServer.getUrl() + "/api/v1/risk-assessment",
            HttpMethod.POST,
            entity,
            RiskAssessmentResponse.class
        );
        
        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRiskScore()).isEqualTo(7.5);
        assertThat(response.getBody().getRiskCategory()).isEqualTo("HIGH");
        assertThat(response.getBody().getBaseMultiplier()).isEqualTo(1.8);
        assertThat(response.getBody().getRiskFactors())
            .containsExactly("Young driver", "Limited experience");
    }

    @Test
    @PactTestFor(pactMethod = "riskAssessmentForExperiencedDriver")
    void shouldCalculateLowRiskForExperiencedDriver(MockServer mockServer) throws JsonProcessingException {
        // RED: Define the test for low-risk assessment
        
        // Arrange
        RiskAssessmentRequest request = new RiskAssessmentRequest(
            35, 3, 15, new BigDecimal("40000.00")
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
            objectMapper.writeValueAsString(request), 
            headers
        );
        
        // Act
        ResponseEntity<RiskAssessmentResponse> response = restTemplate.exchange(
            mockServer.getUrl() + "/api/v1/risk-assessment",
            HttpMethod.POST,
            entity,
            RiskAssessmentResponse.class
        );
        
        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRiskScore()).isEqualTo(3.2);
        assertThat(response.getBody().getRiskCategory()).isEqualTo("LOW");
        assertThat(response.getBody().getBaseMultiplier()).isEqualTo(0.9);
        assertThat(response.getBody().getRiskFactors())
            .containsExactly("Experienced driver", "New vehicle");
    }

    @Test
    @PactTestFor(pactMethod = "riskAssessmentValidationError")
    void shouldHandleValidationErrorForInvalidAge(MockServer mockServer) throws JsonProcessingException {
        // RED: Define the test for validation error handling
        
        // Arrange
        RiskAssessmentRequest request = new RiskAssessmentRequest(
            16, 10, 0, new BigDecimal("15000.00")
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
            objectMapper.writeValueAsString(request), 
            headers
        );
        
        // Act
        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            mockServer.getUrl() + "/api/v1/risk-assessment",
            HttpMethod.POST,
            entity,
            (Class<Map<String, Object>>) (Class<?>) Map.class
        );
        
        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().get("message")).isEqualTo("Driver must be at least 18 years old");
        assertThat(response.getBody().get("timestamp")).isNotNull();
    }

    // Data Transfer Objects for Risk Assessment
    public static class RiskAssessmentRequest {
        private int driverAge;
        private int vehicleAge;
        private int yearsOfExperience;
        private BigDecimal vehicleValue;

        public RiskAssessmentRequest() {}

        public RiskAssessmentRequest(int driverAge, int vehicleAge, int yearsOfExperience, BigDecimal vehicleValue) {
            this.driverAge = driverAge;
            this.vehicleAge = vehicleAge;
            this.yearsOfExperience = yearsOfExperience;
            this.vehicleValue = vehicleValue;
        }

        // Getters and setters
        public int getDriverAge() { return driverAge; }
        public void setDriverAge(int driverAge) { this.driverAge = driverAge; }

        public int getVehicleAge() { return vehicleAge; }
        public void setVehicleAge(int vehicleAge) { this.vehicleAge = vehicleAge; }

        public int getYearsOfExperience() { return yearsOfExperience; }
        public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

        public BigDecimal getVehicleValue() { return vehicleValue; }
        public void setVehicleValue(BigDecimal vehicleValue) { this.vehicleValue = vehicleValue; }
    }

    public static class RiskAssessmentResponse {
        private double riskScore;
        private String riskCategory;
        private String[] riskFactors;
        private double baseMultiplier;

        public RiskAssessmentResponse() {}

        // Getters and setters
        public double getRiskScore() { return riskScore; }
        public void setRiskScore(double riskScore) { this.riskScore = riskScore; }

        public String getRiskCategory() { return riskCategory; }
        public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }

        public String[] getRiskFactors() { return riskFactors; }
        public void setRiskFactors(String[] riskFactors) { this.riskFactors = riskFactors; }

        public double getBaseMultiplier() { return baseMultiplier; }
        public void setBaseMultiplier(double baseMultiplier) { this.baseMultiplier = baseMultiplier; }
    }
}