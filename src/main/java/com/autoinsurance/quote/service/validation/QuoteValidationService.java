package com.autoinsurance.quote.service.validation;

import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.VehicleDto;

/**
 * Service interface for quote request validation.
 * Defines validation methods for different components of a quote request.
 */
public interface QuoteValidationService {
    
    /**
     * Validates a complete quote request including vehicle and drivers.
     * @param request The quote request to validate
     * @throws com.autoinsurance.quote.exception.InvalidQuoteRequestException if validation fails
     */
    void validateQuoteRequest(QuoteRequestDto request);
    
    /**
     * Validates vehicle information.
     * @param vehicle The vehicle to validate
     * @throws com.autoinsurance.quote.exception.InvalidQuoteRequestException if validation fails
     */
    void validateVehicle(VehicleDto vehicle);
    
    /**
     * Validates a single driver.
     * @param driver The driver to validate
     * @throws com.autoinsurance.quote.exception.InvalidQuoteRequestException if validation fails
     */
    void validateDriver(DriverDto driver);
}