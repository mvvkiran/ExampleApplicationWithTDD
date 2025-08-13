package com.autoinsurance.quote.service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Value object representing the results of premium calculation.
 * Encapsulates all premium-related calculations in a immutable structure.
 */
public class PremiumCalculation {
    
    private final BigDecimal basePremium;
    private final BigDecimal totalDiscount;
    private final BigDecimal finalPremium;
    private final BigDecimal monthlyPremium;
    private final List<String> appliedDiscounts;
    
    public PremiumCalculation(BigDecimal basePremium, 
                            BigDecimal totalDiscount, 
                            BigDecimal finalPremium, 
                            BigDecimal monthlyPremium, 
                            List<String> appliedDiscounts) {
        this.basePremium = basePremium;
        this.totalDiscount = totalDiscount;
        this.finalPremium = finalPremium;
        this.monthlyPremium = monthlyPremium;
        this.appliedDiscounts = appliedDiscounts != null ? List.copyOf(appliedDiscounts) : List.of();
    }
    
    public BigDecimal getBasePremium() {
        return basePremium;
    }
    
    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }
    
    public BigDecimal getFinalPremium() {
        return finalPremium;
    }
    
    public BigDecimal getMonthlyPremium() {
        return monthlyPremium;
    }
    
    public List<String> getAppliedDiscounts() {
        return appliedDiscounts;
    }
    
    @Override
    public String toString() {
        return "PremiumCalculation{" +
                "basePremium=" + basePremium +
                ", totalDiscount=" + totalDiscount +
                ", finalPremium=" + finalPremium +
                ", monthlyPremium=" + monthlyPremium +
                ", appliedDiscounts=" + appliedDiscounts +
                '}';
    }
}