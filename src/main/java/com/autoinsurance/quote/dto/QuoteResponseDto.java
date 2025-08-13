package com.autoinsurance.quote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Schema(
    name = "QuoteResponse",
    description = "Response object containing generated quote details with premium calculations, coverage information, and applied discounts.",
    example = """
        {
            "quoteId": "q-12345678-abcd-4567-89ef-123456789012",
            "premium": 1200.00,
            "monthlyPremium": 100.00,
            "coverageAmount": 250000.00,
            "deductible": 1000.00,
            "validUntil": "2024-09-15",
            "discountsApplied": [
                "Safe Driver Discount - 15%",
                "Multi-Policy Discount - 10%"
            ]
        }
        """
)
public class QuoteResponseDto {
    
    @Schema(
        description = "Unique identifier for the generated quote",
        example = "q-12345678-abcd-4567-89ef-123456789012",
        pattern = "^q-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
    )
    private String quoteId;
    
    @Schema(
        description = "Annual premium amount in USD",
        example = "1200.00",
        minimum = "0"
    )
    private BigDecimal premium;
    
    @Schema(
        description = "Monthly premium amount in USD (annual premium divided by 12)",
        example = "100.00",
        minimum = "0"
    )
    private BigDecimal monthlyPremium;
    
    @Schema(
        description = "Total coverage amount in USD",
        example = "250000.00",
        minimum = "25000",
        maximum = "1000000"
    )
    private BigDecimal coverageAmount;
    
    @Schema(
        description = "Deductible amount in USD",
        example = "1000.00",
        minimum = "250",
        maximum = "10000"
    )
    private BigDecimal deductible;
    
    @Schema(
        description = "Date until which this quote is valid (30 days from generation)",
        example = "2024-09-15",
        pattern = "YYYY-MM-DD"
    )
    private LocalDate validUntil;
    
    @Schema(
        description = "List of discounts applied to the base premium",
        example = """
            [
                "Safe Driver Discount - 15%",
                "Multi-Policy Discount - 10%",
                "Good Student Discount - 5%"
            ]
            """
    )
    private List<String> discountsApplied = new ArrayList<>();
    
    // Constructors
    public QuoteResponseDto() {}
    
    public QuoteResponseDto(String quoteId, BigDecimal premium, BigDecimal monthlyPremium, 
                           BigDecimal coverageAmount, BigDecimal deductible, LocalDate validUntil, 
                           List<String> discountsApplied) {
        this.quoteId = quoteId;
        this.premium = premium;
        this.monthlyPremium = monthlyPremium;
        this.coverageAmount = coverageAmount;
        this.deductible = deductible;
        this.validUntil = validUntil;
        this.discountsApplied = discountsApplied != null ? discountsApplied : new ArrayList<>();
    }
    
    // Builder
    public static QuoteResponseDtoBuilder builder() {
        return new QuoteResponseDtoBuilder();
    }
    
    // Getters and Setters
    public String getQuoteId() { return quoteId; }
    public void setQuoteId(String quoteId) { this.quoteId = quoteId; }
    
    public BigDecimal getPremium() { return premium; }
    public void setPremium(BigDecimal premium) { this.premium = premium; }
    
    public BigDecimal getMonthlyPremium() { return monthlyPremium; }
    public void setMonthlyPremium(BigDecimal monthlyPremium) { this.monthlyPremium = monthlyPremium; }
    
    public BigDecimal getCoverageAmount() { return coverageAmount; }
    public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }
    
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
    
    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
    
    public List<String> getDiscountsApplied() { return discountsApplied; }
    public void setDiscountsApplied(List<String> discountsApplied) { 
        this.discountsApplied = discountsApplied != null ? discountsApplied : new ArrayList<>();
    }
    
    public static class QuoteResponseDtoBuilder {
        private String quoteId;
        private BigDecimal premium;
        private BigDecimal monthlyPremium;
        private BigDecimal coverageAmount;
        private BigDecimal deductible;
        private LocalDate validUntil;
        private List<String> discountsApplied = new ArrayList<>();
        
        public QuoteResponseDtoBuilder quoteId(String quoteId) { this.quoteId = quoteId; return this; }
        public QuoteResponseDtoBuilder premium(BigDecimal premium) { this.premium = premium; return this; }
        public QuoteResponseDtoBuilder monthlyPremium(BigDecimal monthlyPremium) { this.monthlyPremium = monthlyPremium; return this; }
        public QuoteResponseDtoBuilder coverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; return this; }
        public QuoteResponseDtoBuilder deductible(BigDecimal deductible) { this.deductible = deductible; return this; }
        public QuoteResponseDtoBuilder validUntil(LocalDate validUntil) { this.validUntil = validUntil; return this; }
        public QuoteResponseDtoBuilder discountsApplied(List<String> discountsApplied) { this.discountsApplied = discountsApplied; return this; }
        
        public QuoteResponseDto build() {
            return new QuoteResponseDto(quoteId, premium, monthlyPremium, coverageAmount, 
                                      deductible, validUntil, discountsApplied);
        }
    }
}