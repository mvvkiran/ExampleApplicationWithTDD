package com.autoinsurance.quote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Schema(
    name = "QuoteRequest",
    description = "Request object for generating auto insurance quotes. Contains vehicle information, driver profiles, and coverage requirements.",
    example = """
        {
            "vehicle": {
                "make": "Honda",
                "model": "Accord",
                "year": 2021,
                "vin": "1HGCV1F31JA123456",
                "currentValue": 30000.00
            },
            "drivers": [
                {
                    "firstName": "Jane",
                    "lastName": "Smith",
                    "dateOfBirth": "1990-03-20",
                    "licenseNumber": "S987654321",
                    "licenseState": "NY",
                    "yearsOfExperience": 10,
                    "safeDriverDiscount": true,
                    "multiPolicyDiscount": true
                }
            ],
            "coverageAmount": 150000.00,
            "deductible": 1000.00
        }
        """
)
public class QuoteRequestDto {
    
    @Schema(
        description = "Vehicle information including make, model, year, VIN, and current market value",
        required = true,
        implementation = VehicleDto.class
    )
    @NotNull(message = "Vehicle information is required")
    @Valid
    private VehicleDto vehicle;
    
    @Schema(
        description = "List of drivers to be covered under the policy. At least one primary driver is required (maximum 4 drivers).",
        required = true,
        example = """
            [
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "dateOfBirth": "1985-06-15",
                    "licenseNumber": "D123456789",
                    "licenseState": "CA",
                    "yearsOfExperience": 12
                }
            ]
            """
    )
    @NotEmpty(message = "At least one driver is required")
    @Valid
    private List<DriverDto> drivers;
    
    @Schema(
        description = "Total coverage amount in USD. This represents the maximum payout for covered damages.",
        required = true,
        minimum = "25000",
        maximum = "1000000",
        example = "250000.00"
    )
    @NotNull(message = "Coverage amount is required")
    @DecimalMin(value = "25000", message = "Coverage amount must be at least $25,000")
    @DecimalMax(value = "1000000", message = "Coverage amount cannot exceed $1,000,000")
    private BigDecimal coverageAmount;
    
    @Schema(
        description = "Deductible amount in USD. The amount you pay out-of-pocket before insurance coverage begins.",
        required = true,
        minimum = "250",
        maximum = "10000",
        example = "1000.00"
    )
    @NotNull(message = "Deductible is required")
    @DecimalMin(value = "250", message = "Deductible must be at least $250")
    @DecimalMax(value = "10000", message = "Deductible cannot exceed $10,000")
    private BigDecimal deductible;
    
    // Constructors
    public QuoteRequestDto() {}
    
    public QuoteRequestDto(VehicleDto vehicle, List<DriverDto> drivers, BigDecimal coverageAmount, BigDecimal deductible) {
        this.vehicle = vehicle;
        this.drivers = drivers;
        this.coverageAmount = coverageAmount;
        this.deductible = deductible;
    }
    
    // Builder
    public static QuoteRequestDtoBuilder builder() {
        return new QuoteRequestDtoBuilder();
    }
    
    // Getters and Setters
    public VehicleDto getVehicle() { return vehicle; }
    public void setVehicle(VehicleDto vehicle) { this.vehicle = vehicle; }
    
    public List<DriverDto> getDrivers() { return drivers; }
    public void setDrivers(List<DriverDto> drivers) { this.drivers = drivers; }
    
    public BigDecimal getCoverageAmount() { return coverageAmount; }
    public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }
    
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
    
    public static class QuoteRequestDtoBuilder {
        private VehicleDto vehicle;
        private List<DriverDto> drivers;
        private BigDecimal coverageAmount;
        private BigDecimal deductible;
        
        public QuoteRequestDtoBuilder vehicle(VehicleDto vehicle) { this.vehicle = vehicle; return this; }
        public QuoteRequestDtoBuilder drivers(List<DriverDto> drivers) { this.drivers = drivers; return this; }
        public QuoteRequestDtoBuilder coverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; return this; }
        public QuoteRequestDtoBuilder deductible(BigDecimal deductible) { this.deductible = deductible; return this; }
        
        public QuoteRequestDto build() {
            return new QuoteRequestDto(vehicle, drivers, coverageAmount, deductible);
        }
    }
}