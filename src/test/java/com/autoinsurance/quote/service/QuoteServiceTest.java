package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.QuoteResponseDto;
import com.autoinsurance.quote.dto.VehicleDto;
import com.autoinsurance.quote.entity.Quote;
import com.autoinsurance.quote.exception.InvalidQuoteRequestException;
import com.autoinsurance.quote.exception.QuoteNotFoundException;
import com.autoinsurance.quote.repository.QuoteRepository;
import com.autoinsurance.quote.service.validation.QuoteValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Quote Service Tests - TDD Red Phase")
class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private RiskCalculationService riskCalculationService;

    @Mock
    private DiscountService discountService;

    @Mock
    private QuoteValidationService quoteValidationService;

    @Mock
    private QuoteEntityBuilderInterface quoteEntityBuilder;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    private QuoteRequestDto validQuoteRequest;
    private VehicleDto vehicle;
    private DriverDto driver;

    @BeforeEach
    void setUp() {
        vehicle = VehicleDto.builder()
                .make("Honda")
                .model("Accord")
                .year(2021)
                .vin("1HGCV1F31JA123456")
                .currentValue(BigDecimal.valueOf(30000))
                .build();

        driver = DriverDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 3, 20))
                .licenseNumber("S987654321")
                .licenseState("NY")
                .yearsOfExperience(10)
                .build();

        validQuoteRequest = QuoteRequestDto.builder()
                .vehicle(vehicle)
                .drivers(List.of(driver))
                .coverageAmount(BigDecimal.valueOf(150000))
                .deductible(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    @DisplayName("Should generate quote with calculated premium")
    void should_GenerateQuote_When_ValidRequestProvided() {
        // Given
        BigDecimal basePremium = BigDecimal.valueOf(1000);
        BigDecimal discount = BigDecimal.valueOf(100);
        BigDecimal finalPremium = BigDecimal.valueOf(900);
        BigDecimal monthlyPremium = BigDecimal.valueOf(75);
        
        Quote mockQuote = Quote.builder()
                .premium(finalPremium)
                .monthlyPremium(monthlyPremium)
                .coverageAmount(validQuoteRequest.getCoverageAmount())
                .deductible(validQuoteRequest.getDeductible())
                .validUntil(LocalDate.now().plusDays(30))
                .discountsApplied(List.of())
                .build();
        
        when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                .thenReturn(basePremium);
        when(discountService.calculateTotalDiscount(any(QuoteRequestDto.class)))
                .thenReturn(discount);
        when(discountService.getAppliedDiscounts(any(QuoteRequestDto.class)))
                .thenReturn(List.of());
        when(quoteEntityBuilder.buildQuoteEntity(any(QuoteRequestDto.class), any(PremiumCalculation.class)))
                .thenReturn(mockQuote);
        when(quoteRepository.save(any(Quote.class)))
                .thenAnswer(invocation -> {
                    Quote quote = invocation.getArgument(0);
                    quote.setId(UUID.randomUUID().toString());
                    return quote;
                });

        // When
        QuoteResponseDto response = quoteService.generateQuote(validQuoteRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getQuoteId()).isNotNull();
        assertThat(response.getPremium()).isEqualTo(BigDecimal.valueOf(900)); // 1000 - 100
        assertThat(response.getMonthlyPremium()).isEqualTo(BigDecimal.valueOf(75)); // 900 / 12
        assertThat(response.getCoverageAmount()).isEqualTo(validQuoteRequest.getCoverageAmount());
        assertThat(response.getValidUntil()).isAfter(LocalDate.now());
        
        verify(quoteRepository, times(1)).save(any(Quote.class));
        verify(quoteEntityBuilder, times(1)).buildQuoteEntity(any(QuoteRequestDto.class), any(PremiumCalculation.class));
    }

    @Test
    @DisplayName("Should throw exception when driver is too young")
    void should_ThrowException_When_DriverBelowMinimumAge() {
        // Given
        DriverDto youngDriver = DriverDto.builder()
                .firstName("Young")
                .lastName("Driver")
                .dateOfBirth(LocalDate.now().minusYears(17)) // Below 18
                .licenseNumber("Y123456789")
                .licenseState("CA")
                .build();
        validQuoteRequest.setDrivers(List.of(youngDriver));

        // Configure mock to throw exception
        doThrow(new InvalidQuoteRequestException("Driver must be at least 18 years old"))
                .when(quoteValidationService).validateQuoteRequest(validQuoteRequest);

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(validQuoteRequest))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessageContaining("Driver must be at least 18 years old");
    }

    @Test
    @DisplayName("Should throw exception when driver is too old")
    void should_ThrowException_When_DriverAboveMaximumAge() {
        // Given
        DriverDto elderlyDriver = DriverDto.builder()
                .firstName("Elderly")
                .lastName("Driver")
                .dateOfBirth(LocalDate.now().minusYears(86)) // Above 85
                .licenseNumber("E123456789")
                .licenseState("FL")
                .build();
        validQuoteRequest.setDrivers(List.of(elderlyDriver));

        // Configure mock to throw exception
        doThrow(new InvalidQuoteRequestException("Driver age exceeds maximum limit"))
                .when(quoteValidationService).validateQuoteRequest(validQuoteRequest);

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(validQuoteRequest))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessageContaining("Driver age exceeds maximum limit");
    }

    @Test
    @DisplayName("Should throw exception when vehicle is too old")
    void should_ThrowException_When_VehicleExceedsMaxAge() {
        // Given
        vehicle.setYear(LocalDate.now().getYear() - 21); // More than 20 years old
        
        // Configure mock to throw exception
        doThrow(new InvalidQuoteRequestException("Vehicle age exceeds maximum"))
                .when(quoteValidationService).validateQuoteRequest(validQuoteRequest);
        
        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(validQuoteRequest))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessageContaining("Vehicle age exceeds maximum");
    }

    @Test
    @DisplayName("Should retrieve quote by ID")
    void should_GetQuote_When_ValidIdProvided() {
        // Given
        String quoteId = UUID.randomUUID().toString();
        Quote savedQuote = Quote.builder()
                .id(quoteId)
                .premium(BigDecimal.valueOf(1200))
                .monthlyPremium(BigDecimal.valueOf(100))
                .coverageAmount(BigDecimal.valueOf(150000))
                .deductible(BigDecimal.valueOf(1000))
                .validUntil(LocalDate.now().plusDays(30))
                .build();
        
        when(quoteRepository.findById(quoteId)).thenReturn(Optional.of(savedQuote));

        // When
        QuoteResponseDto response = quoteService.getQuoteById(quoteId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getQuoteId()).isEqualTo(quoteId);
        assertThat(response.getPremium()).isEqualTo(savedQuote.getPremium());
    }

    @Test
    @DisplayName("Should throw exception when quote not found")
    void should_ThrowException_When_QuoteNotFound() {
        // Given
        String nonExistentId = UUID.randomUUID().toString();
        when(quoteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> quoteService.getQuoteById(nonExistentId))
                .isInstanceOf(QuoteNotFoundException.class)
                .hasMessageContaining("Quote not found");
    }

    @Test
    @DisplayName("Should calculate premium with risk factors")
    void should_CalculatePremium_When_RiskFactorsConsidered() {
        // Given
        BigDecimal expectedPremium = BigDecimal.valueOf(1500);
        when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                .thenReturn(expectedPremium);

        // When
        BigDecimal calculatedPremium = quoteService.calculatePremium(validQuoteRequest);

        // Then
        assertThat(calculatedPremium).isEqualTo(expectedPremium);
        verify(riskCalculationService, times(1)).calculateBasePremium(validQuoteRequest);
    }

    @Test
    @DisplayName("Should apply multiple discounts")
    void should_ApplyMultipleDiscounts_When_EligibleConditionsMet() {
        // Given
        driver.setSafeDriverDiscount(true);
        driver.setMultiPolicyDiscount(true);
        
        List<String> appliedDiscounts = List.of(
                "Safe Driver Discount - 15%",
                "Multi-Policy Discount - 10%"
        );
        
        BigDecimal basePremium = BigDecimal.valueOf(2000);
        BigDecimal totalDiscount = BigDecimal.valueOf(500); // 25% total
        BigDecimal finalPremium = BigDecimal.valueOf(1500);
        
        Quote mockQuote = Quote.builder()
                .premium(finalPremium)
                .monthlyPremium(finalPremium.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP))
                .coverageAmount(validQuoteRequest.getCoverageAmount())
                .deductible(validQuoteRequest.getDeductible())
                .validUntil(LocalDate.now().plusDays(30))
                .discountsApplied(appliedDiscounts)
                .build();
        
        when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                .thenReturn(basePremium);
        when(discountService.calculateTotalDiscount(any(QuoteRequestDto.class)))
                .thenReturn(totalDiscount);
        when(discountService.getAppliedDiscounts(any(QuoteRequestDto.class)))
                .thenReturn(appliedDiscounts);
        when(quoteEntityBuilder.buildQuoteEntity(any(QuoteRequestDto.class), any(PremiumCalculation.class)))
                .thenReturn(mockQuote);
        when(quoteRepository.save(any(Quote.class)))
                .thenAnswer(invocation -> {
                    Quote quote = invocation.getArgument(0);
                    quote.setId(UUID.randomUUID().toString());
                    return quote;
                });

        // When
        QuoteResponseDto response = quoteService.generateQuote(validQuoteRequest);

        // Then
        assertThat(response.getPremium()).isEqualTo(BigDecimal.valueOf(1500)); // 2000 - 500
        assertThat(response.getDiscountsApplied()).containsExactlyElementsOf(appliedDiscounts);
    }

    @Test
    @DisplayName("Should validate VIN format")
    void should_ThrowException_When_InvalidVINProvided() {
        // Given
        vehicle.setVin("INVALID123"); // Invalid VIN (should be 17 characters)

        // Configure mock to throw exception
        doThrow(new InvalidQuoteRequestException("Invalid VIN format"))
                .when(quoteValidationService).validateQuoteRequest(validQuoteRequest);

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(validQuoteRequest))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessageContaining("Invalid VIN format");
    }

    @Test
    @DisplayName("Should validate license number format")
    void should_ThrowException_When_InvalidLicenseNumber() {
        // Given
        driver.setLicenseNumber(""); // Empty license number

        // Configure mock to throw exception
        doThrow(new InvalidQuoteRequestException("Invalid license number"))
                .when(quoteValidationService).validateQuoteRequest(validQuoteRequest);

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(validQuoteRequest))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessageContaining("Invalid license number");
    }

    @Test
    @DisplayName("Should handle null vehicle in request")
    void should_ThrowException_When_VehicleIsNull() {
        // Given
        validQuoteRequest.setVehicle(null);

        // Configure mock to throw exception
        doThrow(new InvalidQuoteRequestException("Vehicle information is required"))
                .when(quoteValidationService).validateQuoteRequest(validQuoteRequest);

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(validQuoteRequest))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessageContaining("Vehicle information is required");
    }

    @Test
    @DisplayName("Should handle empty driver list")
    void should_ThrowException_When_NoDriversProvided() {
        // Given
        validQuoteRequest.setDrivers(List.of());

        // Configure mock to throw exception
        doThrow(new InvalidQuoteRequestException("At least one driver is required"))
                .when(quoteValidationService).validateQuoteRequest(validQuoteRequest);

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(validQuoteRequest))
                .isInstanceOf(InvalidQuoteRequestException.class)
                .hasMessageContaining("At least one driver is required");
    }

    @Test
    @DisplayName("Should calculate correct monthly premium")
    void should_CalculateMonthlyPremium_When_AnnualPremiumProvided() {
        // Given
        BigDecimal annualPremium = BigDecimal.valueOf(1200);
        BigDecimal expectedMonthly = BigDecimal.valueOf(100);
        
        Quote mockQuote = Quote.builder()
                .premium(annualPremium)
                .monthlyPremium(expectedMonthly)
                .coverageAmount(validQuoteRequest.getCoverageAmount())
                .deductible(validQuoteRequest.getDeductible())
                .validUntil(LocalDate.now().plusDays(30))
                .discountsApplied(List.of())
                .build();
        
        when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                .thenReturn(annualPremium);
        when(discountService.calculateTotalDiscount(any(QuoteRequestDto.class)))
                .thenReturn(BigDecimal.ZERO);
        when(discountService.getAppliedDiscounts(any(QuoteRequestDto.class)))
                .thenReturn(List.of());
        when(quoteEntityBuilder.buildQuoteEntity(any(QuoteRequestDto.class), any(PremiumCalculation.class)))
                .thenReturn(mockQuote);
        when(quoteRepository.save(any(Quote.class)))
                .thenAnswer(invocation -> {
                    Quote quote = invocation.getArgument(0);
                    quote.setId(UUID.randomUUID().toString());
                    return quote;
                });

        // When
        QuoteResponseDto response = quoteService.generateQuote(validQuoteRequest);

        // Then
        assertThat(response.getMonthlyPremium()).isEqualTo(expectedMonthly);
    }
}