package com.autoinsurance.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Risk Assessment Service Request DTO
 * 
 * Represents the request structure for external risk assessment API calls.
 * Based on Pact contract specifications.
 */
public class RiskAssessmentRequest {

    @JsonProperty("driverAge")
    private int driverAge;
    
    @JsonProperty("vehicleAge")
    private int vehicleAge;
    
    @JsonProperty("yearsOfExperience")
    private int yearsOfExperience;
    
    @JsonProperty("vehicleValue")
    private BigDecimal vehicleValue;

    public RiskAssessmentRequest() {}

    public RiskAssessmentRequest(int driverAge, int vehicleAge, int yearsOfExperience, BigDecimal vehicleValue) {
        this.driverAge = driverAge;
        this.vehicleAge = vehicleAge;
        this.yearsOfExperience = yearsOfExperience;
        this.vehicleValue = vehicleValue;
    }

    // Getters and setters
    public int getDriverAge() { return driverAge; }
    public void setDriverAge(int driverAge) { this.driverAge = driverAge; }

    public int getVehicleAge() { return vehicleAge; }
    public void setVehicleAge(int vehicleAge) { this.vehicleAge = vehicleAge; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public BigDecimal getVehicleValue() { return vehicleValue; }
    public void setVehicleValue(BigDecimal vehicleValue) { this.vehicleValue = vehicleValue; }
}