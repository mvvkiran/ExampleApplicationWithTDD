package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Service
public class RiskCalculationServiceImpl implements RiskCalculationService {

    private static final Logger log = LoggerFactory.getLogger(RiskCalculationServiceImpl.class);
    
    @Value("${insurance.quote.base-premium:500.00}")
    private BigDecimal basePremium;

    @Override
    public BigDecimal calculateBasePremium(QuoteRequestDto request) {
        log.debug("Calculating base premium for coverage amount: {}", request.getCoverageAmount());
        
        BigDecimal premium = basePremium;
        
        // Coverage amount factor
        BigDecimal coverageFactor = request.getCoverageAmount()
                .divide(BigDecimal.valueOf(100000), 2, java.math.RoundingMode.HALF_UP);
        premium = premium.multiply(coverageFactor);
        
        // Deductible factor (lower deductible = higher premium)
        BigDecimal deductibleFactor = BigDecimal.valueOf(1000)
                .divide(request.getDeductible(), 2, java.math.RoundingMode.HALF_UP);
        premium = premium.multiply(deductibleFactor);
        
        // Vehicle age factor
        int vehicleAge = LocalDate.now().getYear() - request.getVehicle().getYear();
        BigDecimal ageFactor = BigDecimal.valueOf(1.0 + (vehicleAge * 0.02)); // 2% increase per year
        premium = premium.multiply(ageFactor);
        
        // Driver risk factors
        for (DriverDto driver : request.getDrivers()) {
            premium = applyDriverRiskFactors(premium, driver);
        }
        
        return premium.setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    private BigDecimal applyDriverRiskFactors(BigDecimal premium, DriverDto driver) {
        int age = calculateAge(driver.getDateOfBirth());
        
        // Age risk factor
        BigDecimal ageRiskFactor;
        if (age < 25) {
            ageRiskFactor = BigDecimal.valueOf(1.5); // Higher risk for young drivers
        } else if (age < 65) {
            ageRiskFactor = BigDecimal.valueOf(1.0); // Standard risk
        } else {
            ageRiskFactor = BigDecimal.valueOf(1.2); // Slightly higher risk for older drivers
        }
        
        premium = premium.multiply(ageRiskFactor);
        
        // Experience factor
        Integer yearsOfExperience = driver.getYearsOfExperience();
        if (yearsOfExperience != null && yearsOfExperience > 5) {
            BigDecimal experienceFactor = BigDecimal.valueOf(0.95); // 5% discount for experienced drivers
            premium = premium.multiply(experienceFactor);
        }
        
        return premium;
    }
    
    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}