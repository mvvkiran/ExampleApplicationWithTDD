package com.autoinsurance.contract.provider;

import com.autoinsurance.quote.service.QuoteService;
import com.autoinsurance.quote.service.RiskCalculationService;
import com.autoinsurance.quote.service.DiscountService;
import com.autoinsurance.quote.repository.QuoteRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration for Pact provider tests
 * 
 * This configuration provides mock beans for contract testing to ensure
 * predictable responses during Pact verification.
 * 
 * Following TDD approach:
 * RED: Configure mocks to match expected contract behaviors
 * GREEN: Provide implementations that satisfy contract requirements
 * BLUE: Refactor test setup while maintaining contract compliance
 */
@TestConfiguration
@Profile("pact-provider-test")
public class PactProviderTestConfiguration {

    /**
     * Mock QuoteService for predictable contract testing
     * RED: Ensure service responses match consumer expectations
     */
    @MockBean
    public QuoteService quoteService;

    /**
     * Mock RiskCalculationService for deterministic risk assessments
     * RED: Provide consistent risk calculations for contract verification
     */
    @MockBean
    public RiskCalculationService riskCalculationService;

    /**
     * Mock DiscountService for predictable discount calculations
     * RED: Ensure discount logic matches contract expectations
     */
    @MockBean
    public DiscountService discountService;

    /**
     * Mock QuoteRepository for data access during contract tests
     * RED: Provide predictable data responses for verification
     */
    @MockBean
    public QuoteRepository quoteRepository;
}