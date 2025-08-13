package com.autoinsurance.quote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Schema(
    name = "Driver", 
    description = "Driver information for insurance quote calculation"
)
public class DriverDto {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "License number is required")
    private String licenseNumber;
    
    @NotBlank(message = "License state is required")
    @Size(min = 2, max = 2, message = "License state must be 2 characters")
    private String licenseState;
    
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;
    
    private Boolean safeDriverDiscount = false;
    private Boolean multiPolicyDiscount = false;
    
    // Constructors
    public DriverDto() {}
    
    public DriverDto(String firstName, String lastName, LocalDate dateOfBirth, String licenseNumber, 
                    String licenseState, Integer yearsOfExperience, Boolean safeDriverDiscount, Boolean multiPolicyDiscount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.licenseNumber = licenseNumber;
        this.licenseState = licenseState;
        this.yearsOfExperience = yearsOfExperience;
        this.safeDriverDiscount = safeDriverDiscount;
        this.multiPolicyDiscount = multiPolicyDiscount;
    }

    // Builder
    public static DriverDtoBuilder builder() {
        return new DriverDtoBuilder();
    }
    
    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getLicenseState() { return licenseState; }
    public void setLicenseState(String licenseState) { this.licenseState = licenseState; }
    
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    
    public Boolean getSafeDriverDiscount() { return safeDriverDiscount; }
    public void setSafeDriverDiscount(Boolean safeDriverDiscount) { this.safeDriverDiscount = safeDriverDiscount; }
    
    public Boolean getMultiPolicyDiscount() { return multiPolicyDiscount; }
    public void setMultiPolicyDiscount(Boolean multiPolicyDiscount) { this.multiPolicyDiscount = multiPolicyDiscount; }
    
    public static class DriverDtoBuilder {
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String licenseNumber;
        private String licenseState;
        private Integer yearsOfExperience;
        private Boolean safeDriverDiscount = false;
        private Boolean multiPolicyDiscount = false;
        
        public DriverDtoBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public DriverDtoBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public DriverDtoBuilder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public DriverDtoBuilder licenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; return this; }
        public DriverDtoBuilder licenseState(String licenseState) { this.licenseState = licenseState; return this; }
        public DriverDtoBuilder yearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; return this; }
        public DriverDtoBuilder safeDriverDiscount(Boolean safeDriverDiscount) { this.safeDriverDiscount = safeDriverDiscount; return this; }
        public DriverDtoBuilder multiPolicyDiscount(Boolean multiPolicyDiscount) { this.multiPolicyDiscount = multiPolicyDiscount; return this; }
        
        public DriverDto build() {
            return new DriverDto(firstName, lastName, dateOfBirth, licenseNumber, licenseState, 
                               yearsOfExperience, safeDriverDiscount, multiPolicyDiscount);
        }
    }
}