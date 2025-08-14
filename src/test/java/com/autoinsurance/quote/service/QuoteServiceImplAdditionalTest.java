package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.QuoteResponseDto;
import com.autoinsurance.quote.dto.VehicleDto;
import com.autoinsurance.quote.entity.Quote;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Quote Service Implementation Additional Coverage Tests")
class QuoteServiceImplAdditionalTest {

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

    @BeforeEach
    void setUp() {
        VehicleDto vehicle = VehicleDto.builder()
                .make("Toyota")
                .model("Camry")
                .year(2022)
                .vin("1HGBH41JXMN109186")
                .currentValue(BigDecimal.valueOf(25000))
                .build();

        DriverDto driver = DriverDto.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .licenseNumber("D123456789")
                .licenseState("CA")
                .yearsOfExperience(15)
                .build();

        validQuoteRequest = QuoteRequestDto.builder()
                .vehicle(vehicle)
                .drivers(List.of(driver))
                .coverageAmount(BigDecimal.valueOf(100000))
                .deductible(BigDecimal.valueOf(500))
                .build();
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when quote ID is null")
    void should_ThrowIllegalArgumentException_When_QuoteIdIsNull() {
        // When & Then
        assertThatThrownBy(() -> quoteService.getQuoteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quote ID cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when quote ID is empty string")
    void should_ThrowIllegalArgumentException_When_QuoteIdIsEmpty() {
        // When & Then
        assertThatThrownBy(() -> quoteService.getQuoteById(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quote ID cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when quote ID is whitespace")
    void should_ThrowIllegalArgumentException_When_QuoteIdIsWhitespace() {
        // When & Then
        assertThatThrownBy(() -> quoteService.getQuoteById("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quote ID cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when premium calculation request is null")
    void should_ThrowIllegalArgumentException_When_PremiumCalculationRequestIsNull() {
        // When & Then
        assertThatThrownBy(() -> quoteService.calculatePremium(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quote request cannot be null");
    }

    @Test
    @DisplayName("Should handle repository exception during quote generation")
    void should_PropagateException_When_RepositoryFails() {
        // Given
        when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                .thenReturn(BigDecimal.valueOf(1000));
        when(discountService.calculateTotalDiscount(any(QuoteRequestDto.class)))
                .thenReturn(BigDecimal.valueOf(100));
        when(discountService.getAppliedDiscounts(any(QuoteRequestDto.class)))
                .thenReturn(List.of("Safe Driver Discount"));

        Quote mockQuote = Quote.builder()
                .premium(BigDecimal.valueOf(900))
                .monthlyPremium(BigDecimal.valueOf(75))
                .coverageAmount(validQuoteRequest.getCoverageAmount())
                .deductible(validQuoteRequest.getDeductible())
                .validUntil(LocalDate.now().plusDays(30))
                .discountsApplied(List.of("Safe Driver Discount"))
                .build();

        when(quoteEntityBuilder.buildQuoteEntity(any(QuoteRequestDto.class), any(PremiumCalculation.class)))
                .thenReturn(mockQuote);
        when(quoteRepository.save(any(Quote.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(validQuoteRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection failed");
    }

    @Test
    @DisplayName("Should handle risk calculation service exception")
    void should_PropagateException_When_RiskCalculationServiceFails() {
        // Given
        when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                .thenThrow(new RuntimeException("Risk calculation failed"));

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(validQuoteRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Risk calculation failed");
    }

    @Test
    @DisplayName("Should handle discount service exception")
    void should_PropagateException_When_DiscountServiceFails() {
        // Given
        when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                .thenReturn(BigDecimal.valueOf(1000));
        when(discountService.calculateTotalDiscount(any(QuoteRequestDto.class)))
                .thenThrow(new RuntimeException("Discount calculation failed"));

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(validQuoteRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Discount calculation failed");
    }

    @Test
    @DisplayName("Should handle entity builder exception")
    void should_PropagateException_When_EntityBuilderFails() {
        // Given
        when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                .thenReturn(BigDecimal.valueOf(1000));
        when(discountService.calculateTotalDiscount(any(QuoteRequestDto.class)))
                .thenReturn(BigDecimal.valueOf(100));
        when(discountService.getAppliedDiscounts(any(QuoteRequestDto.class)))
                .thenReturn(List.of());
        when(quoteEntityBuilder.buildQuoteEntity(any(QuoteRequestDto.class), any(PremiumCalculation.class)))
                .thenThrow(new RuntimeException("Entity building failed"));

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(validQuoteRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Entity building failed");
    }

    @Test
    @DisplayName("Should handle repository exception during quote retrieval")
    void should_PropagateException_When_RepositoryFailsOnRetrieval() {
        // Given
        String quoteId = UUID.randomUUID().toString();
        when(quoteRepository.findById(quoteId))
                .thenThrow(new RuntimeException("Database error during retrieval"));

        // When & Then
        assertThatThrownBy(() -> quoteService.getQuoteById(quoteId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error during retrieval");
    }

    @Test
    @DisplayName("Should handle risk calculation service exception during premium calculation")
    void should_PropagateException_When_RiskCalculationFailsForPremiumOnly() {
        // Given
        when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                .thenThrow(new RuntimeException("Premium calculation service unavailable"));

        // When & Then
        assertThatThrownBy(() -> quoteService.calculatePremium(validQuoteRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Premium calculation service unavailable");
    }

    @Test
    @DisplayName("Should handle request with single driver correctly")
    void should_GenerateQuote_When_SingleDriverProvided() {
        // Given
        DriverDto singleDriver = DriverDto.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .dateOfBirth(LocalDate.of(1992, 8, 10))
                .licenseNumber("A987654321")
                .licenseState("TX")
                .yearsOfExperience(8)
                .build();

        QuoteRequestDto singleDriverRequest = QuoteRequestDto.builder()
                .vehicle(validQuoteRequest.getVehicle())
                .drivers(List.of(singleDriver))
                .coverageAmount(BigDecimal.valueOf(120000))
                .deductible(BigDecimal.valueOf(750))
                .build();

        Quote mockQuote = Quote.builder()
                .premium(BigDecimal.valueOf(800))
                .monthlyPremium(BigDecimal.valueOf(67))
                .coverageAmount(singleDriverRequest.getCoverageAmount())
                .deductible(singleDriverRequest.getDeductible())
                .validUntil(LocalDate.now().plusDays(30))
                .discountsApplied(List.of())
                .build();

        when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                .thenReturn(BigDecimal.valueOf(800));
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
        QuoteResponseDto response = quoteService.generateQuote(singleDriverRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getQuoteId()).isNotNull();
        assertThat(response.getPremium()).isEqualTo(BigDecimal.valueOf(800));
        assertThat(response.getCoverageAmount()).isEqualTo(BigDecimal.valueOf(120000));
    }

    @Test
    @DisplayName("Should handle request with empty driver list in primary driver name extraction")
    void should_HandleEmptyDriverList_When_ExtractingPrimaryDriverName() {
        // Given
        QuoteRequestDto emptyDriverRequest = QuoteRequestDto.builder()
                .vehicle(validQuoteRequest.getVehicle())
                .drivers(List.of())
                .coverageAmount(validQuoteRequest.getCoverageAmount())
                .deductible(validQuoteRequest.getDeductible())
                .build();

        // Configure mock to throw validation exception (which should happen)
        doThrow(new RuntimeException("At least one driver required"))
                .when(quoteValidationService).validateQuoteRequest(emptyDriverRequest);

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(emptyDriverRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("At least one driver required");
    }

    @Test
    @DisplayName("Should handle quote with null discounts applied correctly")
    void should_BuildResponse_When_DiscountsAppliedIsNull() {
        // Given
        String quoteId = UUID.randomUUID().toString();
        Quote savedQuote = Quote.builder()
                .id(quoteId)
                .premium(BigDecimal.valueOf(1100))
                .monthlyPremium(BigDecimal.valueOf(92))
                .coverageAmount(BigDecimal.valueOf(140000))
                .deductible(BigDecimal.valueOf(800))
                .validUntil(LocalDate.now().plusDays(30))
                .discountsApplied(null) // Null discounts
                .build();

        when(quoteRepository.findById(quoteId)).thenReturn(Optional.of(savedQuote));

        // When
        QuoteResponseDto response = quoteService.getQuoteById(quoteId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getQuoteId()).isEqualTo(quoteId);
        assertThat(response.getPremium()).isEqualTo(savedQuote.getPremium());
        assertThat(response.getDiscountsApplied()).isEmpty();
    }

    @Test
    @DisplayName("Should handle validation service exception during premium calculation")
    void should_PropagateException_When_ValidationFailsForPremiumCalculation() {
        // Given
        doThrow(new RuntimeException("Validation service temporarily unavailable"))
                .when(quoteValidationService).validateQuoteRequest(validQuoteRequest);

        // When & Then
        assertThatThrownBy(() -> quoteService.calculatePremium(validQuoteRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Validation service temporarily unavailable");
    }

    @Test
    @DisplayName("Should handle multiple drivers with different profiles")
    void should_GenerateQuote_When_MultipleDriversWithDifferentProfiles() {
        // Given
        DriverDto primaryDriver = DriverDto.builder()
                .firstName("Bob")
                .lastName("Wilson")
                .dateOfBirth(LocalDate.of(1980, 12, 25))
                .licenseNumber("B123456789")
                .licenseState("NY")
                .yearsOfExperience(20)
                .build();

        DriverDto secondaryDriver = DriverDto.builder()
                .firstName("Carol")
                .lastName("Wilson")
                .dateOfBirth(LocalDate.of(1985, 3, 15))
                .licenseNumber("C987654321")
                .licenseState("NY")
                .yearsOfExperience(12)
                .build();

        QuoteRequestDto multiDriverRequest = QuoteRequestDto.builder()
                .vehicle(validQuoteRequest.getVehicle())
                .drivers(List.of(primaryDriver, secondaryDriver))
                .coverageAmount(BigDecimal.valueOf(200000))
                .deductible(BigDecimal.valueOf(1000))
                .build();

        Quote mockQuote = Quote.builder()
                .premium(BigDecimal.valueOf(1400))
                .monthlyPremium(BigDecimal.valueOf(117))
                .coverageAmount(multiDriverRequest.getCoverageAmount())
                .deductible(multiDriverRequest.getDeductible())
                .validUntil(LocalDate.now().plusDays(30))
                .discountsApplied(List.of("Multi-Driver Discount"))
                .build();

        when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                .thenReturn(BigDecimal.valueOf(1500));
        when(discountService.calculateTotalDiscount(any(QuoteRequestDto.class)))
                .thenReturn(BigDecimal.valueOf(100));
        when(discountService.getAppliedDiscounts(any(QuoteRequestDto.class)))
                .thenReturn(List.of("Multi-Driver Discount"));
        when(quoteEntityBuilder.buildQuoteEntity(any(QuoteRequestDto.class), any(PremiumCalculation.class)))
                .thenReturn(mockQuote);
        when(quoteRepository.save(any(Quote.class)))
                .thenAnswer(invocation -> {
                    Quote quote = invocation.getArgument(0);
                    quote.setId(UUID.randomUUID().toString());
                    return quote;
                });

        // When
        QuoteResponseDto response = quoteService.generateQuote(multiDriverRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getQuoteId()).isNotNull();
        assertThat(response.getPremium()).isEqualTo(BigDecimal.valueOf(1400));
        assertThat(response.getDiscountsApplied()).contains("Multi-Driver Discount");
        assertThat(response.getCoverageAmount()).isEqualTo(BigDecimal.valueOf(200000));
    }
}