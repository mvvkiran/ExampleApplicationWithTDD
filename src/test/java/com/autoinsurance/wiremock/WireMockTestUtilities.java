package com.autoinsurance.wiremock;

import com.autoinsurance.wiremock.stubs.CreditCheckStubs;
import com.autoinsurance.wiremock.stubs.RiskAssessmentStubs;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WireMock Test Utilities
 * 
 * Provides utility methods for setting up common WireMock scenarios.
 * Supports TDD Red-Green-Blue cycle for service virtualization testing.
 */
public class WireMockTestUtilities {

    private static final Logger log = LoggerFactory.getLogger(WireMockTestUtilities.class);

    /**
     * Sets up all successful service responses for happy path testing
     * 
     * RED: Tests fail without proper service mocking
     * GREEN: All services return successful responses
     * BLUE: Refactor test scenarios while maintaining success paths
     */
    public static void setupHappyPathStubs(WireMockServer riskAssessmentServer, WireMockServer creditCheckServer) {
        log.debug("Setting up happy path stubs for all external services");
        
        // Risk Assessment - Default medium risk for most scenarios
        RiskAssessmentStubs.stubDefaultRiskAssessment(riskAssessmentServer);
        RiskAssessmentStubs.stubLowRiskExperiencedDriver(riskAssessmentServer);
        
        // Credit Check - Default good credit for most scenarios  
        CreditCheckStubs.stubDefaultCreditCheck(creditCheckServer);
        CreditCheckStubs.stubExcellentCreditScore(creditCheckServer);
        
        log.info("Happy path stubs configured for Risk Assessment and Credit Check services");
    }

    /**
     * Sets up error scenarios for resilience testing
     * 
     * Tests how the system handles external service failures
     */
    public static void setupErrorScenarios(WireMockServer riskAssessmentServer, WireMockServer creditCheckServer) {
        log.debug("Setting up error scenario stubs for resilience testing");
        
        // Risk Assessment errors
        RiskAssessmentStubs.stubValidationErrorInvalidAge(riskAssessmentServer);
        RiskAssessmentStubs.stubServiceUnavailable(riskAssessmentServer);
        
        // Credit Check errors
        CreditCheckStubs.stubInvalidSSNFormat(creditCheckServer);
        CreditCheckStubs.stubUnauthorizedAccess(creditCheckServer);
        CreditCheckStubs.stubServiceUnavailable(creditCheckServer);
        
        log.info("Error scenario stubs configured for resilience testing");
    }

    /**
     * Sets up specific risk scenarios for comprehensive risk testing
     * 
     * Covers various driver and vehicle risk combinations
     */
    public static void setupRiskScenarios(WireMockServer riskAssessmentServer) {
        log.debug("Setting up risk scenario stubs");
        
        RiskAssessmentStubs.stubHighRiskYoungDriver(riskAssessmentServer);
        RiskAssessmentStubs.stubLowRiskExperiencedDriver(riskAssessmentServer);
        RiskAssessmentStubs.stubValidationErrorInvalidAge(riskAssessmentServer);
        
        log.info("Risk scenario stubs configured");
    }

    /**
     * Sets up specific credit scenarios for discount testing
     * 
     * Covers various credit scores and discount eligibility combinations
     */
    public static void setupCreditScenarios(WireMockServer creditCheckServer) {
        log.debug("Setting up credit scenario stubs");
        
        CreditCheckStubs.stubExcellentCreditScore(creditCheckServer);
        CreditCheckStubs.stubFairCreditScore(creditCheckServer);
        CreditCheckStubs.stubInvalidSSNFormat(creditCheckServer);
        
        log.info("Credit scenario stubs configured");
    }

    /**
     * Verifies that WireMock servers are running and accessible
     * 
     * @param servers Array of WireMock servers to verify
     * @return true if all servers are running, false otherwise
     */
    public static boolean verifyServersRunning(WireMockServer... servers) {
        for (WireMockServer server : servers) {
            if (!server.isRunning()) {
                log.error("WireMock server on port {} is not running", server.port());
                return false;
            }
        }
        
        log.debug("All WireMock servers verified as running");
        return true;
    }

    /**
     * Resets all WireMock servers to clean state
     * 
     * Useful for test isolation and cleanup
     */
    public static void resetAllServers(WireMockServer... servers) {
        for (WireMockServer server : servers) {
            if (server.isRunning()) {
                server.resetAll();
                log.debug("Reset WireMock server on port {}", server.port());
            }
        }
    }
}