package com.autoinsurance.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * WireMock Test Configuration
 * 
 * Provides centralized WireMock server configuration for all tests.
 * Follows TDD Red-Green-Blue cycle for external service virtualization.
 */
@TestConfiguration
public class WireMockTestConfig {

    public static final int RISK_ASSESSMENT_PORT = 9001;
    public static final int CREDIT_CHECK_PORT = 9002;
    
    public static final String RISK_ASSESSMENT_BASE_URL = "http://localhost:" + RISK_ASSESSMENT_PORT;
    public static final String CREDIT_CHECK_BASE_URL = "http://localhost:" + CREDIT_CHECK_PORT;

    @Bean
    @Primary
    public WireMockServer riskAssessmentWireMockServer() {
        WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                .port(RISK_ASSESSMENT_PORT)
                .usingFilesUnderClasspath("wiremock/risk-assessment")
        );
        return wireMockServer;
    }

    @Bean
    @Primary  
    public WireMockServer creditCheckWireMockServer() {
        WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                .port(CREDIT_CHECK_PORT)
                .usingFilesUnderClasspath("wiremock/credit-check")
        );
        return wireMockServer;
    }
}