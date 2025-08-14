package com.autoinsurance.external.client;

import com.autoinsurance.external.dto.CreditCheckRequest;
import com.autoinsurance.external.dto.CreditCheckResponse;
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
 * Credit Check Service Client
 * 
 * Handles communication with external credit check service.
 * Provides service virtualization support via configurable base URL.
 * 
 * Following TDD Red-Green-Blue cycle:
 * RED: Tests fail without proper credit check responses
 * GREEN: WireMock provides predictable credit check data
 * BLUE: Refactor client while maintaining contract compliance
 */
@Component
public class CreditCheckClient {

    private static final Logger log = LoggerFactory.getLogger(CreditCheckClient.class);
    
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public CreditCheckClient(RestTemplate restTemplate,
                           @Value("${external.credit-check.base-url:http://localhost:9002}") String baseUrl,
                           @Value("${external.credit-check.api-key:test-token}") String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    /**
     * Performs credit check for insurance discount eligibility
     * 
     * @param request Credit check parameters including personal information
     * @return Credit check results including score and discount eligibility
     * @throws CreditCheckException if credit check fails
     */
    public CreditCheckResponse checkCredit(CreditCheckRequest request) {
        log.debug("Calling credit check service for: {} {}", 
                 request.getFirstName(), request.getLastName());
        
        try {
            String url = baseUrl + "/api/v1/credit-check";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            HttpEntity<CreditCheckRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<CreditCheckResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, CreditCheckResponse.class
            );
            
            CreditCheckResponse result = response.getBody();
            
            log.info("Credit check completed - Score: {}, Tier: {}, Discount: {}%", 
                    result.getCreditScore(), result.getCreditTier(), result.getDiscountPercentage());
            
            return result;
            
        } catch (Exception ex) {
            log.error("Credit check failed for: {} {}. Error: {}", 
                     request.getFirstName(), request.getLastName(), ex.getMessage(), ex);
            throw new CreditCheckException("Failed to check credit", ex);
        }
    }

    public static class CreditCheckException extends RuntimeException {
        public CreditCheckException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}