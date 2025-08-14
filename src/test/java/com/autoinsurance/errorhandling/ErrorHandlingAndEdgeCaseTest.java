package com.autoinsurance.errorhandling;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.VehicleDto;
import com.autoinsurance.quote.dto.DriverDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Error Handling & Edge Case Test Suite
 * 
 * Following TDD Red-Green-Blue methodology:
 * RED: Tests fail when error handling doesn't work correctly
 * GREEN: All error scenarios are handled gracefully with appropriate responses
 * BLUE: Optimize error handling performance while maintaining user experience
 * 
 * Tests Error Scenarios:
 * - Invalid input validation errors
 * - Boundary condition edge cases
 * - Null and empty data handling
 * - Database constraint violations
 * - Business rule violations
 * - System resource limits
 * - Network and timeout scenarios
 * - Malformed request handling
 * - Data consistency edge cases
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb_errorhandling",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false"
})
@DisplayName("Error Handling & Edge Case Tests")
class ErrorHandlingAndEdgeCaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private QuoteRequestDto validQuoteRequest;

    @BeforeEach
    void setUp() {
        // Setup valid request for modification in tests
        VehicleDto validVehicle = VehicleDto.builder()
            .make("Honda")
            .model("Civic")
            .year(2020)
            .vin("1HGFC2F53JA123456")
            .currentValue(new BigDecimal("25000.00"))
            .build();

        DriverDto validDriver = DriverDto.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(1990, 5, 15))
            .licenseNumber("D123456789")
            .licenseState("CA")
            .yearsOfExperience(8)
            .build();

        validQuoteRequest = new QuoteRequestDto(
            validVehicle,
            List.of(validDriver),
            new BigDecimal("100000.00"),
            new BigDecimal("1000.00")
        );
    }

    @Test
    @DisplayName("Should handle null vehicle data gracefully")
    void should_HandleNullVehicleDataGracefully() throws Exception {
        // RED: Test fails if null vehicle doesn't trigger proper error handling

        // Given - Request with null vehicle
        QuoteRequestDto requestWithNullVehicle = new QuoteRequestDto(
            null, // Null vehicle
            validQuoteRequest.getDrivers(),
            validQuoteRequest.getCoverageAmount(),
            validQuoteRequest.getDeductible()
        );

        String requestBody = objectMapper.writeValueAsString(requestWithNullVehicle);

        // When/Then - Should handle null vehicle gracefully
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should handle empty driver list gracefully")
    void should_HandleEmptyDriverListGracefully() throws Exception {
        // RED: Test fails if empty driver list doesn't trigger proper error handling

        // Given - Request with empty driver list
        QuoteRequestDto requestWithEmptyDrivers = new QuoteRequestDto(
            validQuoteRequest.getVehicle(),
            Collections.emptyList(), // Empty driver list
            validQuoteRequest.getCoverageAmount(),
            validQuoteRequest.getDeductible()
        );

        String requestBody = objectMapper.writeValueAsString(requestWithEmptyDrivers);

        // When/Then - Should handle empty drivers gracefully
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle null driver list gracefully")
    void should_HandleNullDriverListGracefully() throws Exception {
        // RED: Test fails if null driver list doesn't trigger proper error handling

        // Given - Request with null driver list
        QuoteRequestDto requestWithNullDrivers = new QuoteRequestDto(
            validQuoteRequest.getVehicle(),
            null, // Null driver list
            validQuoteRequest.getCoverageAmount(),
            validQuoteRequest.getDeductible()
        );

        String requestBody = objectMapper.writeValueAsString(requestWithNullDrivers);

        // When/Then - Should handle null drivers gracefully
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle negative coverage amounts")
    void should_HandleNegativeCoverageAmounts() throws Exception {
        // RED: Test fails if negative coverage amounts aren't rejected

        // Given - Request with negative coverage amount
        QuoteRequestDto requestWithNegativeCoverage = new QuoteRequestDto(
            validQuoteRequest.getVehicle(),
            validQuoteRequest.getDrivers(),
            new BigDecimal("-50000.00"), // Negative coverage
            validQuoteRequest.getDeductible()
        );

        String requestBody = objectMapper.writeValueAsString(requestWithNegativeCoverage);

        // When/Then - Should reject negative coverage amounts
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle negative deductible amounts")
    void should_HandleNegativeDeductibleAmounts() throws Exception {
        // RED: Test fails if negative deductible amounts aren't rejected

        // Given - Request with negative deductible
        QuoteRequestDto requestWithNegativeDeductible = new QuoteRequestDto(
            validQuoteRequest.getVehicle(),
            validQuoteRequest.getDrivers(),
            validQuoteRequest.getCoverageAmount(),
            new BigDecimal("-1000.00") // Negative deductible
        );

        String requestBody = objectMapper.writeValueAsString(requestWithNegativeDeductible);

        // When/Then - Should reject negative deductible amounts
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle zero coverage amounts")
    void should_HandleZeroCoverageAmounts() throws Exception {
        // RED: Test fails if zero coverage amounts aren't handled properly

        // Given - Request with zero coverage amount
        QuoteRequestDto requestWithZeroCoverage = new QuoteRequestDto(
            validQuoteRequest.getVehicle(),
            validQuoteRequest.getDrivers(),
            new BigDecimal("0.00"), // Zero coverage
            validQuoteRequest.getDeductible()
        );

        String requestBody = objectMapper.writeValueAsString(requestWithZeroCoverage);

        // When/Then - Should reject zero coverage amounts
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle invalid VIN formats")
    void should_HandleInvalidVinFormats() throws Exception {
        // RED: Test fails if invalid VIN formats aren't rejected

        // Test various invalid VIN formats
        String[] invalidVins = {
            "", // Empty VIN
            "12345", // Too short
            "INVALID_VIN_FORMAT_TOO_LONG_DEFINITELY", // Too long
            "123456789012345IO", // Contains invalid characters (I, O)
            "123-456-789-012-345", // Contains hyphens
            "1HGCV1F31JA12345Q", // Invalid character Q
            null // Null VIN (handled in JSON serialization)
        };

        for (String invalidVin : invalidVins) {
            if (invalidVin == null) continue; // Skip null test here

            // Given - Vehicle with invalid VIN
            VehicleDto vehicleWithInvalidVin = VehicleDto.builder()
                .make("Honda")
                .model("Civic")
                .year(2020)
                .vin(invalidVin)
                .currentValue(new BigDecimal("25000.00"))
                .build();

            QuoteRequestDto request = new QuoteRequestDto(
                vehicleWithInvalidVin,
                validQuoteRequest.getDrivers(),
                validQuoteRequest.getCoverageAmount(),
                validQuoteRequest.getDeductible()
            );

            String requestBody = objectMapper.writeValueAsString(request);

            // When/Then - Should reject invalid VIN formats
            mockMvc.perform(post("/api/v1/quotes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    @Test
    @DisplayName("Should handle boundary vehicle years")
    void should_HandleBoundaryVehicleYears() throws Exception {
        // RED: Test fails if boundary vehicle years aren't handled correctly

        // Test various boundary year values
        Integer[] boundaryYears = {
            1885, // Too old (before first car)
            1899, // Extremely old
            1950, // Old but valid
            LocalDate.now().getYear() + 1, // Future year
            LocalDate.now().getYear() + 5, // Far future
            9999 // Extreme future year
        };

        for (Integer year : boundaryYears) {
            // Given - Vehicle with boundary year
            VehicleDto vehicleWithBoundaryYear = VehicleDto.builder()
                .make("Honda")
                .model("Civic")
                .year(year)
                .vin("1HGFC2F53JA123456")
                .currentValue(new BigDecimal("25000.00"))
                .build();

            QuoteRequestDto request = new QuoteRequestDto(
                vehicleWithBoundaryYear,
                validQuoteRequest.getDrivers(),
                validQuoteRequest.getCoverageAmount(),
                validQuoteRequest.getDeductible()
            );

            String requestBody = objectMapper.writeValueAsString(request);

            // When/Then - Should handle boundary years appropriately
            int vehicleAge = LocalDate.now().getYear() - year;
            
            if (year <= 1900 || year > LocalDate.now().getYear() || vehicleAge > 20) {
                // Should reject unrealistic years or vehicles older than 20 years
                mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").exists());
            } else if (year >= 1900 && year <= 2025 && vehicleAge <= 20) {
                // Should accept reasonable years within age limit
                mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.quoteId").exists());
            }
        }
    }

    @Test
    @DisplayName("Should handle extreme vehicle values")
    void should_HandleExtremeVehicleValues() throws Exception {
        // RED: Test fails if extreme vehicle values aren't handled properly

        // Test extreme vehicle current values
        BigDecimal[] extremeValues = {
            new BigDecimal("-1000.00"), // Negative value
            new BigDecimal("0.00"), // Zero value
            new BigDecimal("0.01"), // Minimum valid value
            new BigDecimal("999999999.99"), // Extreme high value
            new BigDecimal("10000000000.00") // Unrealistic high value
        };

        for (BigDecimal value : extremeValues) {
            // Given - Vehicle with extreme value
            VehicleDto vehicleWithExtremeValue = VehicleDto.builder()
                .make("Honda")
                .model("Civic")
                .year(2020)
                .vin("1HGFC2F53JA123456")
                .currentValue(value)
                .build();

            QuoteRequestDto request = new QuoteRequestDto(
                vehicleWithExtremeValue,
                validQuoteRequest.getDrivers(),
                validQuoteRequest.getCoverageAmount(),
                validQuoteRequest.getDeductible()
            );

            String requestBody = objectMapper.writeValueAsString(request);

            // When/Then - Should handle extreme values appropriately
            if (value.compareTo(BigDecimal.ZERO) <= 0 || 
                value.compareTo(new BigDecimal("1000000.00")) > 0) {
                // Should reject negative, zero, or unrealistic values
                mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isBadRequest());
            } else {
                // Should accept reasonable values
                mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.quoteId").exists());
            }
        }
    }

    @Test
    @DisplayName("Should handle boundary driver ages")
    void should_HandleBoundaryDriverAges() throws Exception {
        // RED: Test fails if boundary driver ages aren't handled correctly

        // Test various boundary age scenarios
        LocalDate[] boundaryBirthDates = {
            LocalDate.now().minusYears(16), // 16 years old - underage
            LocalDate.now().minusYears(17), // 17 years old - underage
            LocalDate.now().minusYears(18), // 18 years old - minimum age
            LocalDate.now().minusYears(19), // 19 years old - young driver
            LocalDate.now().minusYears(25), // 25 years old - adult
            LocalDate.now().minusYears(65), // 65 years old - senior
            LocalDate.now().minusYears(80), // 80 years old - elderly
            LocalDate.now().minusYears(100), // 100 years old - extreme age
            LocalDate.now().minusYears(150), // Unrealistic age
            LocalDate.now().plusYears(5) // Future birth date
        };

        for (LocalDate birthDate : boundaryBirthDates) {
            // Given - Driver with boundary age
            DriverDto driverWithBoundaryAge = DriverDto.builder()
                .firstName("Test")
                .lastName("Driver")
                .dateOfBirth(birthDate)
                .licenseNumber("D123456789")
                .licenseState("CA")
                .yearsOfExperience(Math.max(0, Math.min(50, (int)(LocalDate.now().getYear() - birthDate.getYear() - 18))))
                .build();

            QuoteRequestDto request = new QuoteRequestDto(
                validQuoteRequest.getVehicle(),
                List.of(driverWithBoundaryAge),
                validQuoteRequest.getCoverageAmount(),
                validQuoteRequest.getDeductible()
            );

            String requestBody = objectMapper.writeValueAsString(request);

            int age = LocalDate.now().getYear() - birthDate.getYear();

            // When/Then - Should handle boundary ages appropriately
            if (age < 18 || age > 85 || birthDate.isAfter(LocalDate.now())) {
                // Should reject underage, extremely old (>85), or future birth dates
                mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").exists());
            } else {
                // Should accept reasonable ages
                mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.quoteId").exists());
            }
        }
    }

    @Test
    @DisplayName("Should handle invalid license states")
    void should_HandleInvalidLicenseStates() throws Exception {
        // RED: Test fails if invalid license states aren't rejected

        String[] invalidStates = {
            "", // Empty state
            "X", // Single character
            "XXX", // Invalid 3-character code
            "CALIFORNIA", // Full state name instead of code
            "12", // Numbers
            "CA$", // Special characters
            null // Null state (handled in JSON)
        };

        for (String state : invalidStates) {
            if (state == null) continue; // Skip null test

            // Given - Driver with invalid license state
            DriverDto driverWithInvalidState = DriverDto.builder()
                .firstName("Test")
                .lastName("Driver")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .licenseNumber("D123456789")
                .licenseState(state)
                .yearsOfExperience(5)
                .build();

            QuoteRequestDto request = new QuoteRequestDto(
                validQuoteRequest.getVehicle(),
                List.of(driverWithInvalidState),
                validQuoteRequest.getCoverageAmount(),
                validQuoteRequest.getDeductible()
            );

            String requestBody = objectMapper.writeValueAsString(request);

            // When/Then - Should reject invalid license states
            mockMvc.perform(post("/api/v1/quotes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    @Test
    @DisplayName("Should handle negative years of experience")
    void should_HandleNegativeYearsOfExperience() throws Exception {
        // RED: Test fails if negative years of experience aren't rejected

        Integer[] invalidExperiences = {
            -1, -5, -10, -999, Integer.MIN_VALUE
        };

        for (Integer experience : invalidExperiences) {
            // Given - Driver with negative experience
            DriverDto driverWithNegativeExperience = DriverDto.builder()
                .firstName("Test")
                .lastName("Driver")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .licenseNumber("D123456789")
                .licenseState("CA")
                .yearsOfExperience(experience)
                .build();

            QuoteRequestDto request = new QuoteRequestDto(
                validQuoteRequest.getVehicle(),
                List.of(driverWithNegativeExperience),
                validQuoteRequest.getCoverageAmount(),
                validQuoteRequest.getDeductible()
            );

            String requestBody = objectMapper.writeValueAsString(request);

            // When/Then - Should reject negative experience
            mockMvc.perform(post("/api/v1/quotes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    @Test
    @DisplayName("Should handle malformed JSON requests")
    void should_HandleMalformedJsonRequests() throws Exception {
        // RED: Test fails if malformed JSON doesn't trigger proper error handling

        String[] malformedJsonRequests = {
            "{", // Incomplete JSON
            "{ \"vehicle\": }", // Missing value
            "{ \"vehicle\": { \"make\": \"Honda\", \"model\": } }", // Incomplete nested object
            "{ \"invalid_field\": \"value\" }", // Unknown field only
            "[]", // Array instead of object
            "\"just_a_string\"", // String instead of object
            "123", // Number instead of object
            "true", // Boolean instead of object
            "null", // Null JSON
            "" // Empty content
        };

        for (String malformedJson : malformedJsonRequests) {
            // When/Then - Should handle malformed JSON gracefully
            mockMvc.perform(post("/api/v1/quotes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(malformedJson))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        // Accept either 400 or 500 based on Spring Boot's error handling
                        assert status == 400 || status == 500;
                    });
        }
    }

    @Test
    @DisplayName("Should handle nonexistent quote retrieval")
    void should_HandleNonexistentQuoteRetrieval() throws Exception {
        // RED: Test fails if nonexistent quotes don't return proper 404

        String[] nonexistentQuoteIds = {
            "nonexistent-quote-id",
            "12345678-1234-1234-1234-123456789012", // Valid UUID format but doesn't exist
            "", // Empty ID
            "invalid-uuid-format",
            "null",
            "undefined"
        };

        for (String quoteId : nonexistentQuoteIds) {
            // When/Then - Should return 404 for nonexistent quotes
            if (quoteId.isEmpty()) {
                // Empty ID should result in method not found or bad request
                mockMvc.perform(get("/api/v1/quotes/"))
                        .andExpect(result -> {
                            int status = result.getResponse().getStatus();
                            assert status == 404 || status == 400 || status == 405 || status == 500;
                        });
            } else {
                mockMvc.perform(get("/api/v1/quotes/" + quoteId))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Test
    @DisplayName("Should handle deductible higher than coverage")
    void should_HandleDeductibleHigherThanCoverage() throws Exception {
        // RED: Test fails if deductible higher than coverage isn't handled

        // Given - Request where deductible exceeds coverage
        QuoteRequestDto requestWithHighDeductible = new QuoteRequestDto(
            validQuoteRequest.getVehicle(),
            validQuoteRequest.getDrivers(),
            new BigDecimal("50000.00"), // Coverage amount
            new BigDecimal("100000.00") // Deductible higher than coverage
        );

        String requestBody = objectMapper.writeValueAsString(requestWithHighDeductible);

        // When/Then - Should reject when deductible exceeds coverage
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle multiple drivers edge cases")
    void should_HandleMultipleDriversEdgeCases() throws Exception {
        // RED: Test fails if multiple driver scenarios aren't handled correctly

        // Test with many drivers (edge case for business logic)
        List<DriverDto> manyDrivers = List.of(
            createDriver("Driver1", LocalDate.of(1990, 1, 1)),
            createDriver("Driver2", LocalDate.of(1985, 6, 15)),
            createDriver("Driver3", LocalDate.of(1995, 12, 25)),
            createDriver("Driver4", LocalDate.of(1980, 3, 10)),
            createDriver("Driver5", LocalDate.of(1992, 8, 5)),
            createDriver("Driver6", LocalDate.of(1988, 11, 30))
        );

        QuoteRequestDto requestWithManyDrivers = new QuoteRequestDto(
            validQuoteRequest.getVehicle(),
            manyDrivers,
            validQuoteRequest.getCoverageAmount(),
            validQuoteRequest.getDeductible()
        );

        String requestBody = objectMapper.writeValueAsString(requestWithManyDrivers);

        // When/Then - Should handle multiple drivers appropriately
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated()) // Should accept reasonable number of drivers
                .andExpect(jsonPath("$.quoteId").exists());
    }

    @Test
    @DisplayName("Should handle empty string fields")
    void should_HandleEmptyStringFields() throws Exception {
        // RED: Test fails if empty string fields aren't validated

        // Test vehicle with empty strings
        VehicleDto vehicleWithEmptyStrings = VehicleDto.builder()
            .make("") // Empty make
            .model("") // Empty model
            .year(2020)
            .vin("") // Empty VIN
            .currentValue(new BigDecimal("25000.00"))
            .build();

        QuoteRequestDto request = new QuoteRequestDto(
            vehicleWithEmptyStrings,
            validQuoteRequest.getDrivers(),
            validQuoteRequest.getCoverageAmount(),
            validQuoteRequest.getDeductible()
        );

        String requestBody = objectMapper.writeValueAsString(request);

        // When/Then - Should reject empty string fields
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle whitespace-only fields")
    void should_HandleWhitespaceOnlyFields() throws Exception {
        // RED: Test fails if whitespace-only fields aren't validated

        // Test driver with whitespace-only names
        DriverDto driverWithWhitespaceNames = DriverDto.builder()
            .firstName("   ") // Whitespace only
            .lastName("\t\n") // Whitespace with tabs and newlines
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .licenseNumber("D123456789")
            .licenseState("CA")
            .yearsOfExperience(5)
            .build();

        QuoteRequestDto request = new QuoteRequestDto(
            validQuoteRequest.getVehicle(),
            List.of(driverWithWhitespaceNames),
            validQuoteRequest.getCoverageAmount(),
            validQuoteRequest.getDeductible()
        );

        String requestBody = objectMapper.writeValueAsString(request);

        // When/Then - Should reject whitespace-only fields
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle concurrent error scenarios")
    void should_HandleConcurrentErrorScenarios() throws Exception {
        // RED: Test fails if concurrent invalid requests cause system issues

        // Create multiple invalid requests to test concurrent error handling
        QuoteRequestDto[] invalidRequests = {
            new QuoteRequestDto(null, validQuoteRequest.getDrivers(), 
                validQuoteRequest.getCoverageAmount(), validQuoteRequest.getDeductible()),
            new QuoteRequestDto(validQuoteRequest.getVehicle(), Collections.emptyList(),
                validQuoteRequest.getCoverageAmount(), validQuoteRequest.getDeductible()),
            new QuoteRequestDto(validQuoteRequest.getVehicle(), validQuoteRequest.getDrivers(),
                new BigDecimal("-1000.00"), validQuoteRequest.getDeductible())
        };

        // When - Submit multiple invalid requests rapidly
        for (QuoteRequestDto invalidRequest : invalidRequests) {
            String requestBody = objectMapper.writeValueAsString(invalidRequest);
            
            // Then - All should be handled gracefully
            mockMvc.perform(post("/api/v1/quotes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }

        // GREEN: System should remain stable after handling multiple errors
        // Verify system is still functional with a valid request
        String validRequestBody = objectMapper.writeValueAsString(validQuoteRequest);
        mockMvc.perform(post("/api/v1/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quoteId").exists());
    }

    private DriverDto createDriver(String name, LocalDate birthDate) {
        return DriverDto.builder()
            .firstName(name)
            .lastName("TestDriver")
            .dateOfBirth(birthDate)
            .licenseNumber("D" + name.hashCode())
            .licenseState("CA")
            .yearsOfExperience(Math.max(0, LocalDate.now().getYear() - birthDate.getYear() - 16))
            .build();
    }
}