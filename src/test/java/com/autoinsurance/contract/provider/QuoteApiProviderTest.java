package com.autoinsurance.contract.provider;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.autoinsurance.AutoInsuranceApiApplication;
import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.QuoteResponseDto;
import com.autoinsurance.quote.dto.VehicleDto;
import com.autoinsurance.quote.entity.Quote;
import com.autoinsurance.quote.repository.QuoteRepository;
import com.autoinsurance.quote.service.QuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Provider contract test for Auto Insurance Quote API
 * 
 * This test verifies that our API (provider) can fulfill the contracts
 * defined by consumer applications using Pact.io.
 * 
 * Following TDD Red-Green-Blue cycle:
 * RED: Provider tests fail until API implementation matches consumer expectations
 * GREEN: Implement API endpoints to satisfy contract requirements
 * BLUE: Refactor API code while maintaining contract compliance
 */
@ExtendWith(PactVerificationInvocationContextProvider.class)
@SpringBootTest(
    classes = AutoInsuranceApiApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Provider("auto-insurance-api")
@PactFolder("target/pacts")
@org.junit.jupiter.api.Disabled("No consumer contracts exist yet for our API as provider. Current contracts are for our API as consumer of external services.")
public class QuoteApiProviderTest {

    @LocalServerPort
    private int port;

    @MockBean
    private QuoteService quoteService;

    @MockBean
    private QuoteRepository quoteRepository;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    /**
     * State setup for quote generation scenarios
     * RED: Define provider states that consumer tests expect
     */
    @State("quote service is available")
    void quoteServiceIsAvailable() {
        // Mock successful quote generation for standard scenarios
        QuoteResponseDto standardQuoteResponse = new QuoteResponseDto(
            "q-12345678-abcd-4567-89ef-123456789012",
            new BigDecimal("1200.00"),
            new BigDecimal("100.00"),
            new BigDecimal("250000.00"),
            new BigDecimal("1000.00"),
            LocalDate.now().plusDays(30),
            Arrays.asList("Safe Driver Discount - 15%", "Multi-Policy Discount - 10%")
        );

        when(quoteService.generateQuote(any(QuoteRequestDto.class)))
            .thenReturn(standardQuoteResponse);

        when(quoteService.calculatePremium(any(QuoteRequestDto.class)))
            .thenReturn(new BigDecimal("1350.50"));
    }

    @State("quote with ID q-12345678-abcd-4567-89ef-123456789012 exists")
    void quoteWithIdExists() {
        // RED: Setup state for existing quote retrieval
        QuoteResponseDto existingQuote = new QuoteResponseDto(
            "q-12345678-abcd-4567-89ef-123456789012",
            new BigDecimal("1200.00"),
            new BigDecimal("100.00"),
            new BigDecimal("250000.00"),
            new BigDecimal("1000.00"),
            LocalDate.now().plusDays(30),
            Arrays.asList("Safe Driver Discount - 15%", "Multi-Policy Discount - 10%")
        );

        when(quoteService.getQuoteById("q-12345678-abcd-4567-89ef-123456789012"))
            .thenReturn(existingQuote);

        // Mock repository to return a quote entity
        Quote mockQuote = new Quote();
        mockQuote.setId("q-12345678-abcd-4567-89ef-123456789012");
        mockQuote.setPremium(new BigDecimal("1200.00"));
        
        when(quoteRepository.findById("q-12345678-abcd-4567-89ef-123456789012"))
            .thenReturn(Optional.of(mockQuote));
    }

    @State("quote with ID q-nonexistent-quote-id does not exist")
    void quoteWithIdDoesNotExist() {
        // RED: Setup state for non-existent quote scenario
        when(quoteService.getQuoteById("q-nonexistent-quote-id"))
            .thenReturn(null);
            
        when(quoteRepository.findById("q-nonexistent-quote-id"))
            .thenReturn(Optional.empty());
    }

    @State("young driver with limited experience requests quote")
    void youngDriverWithLimitedExperience() {
        // RED: Setup state for high-risk driver scenario
        QuoteResponseDto highRiskQuote = new QuoteResponseDto(
            "q-young-driver-quote-id",
            new BigDecimal("1800.00"),
            new BigDecimal("150.00"),
            new BigDecimal("250000.00"),
            new BigDecimal("1000.00"),
            LocalDate.now().plusDays(30),
            Arrays.asList("New Driver - Higher Premium")
        );

        // Mock for young driver scenario
        when(quoteService.generateQuote(any(QuoteRequestDto.class)))
            .thenAnswer(invocation -> {
                QuoteRequestDto request = invocation.getArgument(0);
                DriverDto driver = request.getDrivers().get(0);
                
                // Check if this is a young driver scenario
                LocalDate birthDate = driver.getDateOfBirth();
                int age = LocalDate.now().getYear() - birthDate.getYear();
                
                if (age <= 25 && driver.getYearsOfExperience() <= 5) {
                    return highRiskQuote;
                }
                
                return new QuoteResponseDto(
                    "q-standard-quote-id",
                    new BigDecimal("1200.00"),
                    new BigDecimal("100.00"),
                    new BigDecimal("250000.00"),
                    new BigDecimal("1000.00"),
                    LocalDate.now().plusDays(30),
                    Arrays.asList("Standard Premium")
                );
            });
    }

    @State("experienced driver with good record requests quote")
    void experiencedDriverWithGoodRecord() {
        // RED: Setup state for low-risk driver scenario
        QuoteResponseDto lowRiskQuote = new QuoteResponseDto(
            "q-experienced-driver-quote-id",
            new BigDecimal("950.00"),
            new BigDecimal("79.17"),
            new BigDecimal("250000.00"),
            new BigDecimal("1000.00"),
            LocalDate.now().plusDays(30),
            Arrays.asList("Safe Driver Discount - 15%", "Experienced Driver Discount - 10%")
        );

        // Mock for experienced driver scenario
        when(quoteService.generateQuote(any(QuoteRequestDto.class)))
            .thenAnswer(invocation -> {
                QuoteRequestDto request = invocation.getArgument(0);
                DriverDto driver = request.getDrivers().get(0);
                
                // Check if this is an experienced driver scenario
                LocalDate birthDate = driver.getDateOfBirth();
                int age = LocalDate.now().getYear() - birthDate.getYear();
                
                if (age >= 30 && driver.getYearsOfExperience() >= 10) {
                    return lowRiskQuote;
                }
                
                return new QuoteResponseDto(
                    "q-standard-quote-id",
                    new BigDecimal("1200.00"),
                    new BigDecimal("100.00"),
                    new BigDecimal("250000.00"),
                    new BigDecimal("1000.00"),
                    LocalDate.now().plusDays(30),
                    Arrays.asList("Standard Premium")
                );
            });
    }

    @State("invalid vehicle information is provided")
    void invalidVehicleInformation() {
        // RED: Setup state for validation error scenario
        when(quoteService.generateQuote(any(QuoteRequestDto.class)))
            .thenThrow(new IllegalArgumentException("Invalid vehicle information provided"));
            
        when(quoteService.calculatePremium(any(QuoteRequestDto.class)))
            .thenThrow(new IllegalArgumentException("Invalid vehicle information provided"));
    }

    @State("driver age is below minimum requirement")
    void driverAgeBelowMinimum() {
        // RED: Setup state for age validation error
        when(quoteService.generateQuote(any(QuoteRequestDto.class)))
            .thenAnswer(invocation -> {
                QuoteRequestDto request = invocation.getArgument(0);
                DriverDto driver = request.getDrivers().get(0);
                
                LocalDate birthDate = driver.getDateOfBirth();
                int age = LocalDate.now().getYear() - birthDate.getYear();
                
                if (age < 18) {
                    throw new IllegalArgumentException("Driver must be at least 18 years old. Current age: " + age + " years");
                }
                
                // Return standard response for valid ages
                return new QuoteResponseDto(
                    "q-standard-quote-id",
                    new BigDecimal("1200.00"),
                    new BigDecimal("100.00"),
                    new BigDecimal("250000.00"),
                    new BigDecimal("1000.00"),
                    LocalDate.now().plusDays(30),
                    Arrays.asList("Standard Premium")
                );
            });
    }

    @State("coverage amount is within valid range")
    void coverageAmountWithinValidRange() {
        // RED: Setup state for coverage validation
        when(quoteService.generateQuote(any(QuoteRequestDto.class)))
            .thenAnswer(invocation -> {
                QuoteRequestDto request = invocation.getArgument(0);
                BigDecimal coverageAmount = request.getCoverageAmount();
                
                // Validate coverage amount range
                if (coverageAmount.compareTo(new BigDecimal("25000")) < 0) {
                    throw new IllegalArgumentException("Coverage amount must be at least $25,000");
                }
                
                if (coverageAmount.compareTo(new BigDecimal("1000000")) > 0) {
                    throw new IllegalArgumentException("Coverage amount cannot exceed $1,000,000");
                }
                
                return new QuoteResponseDto(
                    "q-valid-coverage-quote-id",
                    new BigDecimal("1200.00"),
                    new BigDecimal("100.00"),
                    coverageAmount,
                    new BigDecimal("1000.00"),
                    LocalDate.now().plusDays(30),
                    Arrays.asList("Standard Coverage")
                );
            });
    }

    @State("premium calculation service is operational")
    void premiumCalculationServiceOperational() {
        // RED: Setup state for premium calculation
        when(quoteService.calculatePremium(any(QuoteRequestDto.class)))
            .thenAnswer(invocation -> {
                QuoteRequestDto request = invocation.getArgument(0);
                
                // Simple premium calculation logic for testing
                BigDecimal basePremium = new BigDecimal("1000.00");
                BigDecimal vehicleMultiplier = request.getVehicle().getCurrentValue()
                    .divide(new BigDecimal("30000"), 2, java.math.RoundingMode.HALF_UP);
                BigDecimal coverageMultiplier = request.getCoverageAmount()
                    .divide(new BigDecimal("250000"), 2, java.math.RoundingMode.HALF_UP);
                
                return basePremium
                    .multiply(vehicleMultiplier)
                    .multiply(coverageMultiplier);
            });
    }

    @State("system is under normal load")
    void systemUnderNormalLoad() {
        // RED: Setup state for performance testing
        // For provider tests, we just ensure services are available
        quoteServiceIsAvailable();
    }

    @State("authentication is required")
    void authenticationRequired() {
        // RED: Setup state for authentication scenarios
        // This would be handled by Spring Security in the actual implementation
        // For testing purposes, we assume authentication middleware handles this
        quoteServiceIsAvailable();
    }
}