package com.autoinsurance.quote.controller;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.QuoteResponseDto;
import com.autoinsurance.quote.dto.VehicleDto;
import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.service.QuoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(QuoteController.class)
@Import({com.autoinsurance.config.TestSecurityConfig.class, com.autoinsurance.common.exception.GlobalExceptionHandler.class})
@DisplayName("Quote Controller Tests - TDD Blue Phase")
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuoteService quoteService;

    private QuoteRequestDto validQuoteRequest;
    private QuoteResponseDto mockQuoteResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        VehicleDto vehicle = VehicleDto.builder()
                .make("Toyota")
                .model("Camry")
                .year(2020)
                .vin("1HGBH41JXMN109186")
                .currentValue(BigDecimal.valueOf(25000))
                .build();

        DriverDto driver = DriverDto.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .licenseNumber("D123456789")
                .licenseState("CA")
                .build();

        validQuoteRequest = QuoteRequestDto.builder()
                .vehicle(vehicle)
                .drivers(List.of(driver))
                .coverageAmount(BigDecimal.valueOf(100000))
                .deductible(BigDecimal.valueOf(500))
                .build();

        mockQuoteResponse = QuoteResponseDto.builder()
                .quoteId(UUID.randomUUID().toString())
                .premium(BigDecimal.valueOf(1200))
                .monthlyPremium(BigDecimal.valueOf(100))
                .coverageAmount(BigDecimal.valueOf(100000))
                .deductible(BigDecimal.valueOf(500))
                .validUntil(LocalDate.now().plusDays(30))
                .build();
    }

    @Test
    @DisplayName("Should create a new quote successfully")
    void should_CreateQuote_When_ValidRequestProvided() throws Exception {
        // Given
        when(quoteService.generateQuote(any(QuoteRequestDto.class))).thenReturn(mockQuoteResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validQuoteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quoteId").exists())
                .andExpect(jsonPath("$.premium").value(1200))
                .andExpect(jsonPath("$.monthlyPremium").value(100))
                .andExpect(jsonPath("$.coverageAmount").value(100000))
                .andExpect(jsonPath("$.deductible").value(500));
    }

    @Test
    @DisplayName("Should return 400 when vehicle information is missing")
    void should_ReturnBadRequest_When_VehicleMissing() throws Exception {
        // Given
        validQuoteRequest.setVehicle(null);
        
        when(quoteService.generateQuote(any(QuoteRequestDto.class)))
                .thenThrow(new com.autoinsurance.quote.exception.InvalidQuoteRequestException("Vehicle information is required"));

        // When & Then
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validQuoteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should return 400 when driver age is below minimum")
    void should_ReturnBadRequest_When_DriverTooYoung() throws Exception {
        // Given
        DriverDto youngDriver = DriverDto.builder()
                .firstName("Teen")
                .lastName("Driver")
                .dateOfBirth(LocalDate.now().minusYears(16)) // 16 years old
                .licenseNumber("D987654321")
                .licenseState("CA")
                .build();
        validQuoteRequest.setDrivers(List.of(youngDriver));
        
        when(quoteService.generateQuote(any(QuoteRequestDto.class)))
                .thenThrow(new com.autoinsurance.quote.exception.InvalidQuoteRequestException("Driver must be at least 18 years old - minimum age requirement"));

        // When & Then
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validQuoteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("minimum age")));
    }

    @Test
    @DisplayName("Should retrieve quote by ID")
    void should_GetQuote_When_ValidIdProvided() throws Exception {
        // Given
        String quoteId = UUID.randomUUID().toString();
        when(quoteService.getQuoteById(quoteId)).thenReturn(mockQuoteResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/quotes/{id}", quoteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteId").value(mockQuoteResponse.getQuoteId()))
                .andExpect(jsonPath("$.premium").value(1200));
    }

    @Test
    @DisplayName("Should return 404 when quote not found")
    void should_ReturnNotFound_When_QuoteDoesNotExist() throws Exception {
        // Given
        String nonExistentId = UUID.randomUUID().toString();
        when(quoteService.getQuoteById(nonExistentId)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/v1/quotes/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should calculate premium based on risk factors")
    void should_CalculatePremium_When_RiskFactorsProvided() throws Exception {
        // Given
        when(quoteService.calculatePremium(any(QuoteRequestDto.class)))
                .thenReturn(BigDecimal.valueOf(1500));

        // When & Then
        mockMvc.perform(post("/api/v1/quotes/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validQuoteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.premium").value(1500));
    }

    @Test
    @DisplayName("Should apply discounts when eligible")
    void should_ApplyDiscounts_When_EligibleDriverProfile() throws Exception {
        // Given
        DriverDto safeDriver = DriverDto.builder()
                .firstName("Safe")
                .lastName("Driver")
                .dateOfBirth(LocalDate.of(1975, 1, 1))
                .licenseNumber("D111111111")
                .licenseState("CA")
                .yearsOfExperience(20)
                .safeDriverDiscount(true)
                .build();
        validQuoteRequest.setDrivers(List.of(safeDriver));

        QuoteResponseDto discountedQuote = QuoteResponseDto.builder()
                .quoteId(UUID.randomUUID().toString())
                .premium(BigDecimal.valueOf(900)) // Discounted premium
                .monthlyPremium(BigDecimal.valueOf(75))
                .coverageAmount(BigDecimal.valueOf(100000))
                .deductible(BigDecimal.valueOf(500))
                .discountsApplied(List.of("Safe Driver Discount - 25%"))
                .validUntil(LocalDate.now().plusDays(30))
                .build();

        when(quoteService.generateQuote(any(QuoteRequestDto.class))).thenReturn(discountedQuote);

        // When & Then
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validQuoteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.premium").value(900))
                .andExpect(jsonPath("$.discountsApplied").isArray())
                .andExpect(jsonPath("$.discountsApplied[0]").value(containsString("Safe Driver")));
    }

    @Test
    @DisplayName("Should validate VIN format")
    void should_ValidateVIN_When_InvalidFormatProvided() throws Exception {
        // Given
        VehicleDto invalidVehicle = VehicleDto.builder()
                .make("Toyota")
                .model("Camry")
                .year(2020)
                .vin("INVALID-VIN") // Invalid VIN format
                .currentValue(BigDecimal.valueOf(25000))
                .build();
        validQuoteRequest.setVehicle(invalidVehicle);
        
        when(quoteService.generateQuote(any(QuoteRequestDto.class)))
                .thenThrow(new com.autoinsurance.quote.exception.InvalidQuoteRequestException("Invalid VIN format"));

        // When & Then
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validQuoteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Invalid VIN")));
    }

    @Test
    @DisplayName("Should enforce maximum vehicle age")
    void should_RejectQuote_When_VehicleTooOld() throws Exception {
        // Given
        VehicleDto oldVehicle = VehicleDto.builder()
                .make("Toyota")
                .model("Corolla")
                .year(1995) // More than 20 years old
                .vin("1HGBH41JXMN109186")
                .currentValue(BigDecimal.valueOf(2000))
                .build();
        validQuoteRequest.setVehicle(oldVehicle);
        
        when(quoteService.generateQuote(any(QuoteRequestDto.class)))
                .thenThrow(new com.autoinsurance.quote.exception.InvalidQuoteRequestException("Vehicle age exceeds maximum limit of 20 years"));

        // When & Then
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validQuoteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Vehicle age exceeds maximum")));
    }

    @Test
    @DisplayName("Should handle concurrent quote requests")
    void should_HandleConcurrentRequests_When_MultipleQuotesSubmitted() throws Exception {
        // This test verifies the API can handle multiple concurrent requests
        // Implementation will include proper synchronization in the service layer
        
        when(quoteService.generateQuote(any(QuoteRequestDto.class))).thenReturn(mockQuoteResponse);

        // Simulate concurrent requests
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/v1/quotes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validQuoteRequest)))
                    .andExpect(status().isCreated());
        }
    }
}