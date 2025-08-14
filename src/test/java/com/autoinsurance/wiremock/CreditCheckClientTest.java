package com.autoinsurance.wiremock;

import com.autoinsurance.external.client.CreditCheckClient;
import com.autoinsurance.external.dto.CreditCheckRequest;
import com.autoinsurance.external.dto.CreditCheckResponse;
import com.autoinsurance.wiremock.stubs.CreditCheckStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * WireMock Integration Tests for Credit Check Client
 * 
 * Tests external service integration using WireMock service virtualization.
 * Follows TDD Red-Green-Blue cycle for comprehensive service testing.
 * 
 * RED: Tests fail without proper WireMock service responses
 * GREEN: WireMock provides predictable external service behavior
 * BLUE: Refactor client code while maintaining service contract compliance
 */
@SpringBootTest
class CreditCheckClientTest extends WireMockBaseTest {

    private CreditCheckClient creditCheckClient;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + WireMockTestConfig.CREDIT_CHECK_PORT;
        creditCheckClient = new CreditCheckClient(new RestTemplate(), baseUrl, "test-token");
    }

    @Test
    void shouldReturnExcellentCreditWithDiscount() {
        // RED: Test fails without proper WireMock stub
        
        // Arrange
        CreditCheckStubs.stubExcellentCreditScore(creditCheckWireMockServer);
        
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
        CreditCheckStubs.stubFairCreditScore(creditCheckWireMockServer);
        
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
        CreditCheckStubs.stubInvalidSSNFormat(creditCheckWireMockServer);
        
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
    void shouldHandleUnauthorizedAccess() {
        // RED: Test fails without proper error handling
        
        // Arrange
        CreditCheckStubs.stubUnauthorizedAccess(creditCheckWireMockServer);
        
        // Create client with invalid token to trigger unauthorized response
        CreditCheckClient unauthorizedClient = new CreditCheckClient(
            new RestTemplate(), 
            "http://localhost:" + WireMockTestConfig.CREDIT_CHECK_PORT, 
            "invalid-token"
        );
        
        CreditCheckRequest request = new CreditCheckRequest(
            "Test", "User", "000-00-0000", LocalDate.of(1980, 1, 1),
            new CreditCheckRequest.Address("123 Test St", "Test City", "TX", "12345")
        );
        
        // Act & Assert - GREEN: WireMock simulates unauthorized access
        CreditCheckClient.CreditCheckException exception = assertThrows(
            CreditCheckClient.CreditCheckException.class,
            () -> unauthorizedClient.checkCredit(request)
        );
        
        assertThat(exception.getMessage()).contains("Failed to check credit");
    }

    @Test
    void shouldHandleServiceUnavailableError() {
        // RED: Test fails without proper error handling
        
        // Arrange
        CreditCheckStubs.stubServiceUnavailable(creditCheckWireMockServer);
        
        CreditCheckRequest request = new CreditCheckRequest(
            "Service", "Test", "123-45-6789", LocalDate.of(1985, 1, 1),
            new CreditCheckRequest.Address("123 Service St", "Test City", "TX", "12345")
        );
        
        // Act & Assert - GREEN: WireMock simulates service unavailable
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
        CreditCheckStubs.stubDefaultCreditCheck(creditCheckWireMockServer);
        
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