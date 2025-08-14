package com.autoinsurance.quote.service.validation;

import com.autoinsurance.quote.config.QuoteValidationConfig;
import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.VehicleDto;
import com.autoinsurance.quote.exception.InvalidQuoteRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Quote Validation Service Implementation Tests")
class QuoteValidationServiceImplTest {

    private QuoteValidationConfig validationConfig;
    private QuoteValidationServiceImpl validationService;
    
    // Test data constants
    private static final String VALID_VIN = "1HGBH41JXMN109186";
    private static final String INVALID_VIN = "INVALID";
    private static final int MIN_DRIVER_AGE = 16;
    private static final int MAX_DRIVER_AGE = 100;
    private static final int MAX_VEHICLE_AGE = 30;
    
    @BeforeEach
    void setUp() {
        // Create real instance with custom settings
        validationConfig = new QuoteValidationConfig();
        validationConfig.setMinDriverAge(MIN_DRIVER_AGE);
        validationConfig.setMaxDriverAge(MAX_DRIVER_AGE);
        validationConfig.setMaxVehicleAge(MAX_VEHICLE_AGE);
        
        validationService = new QuoteValidationServiceImpl(validationConfig);
    }

    @Nested
    @DisplayName("Quote Request Validation Tests")
    class QuoteRequestValidationTests {
        
        @Test
        @DisplayName("Should validate successful quote request")
        void should_ValidateSuccessfulQuoteRequest() {
            // Given
            QuoteRequestDto request = createValidQuoteRequest();
            
            // When & Then
            assertDoesNotThrow(() -> validationService.validateQuoteRequest(request));
        }
        
        @Test
        @DisplayName("Should throw exception when quote request is null")
        void should_ThrowException_When_QuoteRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> validationService.validateQuoteRequest(null))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Quote request cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when vehicle is null")
        void should_ThrowException_When_VehicleIsNull() {
            // Given
            QuoteRequestDto request = createValidQuoteRequest();
            request.setVehicle(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateQuoteRequest(request))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Vehicle information is required");
        }
        
        @Test
        @DisplayName("Should throw exception when drivers list is null")
        void should_ThrowException_When_DriversListIsNull() {
            // Given
            QuoteRequestDto request = createValidQuoteRequest();
            request.setDrivers(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateQuoteRequest(request))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("At least one driver is required");
        }
        
        @Test
        @DisplayName("Should throw exception when drivers list is empty")
        void should_ThrowException_When_DriversListIsEmpty() {
            // Given
            QuoteRequestDto request = createValidQuoteRequest();
            request.setDrivers(new ArrayList<>());
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateQuoteRequest(request))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("At least one driver is required");
        }
        
        @Test
        @DisplayName("Should throw exception when coverage amount is null")
        void should_ThrowException_When_CoverageAmountIsNull() {
            // Given
            QuoteRequestDto request = createValidQuoteRequest();
            request.setCoverageAmount(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateQuoteRequest(request))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Valid coverage amount is required");
        }
        
        @Test
        @DisplayName("Should throw exception when coverage amount is zero or negative")
        void should_ThrowException_When_CoverageAmountIsZeroOrNegative() {
            // Given
            QuoteRequestDto request = createValidQuoteRequest();
            request.setCoverageAmount(BigDecimal.ZERO);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateQuoteRequest(request))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Valid coverage amount is required");
        }
        
        @Test
        @DisplayName("Should throw exception when deductible is null")
        void should_ThrowException_When_DeductibleIsNull() {
            // Given
            QuoteRequestDto request = createValidQuoteRequest();
            request.setDeductible(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateQuoteRequest(request))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Valid deductible amount is required");
        }
        
        @Test
        @DisplayName("Should throw exception when deductible is negative")
        void should_ThrowException_When_DeductibleIsNegative() {
            // Given
            QuoteRequestDto request = createValidQuoteRequest();
            request.setDeductible(new BigDecimal("-500"));
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateQuoteRequest(request))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Valid deductible amount is required");
        }
    }

    @Nested
    @DisplayName("Vehicle Validation Tests")
    class VehicleValidationTests {
        
        @Test
        @DisplayName("Should validate successful vehicle")
        void should_ValidateSuccessfulVehicle() {
            // Given
            VehicleDto vehicle = createValidVehicle();
            
            // When & Then
            assertDoesNotThrow(() -> validationService.validateVehicle(vehicle));
        }
        
        @Test
        @DisplayName("Should throw exception when vehicle is null")
        void should_ThrowException_When_VehicleIsNull() {
            // When & Then
            assertThatThrownBy(() -> validationService.validateVehicle(null))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Vehicle information is required");
        }
        
        @Test
        @DisplayName("Should throw exception when VIN is null")
        void should_ThrowException_When_VinIsNull() {
            // Given
            VehicleDto vehicle = createValidVehicle();
            vehicle.setVin(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateVehicle(vehicle))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("VIN is required");
        }
        
        @Test
        @DisplayName("Should throw exception when VIN is empty")
        void should_ThrowException_When_VinIsEmpty() {
            // Given
            VehicleDto vehicle = createValidVehicle();
            vehicle.setVin("");
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateVehicle(vehicle))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("VIN is required");
        }
        
        @Test
        @DisplayName("Should throw exception when VIN format is invalid")
        void should_ThrowException_When_VinFormatIsInvalid() {
            // Given
            VehicleDto vehicle = createValidVehicle();
            vehicle.setVin(INVALID_VIN);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateVehicle(vehicle))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage(String.format("Invalid VIN format: %s. Expected format: %s", INVALID_VIN, validationConfig.getVinPattern()));
        }
        
        @Test
        @DisplayName("Should throw exception when vehicle year is null")
        void should_ThrowException_When_VehicleYearIsNull() {
            // Given
            VehicleDto vehicle = createValidVehicle();
            vehicle.setYear(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateVehicle(vehicle))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Vehicle year is required");
        }
        
        @Test
        @DisplayName("Should throw exception when vehicle year is in future")
        void should_ThrowException_When_VehicleYearIsInFuture() {
            // Given
            VehicleDto vehicle = createValidVehicle();
            vehicle.setYear(LocalDate.now().getYear() + 1);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateVehicle(vehicle))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Vehicle year cannot be in the future");
        }
        
        @Test
        @DisplayName("Should throw exception when vehicle is too old")
        void should_ThrowException_When_VehicleIsTooOld() {
            // Given
            VehicleDto vehicle = createValidVehicle();
            int tooOldYear = LocalDate.now().getYear() - MAX_VEHICLE_AGE - 1;
            vehicle.setYear(tooOldYear);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateVehicle(vehicle))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessageContaining("Vehicle age exceeds maximum limit");
        }
        
        @Test
        @DisplayName("Should throw exception when vehicle make is null")
        void should_ThrowException_When_VehicleMakeIsNull() {
            // Given
            VehicleDto vehicle = createValidVehicle();
            vehicle.setMake(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateVehicle(vehicle))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Vehicle make is required");
        }
        
        @Test
        @DisplayName("Should throw exception when vehicle model is null")
        void should_ThrowException_When_VehicleModelIsNull() {
            // Given
            VehicleDto vehicle = createValidVehicle();
            vehicle.setModel(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateVehicle(vehicle))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Vehicle model is required");
        }
        
        @Test
        @DisplayName("Should throw exception when vehicle current value is null")
        void should_ThrowException_When_VehicleCurrentValueIsNull() {
            // Given
            VehicleDto vehicle = createValidVehicle();
            vehicle.setCurrentValue(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateVehicle(vehicle))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Valid vehicle current value is required");
        }
        
        @Test
        @DisplayName("Should throw exception when vehicle current value is zero or negative")
        void should_ThrowException_When_VehicleCurrentValueIsZeroOrNegative() {
            // Given
            VehicleDto vehicle = createValidVehicle();
            vehicle.setCurrentValue(BigDecimal.ZERO);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateVehicle(vehicle))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Valid vehicle current value is required");
        }
    }

    @Nested
    @DisplayName("Driver Validation Tests")
    class DriverValidationTests {
        
        @Test
        @DisplayName("Should validate successful driver")
        void should_ValidateSuccessfulDriver() {
            // Given
            DriverDto driver = createValidDriver();
            
            // When & Then
            assertDoesNotThrow(() -> validationService.validateDriver(driver));
        }
        
        @Test
        @DisplayName("Should throw exception when driver is null")
        void should_ThrowException_When_DriverIsNull() {
            // When & Then
            assertThatThrownBy(() -> validationService.validateDriver(null))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Driver information cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when driver first name is null")
        void should_ThrowException_When_DriverFirstNameIsNull() {
            // Given
            DriverDto driver = createValidDriver();
            driver.setFirstName(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateDriver(driver))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Driver first name is required");
        }
        
        @Test
        @DisplayName("Should throw exception when driver last name is null")
        void should_ThrowException_When_DriverLastNameIsNull() {
            // Given
            DriverDto driver = createValidDriver();
            driver.setLastName(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateDriver(driver))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Driver last name is required");
        }
        
        @Test
        @DisplayName("Should throw exception when driver date of birth is null")
        void should_ThrowException_When_DriverDateOfBirthIsNull() {
            // Given
            DriverDto driver = createValidDriver();
            driver.setDateOfBirth(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateDriver(driver))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Driver date of birth is required");
        }
        
        @Test
        @DisplayName("Should throw exception when driver is too young")
        void should_ThrowException_When_DriverIsTooYoung() {
            // Given
            DriverDto driver = createValidDriver();
            driver.setDateOfBirth(LocalDate.now().minusYears(MIN_DRIVER_AGE - 1));
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateDriver(driver))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessageContaining("Driver must be at least");
        }
        
        @Test
        @DisplayName("Should throw exception when driver is too old")
        void should_ThrowException_When_DriverIsTooOld() {
            // Given
            DriverDto driver = createValidDriver();
            driver.setDateOfBirth(LocalDate.now().minusYears(MAX_DRIVER_AGE + 1));
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateDriver(driver))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessageContaining("Driver age exceeds maximum limit");
        }
        
        @Test
        @DisplayName("Should throw exception when driver license number is null")
        void should_ThrowException_When_DriverLicenseNumberIsNull() {
            // Given
            DriverDto driver = createValidDriver();
            driver.setLicenseNumber(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateDriver(driver))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Driver license number is required");
        }
        
        @Test
        @DisplayName("Should throw exception when driver license state is null")
        void should_ThrowException_When_DriverLicenseStateIsNull() {
            // Given
            DriverDto driver = createValidDriver();
            driver.setLicenseState(null);
            
            // When & Then
            assertThatThrownBy(() -> validationService.validateDriver(driver))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessage("Driver license state is required");
        }
        
        @Test
        @DisplayName("Should accept driver at minimum age")
        void should_AcceptDriverAtMinimumAge() {
            // Given
            DriverDto driver = createValidDriver();
            driver.setDateOfBirth(LocalDate.now().minusYears(MIN_DRIVER_AGE));
            
            // When & Then
            assertDoesNotThrow(() -> validationService.validateDriver(driver));
        }
        
        @Test
        @DisplayName("Should accept driver at maximum age")
        void should_AcceptDriverAtMaximumAge() {
            // Given
            DriverDto driver = createValidDriver();
            driver.setDateOfBirth(LocalDate.now().minusYears(MAX_DRIVER_AGE));
            
            // When & Then
            assertDoesNotThrow(() -> validationService.validateDriver(driver));
        }
    }

    // Helper methods to create test data
    private QuoteRequestDto createValidQuoteRequest() {
        QuoteRequestDto request = new QuoteRequestDto();
        request.setVehicle(createValidVehicle());
        request.setDrivers(List.of(createValidDriver()));
        request.setCoverageAmount(new BigDecimal("100000"));
        request.setDeductible(new BigDecimal("500"));
        return request;
    }
    
    private VehicleDto createValidVehicle() {
        VehicleDto vehicle = new VehicleDto();
        vehicle.setVin(VALID_VIN);
        vehicle.setMake("Honda");
        vehicle.setModel("Accord");
        vehicle.setYear(2020);
        vehicle.setCurrentValue(new BigDecimal("25000"));
        return vehicle;
    }
    
    private DriverDto createValidDriver() {
        DriverDto driver = new DriverDto();
        driver.setFirstName("John");
        driver.setLastName("Doe");
        driver.setDateOfBirth(LocalDate.now().minusYears(30));
        driver.setLicenseNumber("D123456789");
        driver.setLicenseState("CA");
        return driver;
    }
}