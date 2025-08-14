package com.autoinsurance.testdata.scenarios;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.QuoteResponseDto;
import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.VehicleDto;
import com.autoinsurance.testdata.TestDataFactory;
import com.autoinsurance.testdata.fixtures.VehicleFixtures;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Comprehensive test scenarios for various insurance testing needs.
 * Provides complete test scenarios including expected outcomes for each case.
 */
public class TestScenarios {
    
    /**
     * Scenario with expected results for testing
     */
    public static class TestScenario {
        public final String name;
        public final String description;
        public final QuoteRequestDto request;
        public final ExpectedOutcome expectedOutcome;
        
        public TestScenario(String name, String description, QuoteRequestDto request, ExpectedOutcome expectedOutcome) {
            this.name = name;
            this.description = description;
            this.request = request;
            this.expectedOutcome = expectedOutcome;
        }
    }
    
    /**
     * Expected outcome for a test scenario
     */
    public static class ExpectedOutcome {
        public final BigDecimal minPremium;
        public final BigDecimal maxPremium;
        public final List<String> expectedDiscounts;
        public final String riskLevel;
        public final boolean shouldPass;
        public final String failureReason;
        
        public ExpectedOutcome(BigDecimal minPremium, BigDecimal maxPremium, 
                              List<String> expectedDiscounts, String riskLevel,
                              boolean shouldPass, String failureReason) {
            this.minPremium = minPremium;
            this.maxPremium = maxPremium;
            this.expectedDiscounts = expectedDiscounts;
            this.riskLevel = riskLevel;
            this.shouldPass = shouldPass;
            this.failureReason = failureReason;
        }
        
        public static ExpectedOutcome success(BigDecimal minPremium, BigDecimal maxPremium,
                                             List<String> expectedDiscounts, String riskLevel) {
            return new ExpectedOutcome(minPremium, maxPremium, expectedDiscounts, riskLevel, true, null);
        }
        
        public static ExpectedOutcome failure(String failureReason) {
            return new ExpectedOutcome(null, null, Collections.emptyList(), null, false, failureReason);
        }
    }
    
    // Premium calculation test scenarios
    public static class PremiumCalculationScenarios {
        
        /**
         * Low-risk scenario: Experienced driver, economy car, high deductible
         */
        public static TestScenario getLowRiskScenario() {
            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.HONDA_CIVIC_2020)
                    .drivers(Arrays.asList(TestDataFactory.createExperiencedDriver()))
                    .coverageAmount(new BigDecimal("100000.00"))
                    .deductible(new BigDecimal("2000.00"))
                    .build();
            
            ExpectedOutcome outcome = ExpectedOutcome.success(
                    new BigDecimal("600.00"),
                    new BigDecimal("900.00"),
                    Arrays.asList("Safe Driver Discount - 15%"),
                    "LOW"
            );
            
            return new TestScenario(
                    "Low Risk Premium",
                    "Experienced driver with economy car and high deductible",
                    request,
                    outcome
            );
        }
        
        /**
         * High-risk scenario: Young driver, sports car, low deductible
         */
        public static TestScenario getHighRiskScenario() {
            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.FORD_MUSTANG_2022)
                    .drivers(Arrays.asList(TestDataFactory.createYoungDriver()))
                    .coverageAmount(new BigDecimal("300000.00"))
                    .deductible(new BigDecimal("500.00"))
                    .build();
            
            ExpectedOutcome outcome = ExpectedOutcome.success(
                    new BigDecimal("3000.00"),
                    new BigDecimal("4500.00"),
                    Collections.emptyList(),
                    "HIGH"
            );
            
            return new TestScenario(
                    "High Risk Premium",
                    "Young driver with sports car and low deductible",
                    request,
                    outcome
            );
        }
        
        /**
         * Multi-discount scenario: Multiple applicable discounts
         */
        public static TestScenario getMultiDiscountScenario() {
            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.TESLA_MODEL_3_2023)
                    .drivers(Arrays.asList(TestDataFactory.createDiscountEligibleDriver()))
                    .coverageAmount(new BigDecimal("250000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
            
            ExpectedOutcome outcome = ExpectedOutcome.success(
                    new BigDecimal("900.00"),
                    new BigDecimal("1200.00"),
                    Arrays.asList(
                            "Safe Driver Discount - 15%",
                            "Multi-Policy Discount - 10%",
                            "Electric Vehicle Discount - 5%"
                    ),
                    "LOW"
            );
            
            return new TestScenario(
                    "Multi-Discount Premium",
                    "Multiple discounts applied to electric vehicle",
                    request,
                    outcome
            );
        }
    }
    
    // Validation test scenarios
    public static class ValidationScenarios {
        
        /**
         * Invalid vehicle year (too old)
         */
        public static TestScenario getInvalidVehicleYearScenario() {
            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.INVALID_OLD_YEAR)
                    .drivers(Arrays.asList(TestDataFactory.createExperiencedDriver()))
                    .coverageAmount(new BigDecimal("100000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
            
            ExpectedOutcome outcome = ExpectedOutcome.failure(
                    "Vehicle year must be 1900 or later"
            );
            
            return new TestScenario(
                    "Invalid Vehicle Year",
                    "Vehicle year is before 1900",
                    request,
                    outcome
            );
        }
        
        /**
         * Invalid coverage amount (below minimum)
         */
        public static TestScenario getInvalidCoverageAmountScenario() {
            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.HONDA_ACCORD_2021)
                    .drivers(Arrays.asList(TestDataFactory.createExperiencedDriver()))
                    .coverageAmount(new BigDecimal("10000.00")) // Below minimum
                    .deductible(new BigDecimal("1000.00"))
                    .build();
            
            ExpectedOutcome outcome = ExpectedOutcome.failure(
                    "Coverage amount must be at least $25,000"
            );
            
            return new TestScenario(
                    "Invalid Coverage Amount",
                    "Coverage amount below minimum requirement",
                    request,
                    outcome
            );
        }
        
        /**
         * Invalid driver age (underage)
         */
        public static TestScenario getUnderageDriverScenario() {
            DriverDto underageDriver = DriverDto.builder()
                    .firstName("Too")
                    .lastName("Young")
                    .dateOfBirth(LocalDate.now().minusYears(15)) // 15 years old
                    .licenseNumber("UNDER123456")
                    .licenseState("CA")
                    .yearsOfExperience(0)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(false)
                    .build();
            
            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.HONDA_CIVIC_2020)
                    .drivers(Arrays.asList(underageDriver))
                    .coverageAmount(new BigDecimal("100000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
            
            ExpectedOutcome outcome = ExpectedOutcome.failure(
                    "Driver must be at least 16 years old"
            );
            
            return new TestScenario(
                    "Underage Driver",
                    "Driver is under 16 years old",
                    request,
                    outcome
            );
        }
    }
    
    // Integration test scenarios (with external services)
    public static class IntegrationScenarios {
        
        /**
         * Scenario requiring credit check
         */
        public static TestScenario getCreditCheckScenario() {
            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.BMW_X5_2023)
                    .drivers(Arrays.asList(TestDataFactory.createExperiencedDriver()))
                    .coverageAmount(new BigDecimal("500000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
            
            ExpectedOutcome outcome = ExpectedOutcome.success(
                    new BigDecimal("2000.00"),
                    new BigDecimal("2800.00"),
                    Arrays.asList("Safe Driver Discount - 15%"),
                    "MEDIUM"
            );
            
            return new TestScenario(
                    "Credit Check Required",
                    "High-value vehicle requiring credit verification",
                    request,
                    outcome
            );
        }
        
        /**
         * Scenario requiring risk assessment
         */
        public static TestScenario getRiskAssessmentScenario() {
            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.CHEVROLET_CORVETTE_2023)
                    .drivers(Arrays.asList(TestDataFactory.createYoungDriver()))
                    .coverageAmount(new BigDecimal("300000.00"))
                    .deductible(new BigDecimal("500.00"))
                    .build();
            
            ExpectedOutcome outcome = ExpectedOutcome.success(
                    new BigDecimal("3500.00"),
                    new BigDecimal("5000.00"),
                    Collections.emptyList(),
                    "HIGH"
            );
            
            return new TestScenario(
                    "Risk Assessment Required",
                    "High-risk combination requiring external assessment",
                    request,
                    outcome
            );
        }
    }
    
    // Performance test scenarios
    public static class PerformanceScenarios {
        
        /**
         * Generate batch of standard quotes for load testing
         */
        public static List<QuoteRequestDto> generateLoadTestBatch(int count) {
            List<QuoteRequestDto> batch = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                batch.add(TestDataFactory.createRandomQuoteRequest());
            }
            return batch;
        }
        
        /**
         * Generate concurrent test scenarios
         */
        public static List<TestScenario> generateConcurrentScenarios(int count) {
            List<TestScenario> scenarios = new ArrayList<>();
            
            for (int i = 0; i < count; i++) {
                QuoteRequestDto request = TestDataFactory.createRandomQuoteRequest();
                ExpectedOutcome outcome = ExpectedOutcome.success(
                        new BigDecimal("500.00"),
                        new BigDecimal("5000.00"),
                        Collections.emptyList(),
                        "VARIES"
                );
                
                scenarios.add(new TestScenario(
                        "Concurrent Test " + i,
                        "Load test scenario " + i,
                        request,
                        outcome
                ));
            }
            
            return scenarios;
        }
        
        /**
         * Generate stress test with edge cases
         */
        public static List<TestScenario> generateStressTestScenarios() {
            List<TestScenario> scenarios = new ArrayList<>();
            
            // Maximum drivers scenario
            List<DriverDto> maxDrivers = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                maxDrivers.add(TestDataFactory.createRandomDriver());
            }
            
            QuoteRequestDto maxDriverRequest = QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.CHEVROLET_TAHOE_2022)
                    .drivers(maxDrivers)
                    .coverageAmount(new BigDecimal("1000000.00"))
                    .deductible(new BigDecimal("250.00"))
                    .build();
            
            scenarios.add(new TestScenario(
                    "Maximum Drivers",
                    "Quote with maximum number of drivers",
                    maxDriverRequest,
                    ExpectedOutcome.success(
                            new BigDecimal("3000.00"),
                            new BigDecimal("8000.00"),
                            Collections.emptyList(),
                            "HIGH"
                    )
            ));
            
            // Minimum values scenario
            QuoteRequestDto minValueRequest = QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.FORD_FOCUS_2012)
                    .drivers(Arrays.asList(TestDataFactory.createSeniorDriver()))
                    .coverageAmount(new BigDecimal("25000.00"))
                    .deductible(new BigDecimal("5000.00"))
                    .build();
            
            scenarios.add(new TestScenario(
                    "Minimum Values",
                    "Quote with minimum coverage and maximum deductible",
                    minValueRequest,
                    ExpectedOutcome.success(
                            new BigDecimal("400.00"),
                            new BigDecimal("600.00"),
                            Arrays.asList("Safe Driver Discount - 15%", "Multi-Policy Discount - 10%"),
                            "LOW"
                    )
            ));
            
            return scenarios;
        }
    }
    
    // Boundary test scenarios
    public static class BoundaryScenarios {
        
        /**
         * Get all boundary test scenarios
         */
        public static List<TestScenario> getAllBoundaryScenarios() {
            List<TestScenario> scenarios = new ArrayList<>();
            
            // Minimum valid age (16)
            DriverDto sixteenYearOld = DriverDto.builder()
                    .firstName("Young")
                    .lastName("Driver")
                    .dateOfBirth(LocalDate.now().minusYears(16))
                    .licenseNumber("MIN123456")
                    .licenseState("CA")
                    .yearsOfExperience(0)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(false)
                    .build();
            
            scenarios.add(new TestScenario(
                    "Minimum Age Driver",
                    "Driver exactly 16 years old",
                    QuoteRequestDto.builder()
                            .vehicle(VehicleFixtures.HONDA_CIVIC_2020)
                            .drivers(Arrays.asList(sixteenYearOld))
                            .coverageAmount(new BigDecimal("25000.00"))
                            .deductible(new BigDecimal("1000.00"))
                            .build(),
                    ExpectedOutcome.success(
                            new BigDecimal("2500.00"),
                            new BigDecimal("3500.00"),
                            Collections.emptyList(),
                            "HIGH"
                    )
            ));
            
            // Maximum valid age (100)
            DriverDto centenarian = DriverDto.builder()
                    .firstName("Elder")
                    .lastName("Driver")
                    .dateOfBirth(LocalDate.now().minusYears(100))
                    .licenseNumber("MAX123456")
                    .licenseState("FL")
                    .yearsOfExperience(80)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build();
            
            scenarios.add(new TestScenario(
                    "Maximum Age Driver",
                    "Driver exactly 100 years old",
                    QuoteRequestDto.builder()
                            .vehicle(VehicleFixtures.TOYOTA_CAMRY_2022)
                            .drivers(Arrays.asList(centenarian))
                            .coverageAmount(new BigDecimal("100000.00"))
                            .deductible(new BigDecimal("2000.00"))
                            .build(),
                    ExpectedOutcome.success(
                            new BigDecimal("1200.00"),
                            new BigDecimal("1800.00"),
                            Arrays.asList("Safe Driver Discount - 15%", "Multi-Policy Discount - 10%"),
                            "MEDIUM"
                    )
            ));
            
            // Minimum coverage amount
            scenarios.add(new TestScenario(
                    "Minimum Coverage",
                    "Exactly $25,000 coverage",
                    QuoteRequestDto.builder()
                            .vehicle(VehicleFixtures.NISSAN_SENTRA_2021)
                            .drivers(Arrays.asList(TestDataFactory.createExperiencedDriver()))
                            .coverageAmount(new BigDecimal("25000.00"))
                            .deductible(new BigDecimal("1000.00"))
                            .build(),
                    ExpectedOutcome.success(
                            new BigDecimal("500.00"),
                            new BigDecimal("700.00"),
                            Arrays.asList("Safe Driver Discount - 15%"),
                            "LOW"
                    )
            ));
            
            // Maximum coverage amount
            scenarios.add(new TestScenario(
                    "Maximum Coverage",
                    "Exactly $1,000,000 coverage",
                    QuoteRequestDto.builder()
                            .vehicle(VehicleFixtures.PORSCHE_911_2024)
                            .drivers(Arrays.asList(TestDataFactory.createExperiencedDriver()))
                            .coverageAmount(new BigDecimal("1000000.00"))
                            .deductible(new BigDecimal("500.00"))
                            .build(),
                    ExpectedOutcome.success(
                            new BigDecimal("4000.00"),
                            new BigDecimal("6000.00"),
                            Arrays.asList("Safe Driver Discount - 15%"),
                            "HIGH"
                    )
            ));
            
            return scenarios;
        }
    }
    
    /**
     * Get all test scenarios for comprehensive testing
     */
    public static List<TestScenario> getAllTestScenarios() {
        List<TestScenario> allScenarios = new ArrayList<>();
        
        // Add premium calculation scenarios
        allScenarios.add(PremiumCalculationScenarios.getLowRiskScenario());
        allScenarios.add(PremiumCalculationScenarios.getHighRiskScenario());
        allScenarios.add(PremiumCalculationScenarios.getMultiDiscountScenario());
        
        // Add validation scenarios
        allScenarios.add(ValidationScenarios.getInvalidVehicleYearScenario());
        allScenarios.add(ValidationScenarios.getInvalidCoverageAmountScenario());
        allScenarios.add(ValidationScenarios.getUnderageDriverScenario());
        
        // Add integration scenarios
        allScenarios.add(IntegrationScenarios.getCreditCheckScenario());
        allScenarios.add(IntegrationScenarios.getRiskAssessmentScenario());
        
        // Add boundary scenarios
        allScenarios.addAll(BoundaryScenarios.getAllBoundaryScenarios());
        
        // Add performance scenarios
        allScenarios.addAll(PerformanceScenarios.generateStressTestScenarios());
        
        return allScenarios;
    }
    
    /**
     * Get scenario by name for targeted testing
     */
    public static TestScenario getScenarioByName(String name) {
        return getAllTestScenarios().stream()
                .filter(scenario -> scenario.name.equals(name))
                .findFirst()
                .orElse(null);
    }
}