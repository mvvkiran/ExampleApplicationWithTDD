package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.entity.Quote;

/**
 * Interface for building Quote entities from request data.
 * Provides a contract for quote entity creation with performance optimization.
 */
public interface QuoteEntityBuilderInterface {
    
    /**
     * Builds a Quote entity from request and premium calculation with optimized performance.
     * 
     * @param request The quote request containing vehicle and driver information
     * @param premiumCalc The calculated premium components
     * @return A fully populated Quote entity
     */
    Quote buildQuoteEntity(QuoteRequestDto request, PremiumCalculation premiumCalc);
}