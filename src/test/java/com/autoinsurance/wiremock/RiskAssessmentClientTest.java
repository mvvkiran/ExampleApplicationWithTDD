package com.autoinsurance.wiremock;

import com.autoinsurance.external.client.RiskAssessmentClient;
import com.autoinsurance.external.dto.RiskAssessmentRequest;
import com.autoinsurance.external.dto.RiskAssessmentResponse;
import com.autoinsurance.wiremock.stubs.RiskAssessmentStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * WireMock Integration Tests for Risk Assessment Client
 * 
 * Tests external service integration using WireMock service virtualization.
 * Follows TDD Red-Green-Blue cycle for comprehensive service testing.
 * 
 * RED: Tests fail without proper WireMock service responses
 * GREEN: WireMock provides predictable external service behavior
 * BLUE: Refactor client code while maintaining service contract compliance
 */
@SpringBootTest
class RiskAssessmentClientTest extends WireMockBaseTest {

    private RiskAssessmentClient riskAssessmentClient;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + WireMockTestConfig.RISK_ASSESSMENT_PORT;
        riskAssessmentClient = new RiskAssessmentClient(new RestTemplate(), baseUrl);
    }

    @Test
    void shouldReturnHighRiskForYoungDriver() {
        // RED: Test fails without proper WireMock stub
        
        // Arrange
        RiskAssessmentStubs.stubHighRiskYoungDriver(riskAssessmentWireMockServer);
        
        RiskAssessmentRequest request = new RiskAssessmentRequest(
            22, 5, 3, new BigDecimal("25000.00")
        );
        
        // Act
        RiskAssessmentResponse response = riskAssessmentClient.assessRisk(request);
        
        // Assert - GREEN: WireMock provides expected high-risk response
        assertThat(response).isNotNull();
        assertThat(response.getRiskScore()).isEqualTo(7.5);
        assertThat(response.getRiskCategory()).isEqualTo("HIGH");
        assertThat(response.getBaseMultiplier()).isEqualTo(1.8);
        assertThat(response.getRiskFactors()).containsExactly("Young driver", "Limited experience");
    }

    @Test
    void shouldReturnLowRiskForExperiencedDriver() {
        // RED: Test fails without proper WireMock stub
        
        // Arrange
        RiskAssessmentStubs.stubLowRiskExperiencedDriver(riskAssessmentWireMockServer);
        
        RiskAssessmentRequest request = new RiskAssessmentRequest(
            35, 3, 15, new BigDecimal("40000.00")
        );
        
        // Act
        RiskAssessmentResponse response = riskAssessmentClient.assessRisk(request);
        
        // Assert - GREEN: WireMock provides expected low-risk response
        assertThat(response).isNotNull();
        assertThat(response.getRiskScore()).isEqualTo(3.2);
        assertThat(response.getRiskCategory()).isEqualTo("LOW");
        assertThat(response.getBaseMultiplier()).isEqualTo(0.9);
        assertThat(response.getRiskFactors()).containsExactly("Experienced driver", "New vehicle");
    }

    @Test
    void shouldHandleValidationErrorForInvalidAge() {
        // RED: Test fails without proper error handling
        
        // Arrange
        RiskAssessmentStubs.stubValidationErrorInvalidAge(riskAssessmentWireMockServer);
        
        RiskAssessmentRequest request = new RiskAssessmentRequest(
            16, 10, 0, new BigDecimal("15000.00")
        );
        
        // Act & Assert - GREEN: WireMock provides validation error response
        RiskAssessmentClient.RiskAssessmentException exception = assertThrows(
            RiskAssessmentClient.RiskAssessmentException.class,
            () -> riskAssessmentClient.assessRisk(request)
        );
        
        assertThat(exception.getMessage()).contains("Failed to assess risk");
    }

    @Test
    void shouldHandleServiceUnavailableError() {
        // RED: Test fails without proper error handling
        
        // Arrange
        RiskAssessmentStubs.stubServiceUnavailable(riskAssessmentWireMockServer);
        
        RiskAssessmentRequest request = new RiskAssessmentRequest(
            30, 2, 10, new BigDecimal("30000.00")
        );
        
        // Act & Assert - GREEN: WireMock simulates service unavailable
        RiskAssessmentClient.RiskAssessmentException exception = assertThrows(
            RiskAssessmentClient.RiskAssessmentException.class,
            () -> riskAssessmentClient.assessRisk(request)
        );
        
        assertThat(exception.getMessage()).contains("Failed to assess risk");
    }

    @Test  
    void shouldReturnDefaultRiskForStandardScenario() {
        // RED: Test fails without default stub
        
        // Arrange
        RiskAssessmentStubs.stubDefaultRiskAssessment(riskAssessmentWireMockServer);
        
        RiskAssessmentRequest request = new RiskAssessmentRequest(
            28, 4, 8, new BigDecimal("35000.00")
        );
        
        // Act
        RiskAssessmentResponse response = riskAssessmentClient.assessRisk(request);
        
        // Assert - GREEN: WireMock provides default medium risk response
        assertThat(response).isNotNull();
        assertThat(response.getRiskScore()).isEqualTo(5.0);
        assertThat(response.getRiskCategory()).isEqualTo("MEDIUM");
        assertThat(response.getBaseMultiplier()).isEqualTo(1.0);
        assertThat(response.getRiskFactors()).containsExactly("Standard driver");
    }
}