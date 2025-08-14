package com.autoinsurance.external.client;

import com.autoinsurance.external.dto.RiskAssessmentRequest;
import com.autoinsurance.external.dto.RiskAssessmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Risk Assessment Service Client
 * 
 * Handles communication with external risk assessment service.
 * Provides service virtualization support via configurable base URL.
 * 
 * Following TDD Red-Green-Blue cycle:
 * RED: Tests fail without proper risk assessment responses
 * GREEN: WireMock provides predictable risk assessment data
 * BLUE: Refactor client while maintaining contract compliance
 */
@Component
public class RiskAssessmentClient {

    private static final Logger log = LoggerFactory.getLogger(RiskAssessmentClient.class);
    
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public RiskAssessmentClient(RestTemplate restTemplate,
                               @Value("${external.risk-assessment.base-url:http://localhost:9001}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * Performs risk assessment for insurance quote calculation
     * 
     * @param request Risk assessment parameters
     * @return Risk assessment results including score and multiplier
     * @throws RiskAssessmentException if assessment fails
     */
    public RiskAssessmentResponse assessRisk(RiskAssessmentRequest request) {
        log.debug("Calling risk assessment service for driver age: {}, vehicle age: {}", 
                 request.getDriverAge(), request.getVehicleAge());
        
        try {
            String url = baseUrl + "/api/v1/risk-assessment";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<RiskAssessmentRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<RiskAssessmentResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, RiskAssessmentResponse.class
            );
            
            RiskAssessmentResponse result = response.getBody();
            
            log.info("Risk assessment completed - Score: {}, Category: {}, Multiplier: {}", 
                    result.getRiskScore(), result.getRiskCategory(), result.getBaseMultiplier());
            
            return result;
            
        } catch (Exception ex) {
            log.error("Risk assessment failed for driver age: {}, vehicle age: {}. Error: {}", 
                     request.getDriverAge(), request.getVehicleAge(), ex.getMessage(), ex);
            throw new RiskAssessmentException("Failed to assess risk", ex);
        }
    }

    public static class RiskAssessmentException extends RuntimeException {
        public RiskAssessmentException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}