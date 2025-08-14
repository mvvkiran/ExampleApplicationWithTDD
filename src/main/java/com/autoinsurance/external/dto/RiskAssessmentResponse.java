package com.autoinsurance.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Risk Assessment Service Response DTO
 * 
 * Represents the response structure from external risk assessment API.
 * Based on Pact contract specifications.
 */
public class RiskAssessmentResponse {

    @JsonProperty("riskScore")
    private double riskScore;
    
    @JsonProperty("riskCategory")
    private String riskCategory;
    
    @JsonProperty("riskFactors")
    private String[] riskFactors;
    
    @JsonProperty("baseMultiplier")
    private double baseMultiplier;

    public RiskAssessmentResponse() {}

    // Getters and setters
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }

    public String getRiskCategory() { return riskCategory; }
    public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }

    public String[] getRiskFactors() { return riskFactors; }
    public void setRiskFactors(String[] riskFactors) { this.riskFactors = riskFactors; }

    public double getBaseMultiplier() { return baseMultiplier; }
    public void setBaseMultiplier(double baseMultiplier) { this.baseMultiplier = baseMultiplier; }
}