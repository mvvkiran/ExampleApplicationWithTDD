package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.QuoteResponseDto;
import com.autoinsurance.quote.entity.Quote;
import com.autoinsurance.quote.exception.QuoteNotFoundException;
import com.autoinsurance.quote.repository.QuoteRepository;
import com.autoinsurance.quote.service.validation.QuoteValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Implementation of QuoteService providing core quote management functionality.
 * Handles quote generation, retrieval, and premium calculations with comprehensive
 * validation and error handling.
 */
@Service
public class QuoteServiceImpl implements QuoteService {

    private static final Logger log = LoggerFactory.getLogger(QuoteServiceImpl.class);
    
    private final QuoteRepository quoteRepository;
    private final RiskCalculationService riskCalculationService;
    private final DiscountService discountService;
    private final QuoteValidationService validationService;
    private final QuoteEntityBuilderInterface quoteEntityBuilder;
    
    public QuoteServiceImpl(QuoteRepository quoteRepository, 
                           RiskCalculationService riskCalculationService,
                           DiscountService discountService,
                           QuoteValidationService validationService,
                           QuoteEntityBuilderInterface quoteEntityBuilder) {
        this.quoteRepository = quoteRepository;
        this.riskCalculationService = riskCalculationService;
        this.discountService = discountService;
        this.validationService = validationService;
        this.quoteEntityBuilder = quoteEntityBuilder;
    }

    @Override
    public QuoteResponseDto generateQuote(QuoteRequestDto request) {
        log.info("Starting quote generation process");
        
        // Validate request
        validationService.validateQuoteRequest(request);
        
        // Log validation success with key identifiers
        String vin = request.getVehicle().getVin();
        String primaryDriverName = getPrimaryDriverName(request);
        log.debug("Quote validation successful for vehicle VIN: {} and primary driver: {}", 
                 vin, primaryDriverName);
        
        try {
            // Calculate premium components
            PremiumCalculation premiumCalc = calculatePremiumComponents(request);
            
            // Create and save quote entity
            Quote quote = createQuoteEntity(request, premiumCalc);
            Quote savedQuote = quoteRepository.save(quote);
            
            // Build response
            QuoteResponseDto response = buildQuoteResponse(savedQuote, premiumCalc);
            
            log.info("Quote successfully generated with ID: {}, premium: {}, VIN: {}", 
                    savedQuote.getId(), premiumCalc.getFinalPremium(), vin);
            
            return response;
            
        } catch (Exception ex) {
            log.error("Failed to generate quote for VIN: {} and driver: {}. Error: {}", 
                     vin, primaryDriverName, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    @Cacheable(value = "quoteCache", key = "#id", unless = "#result == null")
    public QuoteResponseDto getQuoteById(String id) {
        log.debug("Retrieving quote with ID: {}", id);
        
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Quote ID cannot be null or empty");
        }
        
        try {
            Quote quote = quoteRepository.findById(id)
                    .orElseThrow(() -> new QuoteNotFoundException("Quote not found with ID: " + id));
            
            QuoteResponseDto response = buildQuoteResponseFromEntity(quote);
            
            log.debug("Quote retrieved successfully for ID: {}", id);
            return response;
            
        } catch (QuoteNotFoundException ex) {
            log.warn("Quote not found for ID: {}", id);
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to retrieve quote with ID: {}. Error: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public BigDecimal calculatePremium(QuoteRequestDto request) {
        log.info("Starting premium calculation");
        
        if (request == null) {
            throw new IllegalArgumentException("Quote request cannot be null");
        }
        
        // Validate request
        validationService.validateQuoteRequest(request);
        
        try {
            BigDecimal premium = riskCalculationService.calculateBasePremium(request);
            
            log.debug("Premium calculation completed successfully for VIN: {}, amount: {}", 
                     request.getVehicle().getVin(), premium);
            
            return premium;
            
        } catch (Exception ex) {
            log.error("Failed to calculate premium for VIN: {}. Error: {}", 
                     request.getVehicle().getVin(), ex.getMessage(), ex);
            throw ex;
        }
    }
    
    /**
     * Extracts the primary driver name from the request for logging purposes.
     */
    private String getPrimaryDriverName(QuoteRequestDto request) {
        if (request.getDrivers() == null || request.getDrivers().isEmpty()) {
            return "Unknown";
        }
        
        var primaryDriver = request.getDrivers().get(0);
        return String.format("%s %s", 
                            primaryDriver.getFirstName(), 
                            primaryDriver.getLastName());
    }
    
    /**
     * Calculates all premium-related components with caching for performance.
     */
    @Cacheable(value = "riskCalculationCache", key = "#request.vehicle.vin + '-' + #request.drivers.size() + '-' + #request.coverageAmount")
    private PremiumCalculation calculatePremiumComponents(QuoteRequestDto request) {
        BigDecimal basePremium = riskCalculationService.calculateBasePremium(request);
        BigDecimal discount = discountService.calculateTotalDiscount(request);
        BigDecimal finalPremium = basePremium.subtract(discount);
        BigDecimal monthlyPremium = finalPremium.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
        List<String> appliedDiscounts = discountService.getAppliedDiscounts(request);
        
        return new PremiumCalculation(basePremium, discount, finalPremium, monthlyPremium, appliedDiscounts);
    }
    
    /**
     * Creates a Quote entity from the request and premium calculation using the optimized builder.
     */
    private Quote createQuoteEntity(QuoteRequestDto request, PremiumCalculation premiumCalc) {
        return quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);
    }
    
    /**
     * Builds a QuoteResponseDto from saved quote entity and premium calculation.
     */
    private QuoteResponseDto buildQuoteResponse(Quote savedQuote, PremiumCalculation premiumCalc) {
        return QuoteResponseDto.builder()
                .quoteId(savedQuote.getId())
                .premium(premiumCalc.getFinalPremium())
                .monthlyPremium(premiumCalc.getMonthlyPremium())
                .coverageAmount(savedQuote.getCoverageAmount())
                .deductible(savedQuote.getDeductible())
                .validUntil(savedQuote.getValidUntil())
                .discountsApplied(premiumCalc.getAppliedDiscounts())
                .build();
    }
    
    /**
     * Builds a QuoteResponseDto from a Quote entity (for retrieval operations).
     */
    private QuoteResponseDto buildQuoteResponseFromEntity(Quote quote) {
        return QuoteResponseDto.builder()
                .quoteId(quote.getId())
                .premium(quote.getPremium())
                .monthlyPremium(quote.getMonthlyPremium())
                .coverageAmount(quote.getCoverageAmount())
                .deductible(quote.getDeductible())
                .validUntil(quote.getValidUntil())
                .discountsApplied(quote.getDiscountsApplied())
                .build();
    }
}