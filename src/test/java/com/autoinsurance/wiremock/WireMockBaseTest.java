package com.autoinsurance.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Base class for WireMock tests
 * 
 * Provides common WireMock setup and teardown functionality.
 * Follows TDD principles for service virtualization testing.
 * 
 * RED: Tests fail without proper service mocking
 * GREEN: WireMock provides predictable service responses  
 * BLUE: Refactor tests while maintaining service virtualization
 */
@SpringBootTest
@ContextConfiguration(classes = WireMockTestConfig.class)
public abstract class WireMockBaseTest {

    @Autowired
    protected WireMockServer riskAssessmentWireMockServer;
    
    @Autowired
    protected WireMockServer creditCheckWireMockServer;

    @BeforeEach
    void startWireMockServers() {
        if (!riskAssessmentWireMockServer.isRunning()) {
            riskAssessmentWireMockServer.start();
        }
        
        if (!creditCheckWireMockServer.isRunning()) {
            creditCheckWireMockServer.start();
        }
    }

    @AfterEach
    void resetWireMockServers() {
        riskAssessmentWireMockServer.resetAll();
        creditCheckWireMockServer.resetAll();
    }

    protected void stopWireMockServers() {
        if (riskAssessmentWireMockServer.isRunning()) {
            riskAssessmentWireMockServer.stop();
        }
        
        if (creditCheckWireMockServer.isRunning()) {
            creditCheckWireMockServer.stop();
        }
    }
}