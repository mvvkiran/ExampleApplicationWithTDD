package com.autoinsurance.wiremock.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * WireMock Stubs for Credit Check Service
 * 
 * Provides pre-configured stubs based on Pact contract specifications.
 * Supports TDD Red-Green-Blue cycle for external service virtualization.
 * 
 * RED: Tests fail without proper credit check mocking
 * GREEN: WireMock stubs provide predictable responses
 * BLUE: Refactor stubs while maintaining contract compliance
 */
public class CreditCheckStubs {

    /**
     * Stub for excellent credit check with discount eligibility
     * Based on Pact contract: "a request for credit check with excellent credit"
     */
    public static void stubExcellentCreditScore(WireMockServer wireMockServer) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/credit-check"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withHeader("Authorization", equalTo("Bearer test-token"))
            .withRequestBody(matchingJsonPath("$.firstName", equalTo("John")))
            .withRequestBody(matchingJsonPath("$.lastName", equalTo("Smith")))
            .withRequestBody(matchingJsonPath("$.ssn", equalTo("123-45-6789")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                    "  \"creditScore\": 785,\n" +
                    "  \"creditTier\": \"EXCELLENT\",\n" +
                    "  \"discountEligible\": true,\n" +
                    "  \"discountPercentage\": 15,\n" +
                    "  \"reportDate\": \"2024-08-14\"\n" +
                    "}")));
    }

    /**
     * Stub for fair credit check without discount eligibility
     * Based on Pact contract: "a request for credit check with fair credit"
     */
    public static void stubFairCreditScore(WireMockServer wireMockServer) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/credit-check"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withHeader("Authorization", equalTo("Bearer test-token"))
            .withRequestBody(matchingJsonPath("$.firstName", equalTo("Jane")))
            .withRequestBody(matchingJsonPath("$.lastName", equalTo("Doe")))
            .withRequestBody(matchingJsonPath("$.ssn", equalTo("987-65-4321")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                    "  \"creditScore\": 620,\n" +
                    "  \"creditTier\": \"FAIR\",\n" +
                    "  \"discountEligible\": false,\n" +
                    "  \"discountPercentage\": 0,\n" +
                    "  \"reportDate\": \"2024-08-14\"\n" +
                    "}")));
    }

    /**
     * Stub for invalid SSN format validation error
     * Based on Pact contract: "a request with invalid SSN format"
     */
    public static void stubInvalidSSNFormat(WireMockServer wireMockServer) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/credit-check"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withHeader("Authorization", equalTo("Bearer test-token"))
            .withRequestBody(matchingJsonPath("$.ssn", equalTo("invalid-ssn")))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                    "  \"error\": \"VALIDATION_ERROR\",\n" +
                    "  \"message\": \"Invalid SSN format. Expected format: XXX-XX-XXXX\",\n" +
                    "  \"timestamp\": \"2024-08-14T10:30:00Z\"\n" +
                    "}")));
    }

    /**
     * Generic stub for any credit check request
     * Provides default good credit response for unmatched requests
     */
    public static void stubDefaultCreditCheck(WireMockServer wireMockServer) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/credit-check"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withHeader("Authorization", matching("Bearer .*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                    "  \"creditScore\": 700,\n" +
                    "  \"creditTier\": \"GOOD\",\n" +
                    "  \"discountEligible\": true,\n" +
                    "  \"discountPercentage\": 10,\n" +
                    "  \"reportDate\": \"2024-08-14\"\n" +
                    "}")));
    }

    /**
     * Stub for unauthorized access scenario
     * Used for testing authentication and error handling
     */
    public static void stubUnauthorizedAccess(WireMockServer wireMockServer) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/credit-check"))
            .withHeader("Authorization", equalTo("Bearer invalid-token"))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                    "  \"error\": \"UNAUTHORIZED\",\n" +
                    "  \"message\": \"Invalid or expired authorization token\",\n" +
                    "  \"timestamp\": \"2024-08-14T10:30:00Z\"\n" +
                    "}")));
    }

    /**
     * Stub for service unavailable scenario
     * Used for testing error handling and resilience
     */
    public static void stubServiceUnavailable(WireMockServer wireMockServer) {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/credit-check"))
            .willReturn(aResponse()
                .withStatus(503)
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                    "  \"error\": \"SERVICE_UNAVAILABLE\",\n" +
                    "  \"message\": \"Credit check service is temporarily unavailable\"\n" +
                    "}")));
    }
}