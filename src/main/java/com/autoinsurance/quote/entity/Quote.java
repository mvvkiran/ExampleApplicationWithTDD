package com.autoinsurance.quote.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotes")
public class Quote {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private BigDecimal premium;
    
    @Column(nullable = false)
    private BigDecimal monthlyPremium;
    
    @Column(nullable = false)
    private BigDecimal coverageAmount;
    
    @Column(nullable = false)
    private BigDecimal deductible;
    
    @Column(nullable = false)
    private LocalDate validUntil;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    // Vehicle information
    @Column(nullable = false)
    private String vehicleMake;
    
    @Column(nullable = false)
    private String vehicleModel;
    
    @Column(nullable = false)
    private Integer vehicleYear;
    
    @Column(nullable = false)
    private String vehicleVin;
    
    @Column(nullable = false)
    private BigDecimal vehicleCurrentValue;
    
    // Primary driver information
    @Column(nullable = false)
    private String primaryDriverName;
    
    @Column(nullable = false)
    private String primaryDriverLicense;
    
    @ElementCollection
    @CollectionTable(name = "quote_discounts", joinColumns = @JoinColumn(name = "quote_id"))
    @Column(name = "discount_description")
    private List<String> discountsApplied;
    
    // Constructors
    public Quote() {}
    
    public Quote(String id, BigDecimal premium, BigDecimal monthlyPremium, BigDecimal coverageAmount,
                BigDecimal deductible, LocalDate validUntil, LocalDateTime createdAt,
                String vehicleMake, String vehicleModel, Integer vehicleYear, String vehicleVin,
                BigDecimal vehicleCurrentValue, String primaryDriverName, String primaryDriverLicense,
                List<String> discountsApplied) {
        this.id = id;
        this.premium = premium;
        this.monthlyPremium = monthlyPremium;
        this.coverageAmount = coverageAmount;
        this.deductible = deductible;
        this.validUntil = validUntil;
        this.createdAt = createdAt;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleYear = vehicleYear;
        this.vehicleVin = vehicleVin;
        this.vehicleCurrentValue = vehicleCurrentValue;
        this.primaryDriverName = primaryDriverName;
        this.primaryDriverLicense = primaryDriverLicense;
        this.discountsApplied = discountsApplied != null ? discountsApplied : new ArrayList<>();
    }

    // Builder
    public static QuoteBuilder builder() {
        return new QuoteBuilder();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
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
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getVehicleMake() { return vehicleMake; }
    public void setVehicleMake(String vehicleMake) { this.vehicleMake = vehicleMake; }
    
    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    
    public Integer getVehicleYear() { return vehicleYear; }
    public void setVehicleYear(Integer vehicleYear) { this.vehicleYear = vehicleYear; }
    
    public String getVehicleVin() { return vehicleVin; }
    public void setVehicleVin(String vehicleVin) { this.vehicleVin = vehicleVin; }
    
    public BigDecimal getVehicleCurrentValue() { return vehicleCurrentValue; }
    public void setVehicleCurrentValue(BigDecimal vehicleCurrentValue) { this.vehicleCurrentValue = vehicleCurrentValue; }
    
    public String getPrimaryDriverName() { return primaryDriverName; }
    public void setPrimaryDriverName(String primaryDriverName) { this.primaryDriverName = primaryDriverName; }
    
    public String getPrimaryDriverLicense() { return primaryDriverLicense; }
    public void setPrimaryDriverLicense(String primaryDriverLicense) { this.primaryDriverLicense = primaryDriverLicense; }
    
    public List<String> getDiscountsApplied() { return discountsApplied; }
    public void setDiscountsApplied(List<String> discountsApplied) { 
        this.discountsApplied = discountsApplied != null ? discountsApplied : new ArrayList<>(); 
    }

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    public static class QuoteBuilder {
        private String id;
        private BigDecimal premium;
        private BigDecimal monthlyPremium;
        private BigDecimal coverageAmount;
        private BigDecimal deductible;
        private LocalDate validUntil;
        private LocalDateTime createdAt;
        private String vehicleMake;
        private String vehicleModel;
        private Integer vehicleYear;
        private String vehicleVin;
        private BigDecimal vehicleCurrentValue;
        private String primaryDriverName;
        private String primaryDriverLicense;
        private List<String> discountsApplied = new ArrayList<>();
        
        public QuoteBuilder id(String id) { this.id = id; return this; }
        public QuoteBuilder premium(BigDecimal premium) { this.premium = premium; return this; }
        public QuoteBuilder monthlyPremium(BigDecimal monthlyPremium) { this.monthlyPremium = monthlyPremium; return this; }
        public QuoteBuilder coverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; return this; }
        public QuoteBuilder deductible(BigDecimal deductible) { this.deductible = deductible; return this; }
        public QuoteBuilder validUntil(LocalDate validUntil) { this.validUntil = validUntil; return this; }
        public QuoteBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public QuoteBuilder vehicleMake(String vehicleMake) { this.vehicleMake = vehicleMake; return this; }
        public QuoteBuilder vehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; return this; }
        public QuoteBuilder vehicleYear(Integer vehicleYear) { this.vehicleYear = vehicleYear; return this; }
        public QuoteBuilder vehicleVin(String vehicleVin) { this.vehicleVin = vehicleVin; return this; }
        public QuoteBuilder vehicleCurrentValue(BigDecimal vehicleCurrentValue) { this.vehicleCurrentValue = vehicleCurrentValue; return this; }
        public QuoteBuilder primaryDriverName(String primaryDriverName) { this.primaryDriverName = primaryDriverName; return this; }
        public QuoteBuilder primaryDriverLicense(String primaryDriverLicense) { this.primaryDriverLicense = primaryDriverLicense; return this; }
        public QuoteBuilder discountsApplied(List<String> discountsApplied) { this.discountsApplied = discountsApplied; return this; }
        
        public Quote build() {
            return new Quote(id, premium, monthlyPremium, coverageAmount, deductible, validUntil, 
                           createdAt, vehicleMake, vehicleModel, vehicleYear, vehicleVin, 
                           vehicleCurrentValue, primaryDriverName, primaryDriverLicense, discountsApplied);
        }
    }
}