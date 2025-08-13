package com.autoinsurance.quote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Configuration class for quote validation parameters.
 * Allows validation rules to be externalized and easily modified without code changes.
 */
@Component
@ConfigurationProperties(prefix = "autoinsurance.quote.validation")
public class QuoteValidationConfig {
    
    private static final String DEFAULT_VIN_PATTERN = "^[A-HJ-NPR-Z0-9]{17}$";
    
    private String vinPattern = DEFAULT_VIN_PATTERN;
    private int minDriverAge = 18;
    private int maxDriverAge = 85;
    private int maxVehicleAge = 20;
    
    private Pattern compiledVinPattern;
    
    public QuoteValidationConfig() {
        this.compiledVinPattern = Pattern.compile(vinPattern);
    }
    
    public String getVinPattern() {
        return vinPattern;
    }
    
    public void setVinPattern(String vinPattern) {
        this.vinPattern = vinPattern;
        this.compiledVinPattern = Pattern.compile(vinPattern);
    }
    
    public Pattern getCompiledVinPattern() {
        return compiledVinPattern;
    }
    
    public int getMinDriverAge() {
        return minDriverAge;
    }
    
    public void setMinDriverAge(int minDriverAge) {
        this.minDriverAge = minDriverAge;
    }
    
    public int getMaxDriverAge() {
        return maxDriverAge;
    }
    
    public void setMaxDriverAge(int maxDriverAge) {
        this.maxDriverAge = maxDriverAge;
    }
    
    public int getMaxVehicleAge() {
        return maxVehicleAge;
    }
    
    public void setMaxVehicleAge(int maxVehicleAge) {
        this.maxVehicleAge = maxVehicleAge;
    }
}