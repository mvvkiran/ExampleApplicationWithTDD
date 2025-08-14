package com.autoinsurance.wiremock.standalone;

import com.autoinsurance.wiremock.WireMockTestConfig;
import com.autoinsurance.wiremock.WireMockTestUtilities;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standalone WireMock Server for Development
 * 
 * Provides a standalone WireMock server that can be run during development
 * to virtualize external services without requiring real service dependencies.
 * 
 * Following TDD Red-Green-Blue cycle for development workflow:
 * RED: Development fails without external services
 * GREEN: WireMock virtualizes all external dependencies  
 * BLUE: Refactor application code while maintaining service virtualization
 * 
 * Usage:
 * - Run this class to start WireMock servers for all external services
 * - Configure application.yml to point to localhost:9001 and localhost:9002
 * - Develop and test against predictable virtualized services
 */
public class WireMockStandaloneServer {

    private static final Logger log = LoggerFactory.getLogger(WireMockStandaloneServer.class);
    
    private static WireMockServer riskAssessmentServer;
    private static WireMockServer creditCheckServer;

    public static void main(String[] args) {
        log.info("Starting WireMock Standalone Servers for Auto Insurance API external dependencies");
        
        try {
            startServers();
            setupStubs();
            
            log.info("=======================================================");
            log.info("WireMock Servers Successfully Started!");
            log.info("=======================================================");
            log.info("Risk Assessment Service: http://localhost:{}", WireMockTestConfig.RISK_ASSESSMENT_PORT);
            log.info("Credit Check Service: http://localhost:{}", WireMockTestConfig.CREDIT_CHECK_PORT);
            log.info("=======================================================");
            log.info("Configure your application.yml with:");
            log.info("external:");
            log.info("  risk-assessment:");
            log.info("    base-url: http://localhost:{}", WireMockTestConfig.RISK_ASSESSMENT_PORT);
            log.info("  credit-check:");
            log.info("    base-url: http://localhost:{}", WireMockTestConfig.CREDIT_CHECK_PORT);
            log.info("    api-key: test-token");
            log.info("=======================================================");
            log.info("Press Ctrl+C to stop the servers");
            
            // Add shutdown hook to gracefully stop servers
            Runtime.getRuntime().addShutdownHook(new Thread(WireMockStandaloneServer::stopServers));
            
            // Keep the main thread alive
            Thread.currentThread().join();
            
        } catch (Exception e) {
            log.error("Failed to start WireMock servers", e);
            stopServers();
            System.exit(1);
        }
    }

    private static void startServers() {
        log.info("Starting Risk Assessment WireMock Server on port {}", WireMockTestConfig.RISK_ASSESSMENT_PORT);
        riskAssessmentServer = new WireMockServer(
            WireMockConfiguration.options()
                .port(WireMockTestConfig.RISK_ASSESSMENT_PORT)
                .usingFilesUnderClasspath("wiremock/risk-assessment")
        );
        riskAssessmentServer.start();

        log.info("Starting Credit Check WireMock Server on port {}", WireMockTestConfig.CREDIT_CHECK_PORT);
        creditCheckServer = new WireMockServer(
            WireMockConfiguration.options()
                .port(WireMockTestConfig.CREDIT_CHECK_PORT)
                .usingFilesUnderClasspath("wiremock/credit-check")
        );
        creditCheckServer.start();
        
        // Verify servers are running
        if (!WireMockTestUtilities.verifyServersRunning(riskAssessmentServer, creditCheckServer)) {
            throw new RuntimeException("Failed to start one or more WireMock servers");
        }
    }

    private static void setupStubs() {
        log.info("Configuring WireMock stubs for all external services");
        
        // Setup comprehensive stubs for development use
        WireMockTestUtilities.setupHappyPathStubs(riskAssessmentServer, creditCheckServer);
        WireMockTestUtilities.setupErrorScenarios(riskAssessmentServer, creditCheckServer);
        WireMockTestUtilities.setupRiskScenarios(riskAssessmentServer);
        WireMockTestUtilities.setupCreditScenarios(creditCheckServer);
        
        log.info("WireMock stubs configured successfully");
        
        // Log available endpoints
        log.info("Available Risk Assessment endpoints:");
        log.info("  POST http://localhost:{}/api/v1/risk-assessment", WireMockTestConfig.RISK_ASSESSMENT_PORT);
        
        log.info("Available Credit Check endpoints:");
        log.info("  POST http://localhost:{}/api/v1/credit-check", WireMockTestConfig.CREDIT_CHECK_PORT);
    }

    private static void stopServers() {
        log.info("Stopping WireMock Standalone Servers");
        
        if (riskAssessmentServer != null && riskAssessmentServer.isRunning()) {
            riskAssessmentServer.stop();
            log.info("Risk Assessment WireMock Server stopped");
        }
        
        if (creditCheckServer != null && creditCheckServer.isRunning()) {
            creditCheckServer.stop();
            log.info("Credit Check WireMock Server stopped");
        }
        
        log.info("All WireMock servers stopped successfully");
    }

    /**
     * Programmatic server control for integration testing
     */
    public static void startStandaloneServers() {
        if (riskAssessmentServer == null || !riskAssessmentServer.isRunning()) {
            startServers();
            setupStubs();
        }
    }

    public static void stopStandaloneServers() {
        stopServers();
    }

    public static boolean areServersRunning() {
        return riskAssessmentServer != null && riskAssessmentServer.isRunning() &&
               creditCheckServer != null && creditCheckServer.isRunning();
    }
}