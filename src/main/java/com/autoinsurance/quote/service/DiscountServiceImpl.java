package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiscountServiceImpl implements DiscountService {

    private static final Logger log = LoggerFactory.getLogger(DiscountServiceImpl.class);
    
    private final RiskCalculationService riskCalculationService;
    
    public DiscountServiceImpl(RiskCalculationService riskCalculationService) {
        this.riskCalculationService = riskCalculationService;
    }

    @Override
    public BigDecimal calculateTotalDiscount(QuoteRequestDto request) {
        log.debug("Calculating discounts for quote request");
        
        BigDecimal basePremium = riskCalculationService.calculateBasePremium(request);
        BigDecimal totalDiscountPercentage = BigDecimal.ZERO;
        
        for (DriverDto driver : request.getDrivers()) {
            if (Boolean.TRUE.equals(driver.getSafeDriverDiscount())) {
                totalDiscountPercentage = totalDiscountPercentage.add(BigDecimal.valueOf(0.15)); // 15% discount
                log.debug("Applied safe driver discount for driver: {}", driver.getFirstName());
            }
            
            if (Boolean.TRUE.equals(driver.getMultiPolicyDiscount())) {
                totalDiscountPercentage = totalDiscountPercentage.add(BigDecimal.valueOf(0.10)); // 10% discount
                log.debug("Applied multi-policy discount for driver: {}", driver.getFirstName());
            }
        }
        
        // Cap total discount at 25%
        if (totalDiscountPercentage.compareTo(BigDecimal.valueOf(0.25)) > 0) {
            totalDiscountPercentage = BigDecimal.valueOf(0.25);
        }
        
        BigDecimal totalDiscount = basePremium.multiply(totalDiscountPercentage);
        
        log.info("Total discount applied: {}% (${}) for quote", 
                totalDiscountPercentage.multiply(BigDecimal.valueOf(100)), totalDiscount);
        
        return totalDiscount.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    @Override
    public List<String> getAppliedDiscounts(QuoteRequestDto request) {
        List<String> appliedDiscounts = new ArrayList<>();
        
        for (DriverDto driver : request.getDrivers()) {
            if (Boolean.TRUE.equals(driver.getSafeDriverDiscount())) {
                appliedDiscounts.add("Safe Driver Discount - 15%");
            }
            
            if (Boolean.TRUE.equals(driver.getMultiPolicyDiscount())) {
                appliedDiscounts.add("Multi-Policy Discount - 10%");
            }
        }
        
        return appliedDiscounts;
    }
}