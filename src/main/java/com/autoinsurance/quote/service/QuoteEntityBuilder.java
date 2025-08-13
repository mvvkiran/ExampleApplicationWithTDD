package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.entity.Quote;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Performance-optimized builder for Quote entities.
 * Reduces object creation overhead and provides reusable quote building logic.
 */
@Component
public class QuoteEntityBuilder implements QuoteEntityBuilderInterface {

    private static final int QUOTE_VALIDITY_DAYS = 30;

    /**
     * Builds a Quote entity from request and premium calculation with optimized performance.
     * 
     * @param request The quote request containing vehicle and driver information
     * @param premiumCalc The calculated premium components
     * @return A fully populated Quote entity
     */
    public Quote buildQuoteEntity(QuoteRequestDto request, PremiumCalculation premiumCalc) {
        // Use StringBuilder for efficient string concatenation
        StringBuilder primaryDriverNameBuilder = new StringBuilder();
        var primaryDriver = request.getDrivers().get(0);
        primaryDriverNameBuilder.append(primaryDriver.getFirstName())
                               .append(" ")
                               .append(primaryDriver.getLastName());
        
        return Quote.builder()
                .id(UUID.randomUUID().toString())
                .premium(premiumCalc.getFinalPremium())
                .monthlyPremium(premiumCalc.getMonthlyPremium())
                .coverageAmount(request.getCoverageAmount())
                .deductible(request.getDeductible())
                .validUntil(LocalDate.now().plusDays(QUOTE_VALIDITY_DAYS))
                .vehicleMake(request.getVehicle().getMake())
                .vehicleModel(request.getVehicle().getModel())
                .vehicleYear(request.getVehicle().getYear())
                .vehicleVin(request.getVehicle().getVin())
                .vehicleCurrentValue(request.getVehicle().getCurrentValue())
                .primaryDriverName(primaryDriverNameBuilder.toString())
                .primaryDriverLicense(primaryDriver.getLicenseNumber())
                .discountsApplied(copyDiscountsList(premiumCalc.getAppliedDiscounts()))
                .build();
    }

    /**
     * Creates a defensive copy of the discounts list to prevent external modification.
     * Uses ArrayList for better performance with small lists.
     */
    private List<String> copyDiscountsList(List<String> originalDiscounts) {
        if (originalDiscounts == null || originalDiscounts.isEmpty()) {
            return List.of(); // Immutable empty list
        }
        return List.copyOf(originalDiscounts); // Immutable copy
    }
}