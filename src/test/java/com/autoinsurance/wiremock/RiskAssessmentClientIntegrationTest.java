package com.autoinsurance.wiremock;

import com.autoinsurance.external.client.RiskAssessmentClient;
import com.autoinsurance.external.dto.RiskAssessmentRequest;
import com.autoinsurance.external.dto.RiskAssessmentResponse;
import com.autoinsurance.wiremock.stubs.RiskAssessmentStubs;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Risk Assessment Client Integration Test with WireMock
 * 
 * Tests external service integration using WireMock service virtualization.
 * Follows TDD Red-Green-Blue cycle for comprehensive service testing.
 * 
 * RED: Tests fail without proper WireMock service responses
 * GREEN: WireMock provides predictable external service behavior
 * BLUE: Refactor client code while maintaining service contract compliance
 */
class RiskAssessmentClientIntegrationTest {

    private static WireMockServer wireMockServer;
    private RiskAssessmentClient riskAssessmentClient;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(9001));
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
        String baseUrl = "http://localhost:9001";
        riskAssessmentClient = new RiskAssessmentClient(new RestTemplate(), baseUrl);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.resetAll();
    }

    @Test
    void shouldReturnHighRiskForYoungDriver() {
        // RED: Test fails without proper WireMock stub
        
        // Arrange
        RiskAssessmentStubs.stubHighRiskYoungDriver(wireMockServer);
        
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
        RiskAssessmentStubs.stubLowRiskExperiencedDriver(wireMockServer);
        
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
        RiskAssessmentStubs.stubValidationErrorInvalidAge(wireMockServer);
        
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
    void shouldReturnDefaultRiskForStandardScenario() {
        // RED: Test fails without default stub
        
        // Arrange
        RiskAssessmentStubs.stubDefaultRiskAssessment(wireMockServer);
        
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