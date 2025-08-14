package com.autoinsurance.wiremock;

import com.autoinsurance.external.client.CreditCheckClient;
import com.autoinsurance.external.dto.CreditCheckRequest;
import com.autoinsurance.external.dto.CreditCheckResponse;
import com.autoinsurance.wiremock.stubs.CreditCheckStubs;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Credit Check Client Integration Test with WireMock
 * 
 * Tests external service integration using WireMock service virtualization.
 * Follows TDD Red-Green-Blue cycle for comprehensive service testing.
 * 
 * RED: Tests fail without proper WireMock service responses
 * GREEN: WireMock provides predictable external service behavior
 * BLUE: Refactor client code while maintaining service contract compliance
 */
class CreditCheckClientIntegrationTest {

    private static WireMockServer wireMockServer;
    private CreditCheckClient creditCheckClient;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(9002));
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
        String baseUrl = "http://localhost:9002";
        creditCheckClient = new CreditCheckClient(new RestTemplate(), baseUrl, "test-token");
    }

    @AfterEach
    void tearDown() {
        wireMockServer.resetAll();
    }

    @Test
    void shouldReturnExcellentCreditWithDiscount() {
        // RED: Test fails without proper WireMock stub
        
        // Arrange
        CreditCheckStubs.stubExcellentCreditScore(wireMockServer);
        
        CreditCheckRequest request = new CreditCheckRequest(
            "John", "Smith", "123-45-6789", LocalDate.of(1985, 6, 15),
            new CreditCheckRequest.Address("123 Main St", "New York", "NY", "10001")
        );
        
        // Act
        CreditCheckResponse response = creditCheckClient.checkCredit(request);
        
        // Assert - GREEN: WireMock provides expected excellent credit response
        assertThat(response).isNotNull();
        assertThat(response.getCreditScore()).isEqualTo(785);
        assertThat(response.getCreditTier()).isEqualTo("EXCELLENT");
        assertThat(response.isDiscountEligible()).isTrue();
        assertThat(response.getDiscountPercentage()).isEqualTo(15);
        assertThat(response.getReportDate()).isEqualTo("2024-08-14");
    }

    @Test
    void shouldReturnFairCreditWithoutDiscount() {
        // RED: Test fails without proper WireMock stub
        
        // Arrange
        CreditCheckStubs.stubFairCreditScore(wireMockServer);
        
        CreditCheckRequest request = new CreditCheckRequest(
            "Jane", "Doe", "987-65-4321", LocalDate.of(1990, 3, 20),
            new CreditCheckRequest.Address("456 Oak Ave", "Los Angeles", "CA", "90210")
        );
        
        // Act
        CreditCheckResponse response = creditCheckClient.checkCredit(request);
        
        // Assert - GREEN: WireMock provides expected fair credit response
        assertThat(response).isNotNull();
        assertThat(response.getCreditScore()).isEqualTo(620);
        assertThat(response.getCreditTier()).isEqualTo("FAIR");
        assertThat(response.isDiscountEligible()).isFalse();
        assertThat(response.getDiscountPercentage()).isEqualTo(0);
        assertThat(response.getReportDate()).isEqualTo("2024-08-14");
    }

    @Test
    void shouldHandleInvalidSSNFormat() {
        // RED: Test fails without proper error handling
        
        // Arrange
        CreditCheckStubs.stubInvalidSSNFormat(wireMockServer);
        
        CreditCheckRequest request = new CreditCheckRequest(
            "Invalid", "SSN", "invalid-ssn", LocalDate.of(1985, 6, 15),
            new CreditCheckRequest.Address("123 Main St", "New York", "NY", "10001")
        );
        
        // Act & Assert - GREEN: WireMock provides validation error response
        CreditCheckClient.CreditCheckException exception = assertThrows(
            CreditCheckClient.CreditCheckException.class,
            () -> creditCheckClient.checkCredit(request)
        );
        
        assertThat(exception.getMessage()).contains("Failed to check credit");
    }

    @Test
    void shouldReturnDefaultCreditForStandardScenario() {
        // RED: Test fails without default stub
        
        // Arrange
        CreditCheckStubs.stubDefaultCreditCheck(wireMockServer);
        
        CreditCheckRequest request = new CreditCheckRequest(
            "Standard", "User", "555-66-7777", LocalDate.of(1988, 5, 10),
            new CreditCheckRequest.Address("789 Standard St", "Standard City", "SC", "55566")
        );
        
        // Act
        CreditCheckResponse response = creditCheckClient.checkCredit(request);
        
        // Assert - GREEN: WireMock provides default good credit response
        assertThat(response).isNotNull();
        assertThat(response.getCreditScore()).isEqualTo(700);
        assertThat(response.getCreditTier()).isEqualTo("GOOD");
        assertThat(response.isDiscountEligible()).isTrue();
        assertThat(response.getDiscountPercentage()).isEqualTo(10);
        assertThat(response.getReportDate()).isEqualTo("2024-08-14");
    }
}