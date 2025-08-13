package com.autoinsurance.quote.service.validation;

import com.autoinsurance.quote.config.QuoteValidationConfig;
import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.VehicleDto;
import com.autoinsurance.quote.exception.InvalidQuoteRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

/**
 * Implementation of quote validation service.
 * Handles validation of quote requests using configurable business rules.
 */
@Service
public class QuoteValidationServiceImpl implements QuoteValidationService {
    
    private static final Logger log = LoggerFactory.getLogger(QuoteValidationServiceImpl.class);
    
    private final QuoteValidationConfig validationConfig;
    
    public QuoteValidationServiceImpl(QuoteValidationConfig validationConfig) {
        this.validationConfig = validationConfig;
    }
    
    @Override
    public void validateQuoteRequest(QuoteRequestDto request) {
        log.debug("Starting validation for quote request");
        
        if (request == null) {
            throw new InvalidQuoteRequestException("Quote request cannot be null");
        }
        
        validateVehiclePresence(request);
        validateDriversPresence(request);
        validateCoverageInformation(request);
        
        // Validate individual components
        validateVehicle(request.getVehicle());
        
        for (DriverDto driver : request.getDrivers()) {
            validateDriver(driver);
        }
        
        log.debug("Quote request validation completed successfully");
    }
    
    @Override
    public void validateVehicle(VehicleDto vehicle) {
        if (vehicle == null) {
            throw new InvalidQuoteRequestException("Vehicle information is required");
        }
        
        validateVin(vehicle.getVin());
        validateVehicleAge(vehicle.getYear());
        validateVehicleBasicInfo(vehicle);
        
        log.debug("Vehicle validation completed for VIN: {}", vehicle.getVin());
    }
    
    @Override
    public void validateDriver(DriverDto driver) {
        if (driver == null) {
            throw new InvalidQuoteRequestException("Driver information cannot be null");
        }
        
        validateDriverBasicInfo(driver);
        validateDriverAge(driver);
        validateDriverLicense(driver);
        
        log.debug("Driver validation completed for: {} {}", 
                 driver.getFirstName(), driver.getLastName());
    }
    
    private void validateVehiclePresence(QuoteRequestDto request) {
        if (request.getVehicle() == null) {
            throw new InvalidQuoteRequestException("Vehicle information is required");
        }
    }
    
    private void validateDriversPresence(QuoteRequestDto request) {
        if (request.getDrivers() == null || request.getDrivers().isEmpty()) {
            throw new InvalidQuoteRequestException("At least one driver is required");
        }
    }
    
    private void validateCoverageInformation(QuoteRequestDto request) {
        if (request.getCoverageAmount() == null || request.getCoverageAmount().signum() <= 0) {
            throw new InvalidQuoteRequestException("Valid coverage amount is required");
        }
        
        if (request.getDeductible() == null || request.getDeductible().signum() < 0) {
            throw new InvalidQuoteRequestException("Valid deductible amount is required");
        }
    }
    
    private void validateVin(String vin) {
        if (vin == null || vin.trim().isEmpty()) {
            throw new InvalidQuoteRequestException("VIN is required");
        }
        
        if (!validationConfig.getCompiledVinPattern().matcher(vin).matches()) {
            throw new InvalidQuoteRequestException(
                String.format("Invalid VIN format: %s. Expected format: %s", 
                             vin, validationConfig.getVinPattern())
            );
        }
    }
    
    private void validateVehicleAge(Integer year) {
        if (year == null) {
            throw new InvalidQuoteRequestException("Vehicle year is required");
        }
        
        int currentYear = LocalDate.now().getYear();
        int vehicleAge = currentYear - year;
        
        if (vehicleAge < 0) {
            throw new InvalidQuoteRequestException("Vehicle year cannot be in the future");
        }
        
        if (vehicleAge > validationConfig.getMaxVehicleAge()) {
            throw new InvalidQuoteRequestException(
                String.format("Vehicle age exceeds maximum limit of %d years. Vehicle age: %d years", 
                             validationConfig.getMaxVehicleAge(), vehicleAge)
            );
        }
    }
    
    private void validateVehicleBasicInfo(VehicleDto vehicle) {
        if (vehicle.getMake() == null || vehicle.getMake().trim().isEmpty()) {
            throw new InvalidQuoteRequestException("Vehicle make is required");
        }
        
        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new InvalidQuoteRequestException("Vehicle model is required");
        }
        
        if (vehicle.getCurrentValue() == null || vehicle.getCurrentValue().signum() <= 0) {
            throw new InvalidQuoteRequestException("Valid vehicle current value is required");
        }
    }
    
    private void validateDriverBasicInfo(DriverDto driver) {
        if (driver.getFirstName() == null || driver.getFirstName().trim().isEmpty()) {
            throw new InvalidQuoteRequestException("Driver first name is required");
        }
        
        if (driver.getLastName() == null || driver.getLastName().trim().isEmpty()) {
            throw new InvalidQuoteRequestException("Driver last name is required");
        }
        
        if (driver.getDateOfBirth() == null) {
            throw new InvalidQuoteRequestException("Driver date of birth is required");
        }
    }
    
    private void validateDriverAge(DriverDto driver) {
        int age = calculateAge(driver.getDateOfBirth());
        
        if (age < validationConfig.getMinDriverAge()) {
            throw new InvalidQuoteRequestException(
                String.format("Driver must be at least %d years old. Current age: %d years", 
                             validationConfig.getMinDriverAge(), age)
            );
        }
        
        if (age > validationConfig.getMaxDriverAge()) {
            throw new InvalidQuoteRequestException(
                String.format("Driver age exceeds maximum limit of %d years. Current age: %d years", 
                             validationConfig.getMaxDriverAge(), age)
            );
        }
    }
    
    private void validateDriverLicense(DriverDto driver) {
        if (driver.getLicenseNumber() == null || driver.getLicenseNumber().trim().isEmpty()) {
            throw new InvalidQuoteRequestException("Driver license number is required");
        }
        
        if (driver.getLicenseState() == null || driver.getLicenseState().trim().isEmpty()) {
            throw new InvalidQuoteRequestException("Driver license state is required");
        }
    }
    
    @Cacheable(value = "validationPatternCache", key = "#birthDate.toString() + '-' + T(java.time.LocalDate).now().getYear()")
    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}