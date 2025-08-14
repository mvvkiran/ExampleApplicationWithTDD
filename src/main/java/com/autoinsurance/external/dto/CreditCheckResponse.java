package com.autoinsurance.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Credit Check Service Response DTO
 * 
 * Represents the response structure from external credit check API.
 * Based on Pact contract specifications.
 */
public class CreditCheckResponse {

    @JsonProperty("creditScore")
    private int creditScore;
    
    @JsonProperty("creditTier")
    private String creditTier;
    
    @JsonProperty("discountEligible")
    private boolean discountEligible;
    
    @JsonProperty("discountPercentage")
    private int discountPercentage;
    
    @JsonProperty("reportDate")
    private String reportDate;

    public CreditCheckResponse() {}

    // Getters and setters
    public int getCreditScore() { return creditScore; }
    public void setCreditScore(int creditScore) { this.creditScore = creditScore; }

    public String getCreditTier() { return creditTier; }
    public void setCreditTier(String creditTier) { this.creditTier = creditTier; }

    public boolean isDiscountEligible() { return discountEligible; }
    public void setDiscountEligible(boolean discountEligible) { this.discountEligible = discountEligible; }

    public int getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(int discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }
}