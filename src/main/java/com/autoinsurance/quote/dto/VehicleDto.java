package com.autoinsurance.quote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(
    name = "Vehicle",
    description = "Vehicle information for insurance quote calculation",
    example = """
        {
            "make": "Honda",
            "model": "Accord",
            "year": 2021,
            "vin": "1HGCV1F31JA123456",
            "currentValue": 30000.00
        }
        """
)
public class VehicleDto {
    
    @Schema(
        description = "Vehicle manufacturer/brand",
        example = "Honda",
        required = true
    )
    @NotBlank(message = "Vehicle make is required")
    private String make;
    
    @Schema(
        description = "Vehicle model name",
        example = "Accord",
        required = true
    )
    @NotBlank(message = "Vehicle model is required")
    private String model;
    
    @Schema(
        description = "Manufacturing year of the vehicle",
        example = "2021",
        minimum = "1900",
        maximum = "2025",
        required = true
    )
    @NotNull(message = "Vehicle year is required")
    @Min(value = 1900, message = "Invalid vehicle year")
    @Max(value = 2025, message = "Invalid vehicle year")
    private Integer year;
    
    @Schema(
        description = "Vehicle Identification Number (17-character alphanumeric code)",
        example = "1HGCV1F31JA123456",
        pattern = "^[A-HJ-NPR-Z0-9]{17}$",
        required = true
    )
    @NotBlank(message = "VIN is required")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Invalid VIN format")
    private String vin;
    
    @Schema(
        description = "Current market value of the vehicle in USD",
        example = "30000.00",
        minimum = "0",
        required = true
    )
    @NotNull(message = "Vehicle current value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Vehicle value must be positive")
    @DecimalMax(value = "1000000.00", message = "Vehicle value cannot exceed $1,000,000")
    private BigDecimal currentValue;

    // Constructors
    public VehicleDto() {}
    
    public VehicleDto(String make, String model, Integer year, String vin, BigDecimal currentValue) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.vin = vin;
        this.currentValue = currentValue;
    }

    // Builder pattern
    public static VehicleDtoBuilder builder() {
        return new VehicleDtoBuilder();
    }
    
    // Getters and Setters
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    
    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }
    
    public static class VehicleDtoBuilder {
        private String make;
        private String model;
        private Integer year;
        private String vin;
        private BigDecimal currentValue;
        
        public VehicleDtoBuilder make(String make) { this.make = make; return this; }
        public VehicleDtoBuilder model(String model) { this.model = model; return this; }
        public VehicleDtoBuilder year(Integer year) { this.year = year; return this; }
        public VehicleDtoBuilder vin(String vin) { this.vin = vin; return this; }
        public VehicleDtoBuilder currentValue(BigDecimal currentValue) { this.currentValue = currentValue; return this; }
        
        public VehicleDto build() {
            return new VehicleDto(make, model, year, vin, currentValue);
        }
    }
}