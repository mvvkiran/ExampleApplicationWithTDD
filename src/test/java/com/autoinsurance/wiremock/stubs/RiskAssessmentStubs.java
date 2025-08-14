package com.autoinsurance.wiremock.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * WireMock Stubs for Risk Assessment Service
 * 
 * Provides pre-configured stubs based on Pact contract specifications.
 * Supports TDD Red-Green-Blue cycle for external service virtualization.
 * 
 * RED: Tests fail without proper risk assessment mocking
 * GREEN: WireMock stubs provide predictable responses
 * BLUE: Refactor stubs while maintaining contract compliance
 */
public class RiskAssessmentStubs {

    /**
     * Stub for young driver with high risk assessment
     * Based on Pact contract: "a request for risk assessment of a young driver"
     */
    public static void stubHighRiskYoungDriver(WireMockServer wireMockServer) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/risk-assessment"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(matchingJsonPath("$.driverAge", equalTo("22")))
            .withRequestBody(matchingJsonPath("$.vehicleAge", equalTo("5")))
            .withRequestBody(matchingJsonPath("$.yearsOfExperience", equalTo("3")))
            .withRequestBody(matchingJsonPath("$.vehicleValue", equalTo("25000.0")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                    "  \"riskScore\": 7.5,\n" +
                    "  \"riskCategory\": \"HIGH\",\n" +
                    "  \"riskFactors\": [\"Young driver\", \"Limited experience\"],\n" +
                    "  \"baseMultiplier\": 1.8\n" +
                    "}")));
    }

    /**
     * Stub for experienced driver with low risk assessment
     * Based on Pact contract: "a request for risk assessment of an experienced driver"
     */
    public static void stubLowRiskExperiencedDriver(WireMockServer wireMockServer) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/risk-assessment"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(matchingJsonPath("$.driverAge", equalTo("35")))
            .withRequestBody(matchingJsonPath("$.vehicleAge", equalTo("3")))
            .withRequestBody(matchingJsonPath("$.yearsOfExperience", equalTo("15")))
            .withRequestBody(matchingJsonPath("$.vehicleValue", equalTo("40000.0")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                    "  \"riskScore\": 3.2,\n" +
                    "  \"riskCategory\": \"LOW\",\n" +
                    "  \"riskFactors\": [\"Experienced driver\", \"New vehicle\"],\n" +
                    "  \"baseMultiplier\": 0.9\n" +
                    "}")));
    }

    /**
     * Stub for validation error with invalid driver age
     * Based on Pact contract: "a request with invalid driver age"
     */
    public static void stubValidationErrorInvalidAge(WireMockServer wireMockServer) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/risk-assessment"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(matchingJsonPath("$.driverAge", equalTo("16")))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                    "  \"error\": \"VALIDATION_ERROR\",\n" +
                    "  \"message\": \"Driver must be at least 18 years old\",\n" +
                    "  \"timestamp\": \"2024-08-14T10:30:00Z\"\n" +
                    "}")));
    }

    /**
     * Generic stub for any risk assessment request
     * Provides default medium risk response for unmatched requests
     */
    public static void stubDefaultRiskAssessment(WireMockServer wireMockServer) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/risk-assessment"))
            .withHeader("Content-Type", equalTo("application/json"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                    "  \"riskScore\": 5.0,\n" +
                    "  \"riskCategory\": \"MEDIUM\",\n" +
                    "  \"riskFactors\": [\"Standard driver\"],\n" +
                    "  \"baseMultiplier\": 1.0\n" +
                    "}")));
    }

    /**
     * Stub for service unavailable scenario
     * Used for testing error handling and resilience
     */
    public static void stubServiceUnavailable(WireMockServer wireMockServer) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/risk-assessment"))
            .willReturn(aResponse()
                .withStatus(503)
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                    "  \"error\": \"SERVICE_UNAVAILABLE\",\n" +
                    "  \"message\": \"Risk assessment service is temporarily unavailable\"\n" +
                    "}")));
    }
}