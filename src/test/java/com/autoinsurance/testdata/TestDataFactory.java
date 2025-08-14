package com.autoinsurance.testdata;

import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.VehicleDto;
import com.autoinsurance.quote.entity.Quote;
import com.autoinsurance.external.dto.RiskAssessmentRequest;
import com.autoinsurance.external.dto.RiskAssessmentResponse;
import com.autoinsurance.external.dto.CreditCheckRequest;
import com.autoinsurance.external.dto.CreditCheckResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Centralized factory for generating test data across all test scenarios.
 * Supports both fixed and randomized test data for comprehensive coverage.
 */
public class TestDataFactory {
    
    // Common test data constants
    public static final String[] VEHICLE_MAKES = {
        "Honda", "Toyota", "Ford", "Chevrolet", "Nissan", "BMW", "Mercedes-Benz", 
        "Audi", "Volkswagen", "Subaru", "Mazda", "Hyundai", "Kia", "Lexus", "Tesla"
    };
    
    public static final String[][] VEHICLE_MODELS = {
        {"Accord", "Civic", "CR-V", "Pilot", "Odyssey"},
        {"Camry", "Corolla", "RAV4", "Highlander", "Prius"},
        {"F-150", "Explorer", "Escape", "Mustang", "Focus"},
        {"Silverado", "Equinox", "Cruze", "Malibu", "Tahoe"},
        {"Altima", "Sentra", "Rogue", "Murano", "Pathfinder"},
        {"X3", "X5", "3 Series", "5 Series", "7 Series"},
        {"C-Class", "E-Class", "S-Class", "GLE", "GLC"},
        {"A4", "A6", "Q5", "Q7", "Q3"},
        {"Jetta", "Passat", "Tiguan", "Atlas", "Golf"},
        {"Outback", "Forester", "Impreza", "Legacy", "Ascent"},
        {"CX-5", "Mazda3", "CX-9", "CX-3", "Mazda6"},
        {"Elantra", "Sonata", "Tucson", "Santa Fe", "Accent"},
        {"Optima", "Sorento", "Sportage", "Rio", "Forte"},
        {"ES", "RX", "NX", "GX", "LS"},
        {"Model S", "Model 3", "Model X", "Model Y", "Cybertruck"}
    };
    
    public static final String[] FIRST_NAMES = {
        "John", "Jane", "Michael", "Sarah", "David", "Lisa", "Robert", "Mary",
        "William", "Jennifer", "James", "Patricia", "Christopher", "Linda",
        "Daniel", "Elizabeth", "Matthew", "Barbara", "Anthony", "Susan"
    };
    
    public static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
        "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez",
        "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"
    };
    
    public static final String[] US_STATES = {
        "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID",
        "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS",
        "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK",
        "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    };
    
    // Vehicle Test Data Builders
    
    /**
     * Creates a standard test vehicle (Honda Accord 2021)
     */
    public static VehicleDto createStandardVehicle() {
        return VehicleDto.builder()
                .make("Honda")
                .model("Accord")
                .year(2021)
                .vin("1HGCV1F31MA123456")
                .currentValue(new BigDecimal("30000.00"))
                .build();
    }
    
    /**
     * Creates a luxury test vehicle (BMW X5 2023)
     */
    public static VehicleDto createLuxuryVehicle() {
        return VehicleDto.builder()
                .make("BMW")
                .model("X5")
                .year(2023)
                .vin("5UXCR6C0XP9123456")
                .currentValue(new BigDecimal("75000.00"))
                .build();
    }
    
    /**
     * Creates an older test vehicle (Toyota Camry 2010)
     */
    public static VehicleDto createOlderVehicle() {
        return VehicleDto.builder()
                .make("Toyota")
                .model("Camry")
                .year(2010)
                .vin("4T1BF3EK5AU123456")
                .currentValue(new BigDecimal("12000.00"))
                .build();
    }
    
    /**
     * Creates a high-value sports car (Tesla Model S 2024)
     */
    public static VehicleDto createSportsCar() {
        return VehicleDto.builder()
                .make("Tesla")
                .model("Model S")
                .year(2024)
                .vin("5YJ3E1EA0PF123456")
                .currentValue(new BigDecimal("95000.00"))
                .build();
    }
    
    /**
     * Creates a random vehicle for load testing
     */
    public static VehicleDto createRandomVehicle() {
        int makeIndex = ThreadLocalRandom.current().nextInt(VEHICLE_MAKES.length);
        String make = VEHICLE_MAKES[makeIndex];
        String model = VEHICLE_MODELS[makeIndex][ThreadLocalRandom.current().nextInt(VEHICLE_MODELS[makeIndex].length)];
        int year = ThreadLocalRandom.current().nextInt(2010, 2025);
        String vin = generateRandomVin();
        BigDecimal value = generateRandomVehicleValue(year, make);
        
        return VehicleDto.builder()
                .make(make)
                .model(model)
                .year(year)
                .vin(vin)
                .currentValue(value)
                .build();
    }
    
    // Driver Test Data Builders
    
    /**
     * Creates an experienced safe driver (35 years old, 15 years experience)
     */
    public static DriverDto createExperiencedDriver() {
        return DriverDto.builder()
                .firstName("John")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1988, 6, 15))
                .licenseNumber("D123456789")
                .licenseState("CA")
                .yearsOfExperience(15)
                .safeDriverDiscount(true)
                .multiPolicyDiscount(false)
                .build();
    }
    
    /**
     * Creates a young driver (18 years old, new driver)
     */
    public static DriverDto createYoungDriver() {
        return DriverDto.builder()
                .firstName("Emily")
                .lastName("Johnson")
                .dateOfBirth(LocalDate.of(2005, 3, 20))
                .licenseNumber("D987654321")
                .licenseState("TX")
                .yearsOfExperience(0)
                .safeDriverDiscount(false)
                .multiPolicyDiscount(false)
                .build();
    }
    
    /**
     * Creates a senior driver (65 years old, 45 years experience)
     */
    public static DriverDto createSeniorDriver() {
        return DriverDto.builder()
                .firstName("Robert")
                .lastName("Davis")
                .dateOfBirth(LocalDate.of(1958, 11, 8))
                .licenseNumber("D555666777")
                .licenseState("FL")
                .yearsOfExperience(45)
                .safeDriverDiscount(true)
                .multiPolicyDiscount(true)
                .build();
    }
    
    /**
     * Creates a driver with multiple discounts
     */
    public static DriverDto createDiscountEligibleDriver() {
        return DriverDto.builder()
                .firstName("Sarah")
                .lastName("Wilson")
                .dateOfBirth(LocalDate.of(1985, 9, 12))
                .licenseNumber("D444555666")
                .licenseState("NY")
                .yearsOfExperience(18)
                .safeDriverDiscount(true)
                .multiPolicyDiscount(true)
                .build();
    }
    
    /**
     * Creates a random driver for load testing
     */
    public static DriverDto createRandomDriver() {
        String firstName = FIRST_NAMES[ThreadLocalRandom.current().nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[ThreadLocalRandom.current().nextInt(LAST_NAMES.length)];
        LocalDate birthDate = generateRandomBirthDate();
        String licenseNumber = generateRandomLicenseNumber();
        String state = US_STATES[ThreadLocalRandom.current().nextInt(US_STATES.length)];
        int experience = ThreadLocalRandom.current().nextInt(0, 50);
        boolean safeDriver = ThreadLocalRandom.current().nextBoolean();
        boolean multiPolicy = ThreadLocalRandom.current().nextBoolean();
        
        return DriverDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(birthDate)
                .licenseNumber(licenseNumber)
                .licenseState(state)
                .yearsOfExperience(experience)
                .safeDriverDiscount(safeDriver)
                .multiPolicyDiscount(multiPolicy)
                .build();
    }
    
    // Quote Request Test Data Builders
    
    /**
     * Creates a standard quote request (standard vehicle, experienced driver)
     */
    public static QuoteRequestDto createStandardQuoteRequest() {
        return QuoteRequestDto.builder()
                .vehicle(createStandardVehicle())
                .drivers(Arrays.asList(createExperiencedDriver()))
                .coverageAmount(new BigDecimal("250000.00"))
                .deductible(new BigDecimal("1000.00"))
                .build();
    }
    
    /**
     * Creates a high-risk quote request (luxury vehicle, young driver)
     */
    public static QuoteRequestDto createHighRiskQuoteRequest() {
        return QuoteRequestDto.builder()
                .vehicle(createLuxuryVehicle())
                .drivers(Arrays.asList(createYoungDriver()))
                .coverageAmount(new BigDecimal("500000.00"))
                .deductible(new BigDecimal("500.00"))
                .build();
    }
    
    /**
     * Creates a low-risk quote request (older vehicle, senior driver)
     */
    public static QuoteRequestDto createLowRiskQuoteRequest() {
        return QuoteRequestDto.builder()
                .vehicle(createOlderVehicle())
                .drivers(Arrays.asList(createSeniorDriver()))
                .coverageAmount(new BigDecimal("100000.00"))
                .deductible(new BigDecimal("2000.00"))
                .build();
    }
    
    /**
     * Creates a multi-driver quote request
     */
    public static QuoteRequestDto createMultiDriverQuoteRequest() {
        return QuoteRequestDto.builder()
                .vehicle(createStandardVehicle())
                .drivers(Arrays.asList(
                    createExperiencedDriver(),
                    createDiscountEligibleDriver()
                ))
                .coverageAmount(new BigDecimal("300000.00"))
                .deductible(new BigDecimal("1000.00"))
                .build();
    }
    
    /**
     * Creates a random quote request for load testing
     */
    public static QuoteRequestDto createRandomQuoteRequest() {
        VehicleDto vehicle = createRandomVehicle();
        DriverDto driver = createRandomDriver();
        BigDecimal coverage = generateRandomCoverage();
        BigDecimal deductible = generateRandomDeductible();
        
        return QuoteRequestDto.builder()
                .vehicle(vehicle)
                .drivers(Arrays.asList(driver))
                .coverageAmount(coverage)
                .deductible(deductible)
                .build();
    }
    
    // Quote Entity Test Data Builders
    
    /**
     * Creates a standard quote entity
     */
    public static Quote createStandardQuote() {
        return Quote.builder()
                .id(UUID.randomUUID().toString())
                .premium(new BigDecimal("1200.00"))
                .monthlyPremium(new BigDecimal("100.00"))
                .coverageAmount(new BigDecimal("250000.00"))
                .deductible(new BigDecimal("1000.00"))
                .validUntil(LocalDate.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .vehicleMake("Honda")
                .vehicleModel("Accord")
                .vehicleYear(2021)
                .vehicleVin("1HGCV1F31MA123456")
                .vehicleCurrentValue(new BigDecimal("30000.00"))
                .primaryDriverName("John Smith")
                .primaryDriverLicense("D123456789")
                .discountsApplied(Arrays.asList("Safe Driver Discount - 15%"))
                .build();
    }
    
    /**
     * Creates a high-premium quote entity
     */
    public static Quote createHighPremiumQuote() {
        return Quote.builder()
                .id(UUID.randomUUID().toString())
                .premium(new BigDecimal("3500.00"))
                .monthlyPremium(new BigDecimal("291.67"))
                .coverageAmount(new BigDecimal("500000.00"))
                .deductible(new BigDecimal("500.00"))
                .validUntil(LocalDate.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .vehicleMake("BMW")
                .vehicleModel("X5")
                .vehicleYear(2023)
                .vehicleVin("5UXCR6C0XP9123456")
                .vehicleCurrentValue(new BigDecimal("75000.00"))
                .primaryDriverName("Emily Johnson")
                .primaryDriverLicense("D987654321")
                .discountsApplied(Collections.emptyList())
                .build();
    }
    
    /**
     * Creates a low-premium quote entity
     */
    public static Quote createLowPremiumQuote() {
        return Quote.builder()
                .id(UUID.randomUUID().toString())
                .premium(new BigDecimal("800.00"))
                .monthlyPremium(new BigDecimal("66.67"))
                .coverageAmount(new BigDecimal("100000.00"))
                .deductible(new BigDecimal("2000.00"))
                .validUntil(LocalDate.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .vehicleMake("Toyota")
                .vehicleModel("Camry")
                .vehicleYear(2010)
                .vehicleVin("4T1BF3EK5AU123456")
                .vehicleCurrentValue(new BigDecimal("12000.00"))
                .primaryDriverName("Robert Davis")
                .primaryDriverLicense("D555666777")
                .discountsApplied(Arrays.asList("Safe Driver Discount - 15%", "Multi-Policy Discount - 10%"))
                .build();
    }
    
    // External Service Test Data Builders
    
    /**
     * Creates a risk assessment request
     */
    public static RiskAssessmentRequest createRiskAssessmentRequest() {
        return new RiskAssessmentRequest(
            "John Smith",
            LocalDate.of(1988, 6, 15),
            "D123456789",
            "CA",
            15,
            "Honda",
            "Accord",
            2021,
            "1HGCV1F31MA123456"
        );
    }
    
    /**
     * Creates a low-risk assessment response
     */
    public static RiskAssessmentResponse createLowRiskAssessmentResponse() {
        return new RiskAssessmentResponse(
            "LOW",
            85,
            Arrays.asList("Clean driving record", "Experienced driver"),
            UUID.randomUUID().toString()
        );
    }
    
    /**
     * Creates a high-risk assessment response
     */
    public static RiskAssessmentResponse createHighRiskAssessmentResponse() {
        return new RiskAssessmentResponse(
            "HIGH",
            25,
            Arrays.asList("Young driver", "High-value vehicle", "Multiple violations"),
            UUID.randomUUID().toString()
        );
    }
    
    /**
     * Creates a credit check request
     */
    public static CreditCheckRequest createCreditCheckRequest() {
        return new CreditCheckRequest(
            "John",
            "Smith",
            LocalDate.of(1988, 6, 15),
            "123-45-6789"
        );
    }
    
    /**
     * Creates an excellent credit check response
     */
    public static CreditCheckResponse createExcellentCreditResponse() {
        return new CreditCheckResponse(
            800,
            "EXCELLENT",
            true,
            UUID.randomUUID().toString()
        );
    }
    
    /**
     * Creates a poor credit check response
     */
    public static CreditCheckResponse createPoorCreditResponse() {
        return new CreditCheckResponse(
            550,
            "POOR",
            false,
            UUID.randomUUID().toString()
        );
    }
    
    // Utility Methods for Random Data Generation
    
    private static String generateRandomVin() {
        String chars = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789";
        StringBuilder vin = new StringBuilder();
        for (int i = 0; i < 17; i++) {
            vin.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        }
        return vin.toString();
    }
    
    private static BigDecimal generateRandomVehicleValue(int year, String make) {
        int currentYear = LocalDate.now().getYear();
        int age = currentYear - year;
        
        // Base value ranges by make
        int baseValue = switch (make) {
            case "BMW", "Mercedes-Benz", "Audi", "Tesla" -> ThreadLocalRandom.current().nextInt(40000, 100000);
            case "Honda", "Toyota", "Mazda", "Subaru" -> ThreadLocalRandom.current().nextInt(20000, 50000);
            default -> ThreadLocalRandom.current().nextInt(15000, 40000);
        };
        
        // Depreciate based on age
        double depreciationFactor = Math.max(0.3, 1.0 - (age * 0.1));
        return new BigDecimal(String.valueOf((int)(baseValue * depreciationFactor)));
    }
    
    private static LocalDate generateRandomBirthDate() {
        int year = ThreadLocalRandom.current().nextInt(1950, 2005);
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int day = ThreadLocalRandom.current().nextInt(1, 29); // Simplified to avoid month-specific logic
        return LocalDate.of(year, month, day);
    }
    
    private static String generateRandomLicenseNumber() {
        return "D" + ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
    }
    
    private static BigDecimal generateRandomCoverage() {
        int[] coverageOptions = {25000, 50000, 100000, 250000, 500000, 1000000};
        return new BigDecimal(coverageOptions[ThreadLocalRandom.current().nextInt(coverageOptions.length)]);
    }
    
    private static BigDecimal generateRandomDeductible() {
        int[] deductibleOptions = {250, 500, 1000, 2000, 5000, 10000};
        return new BigDecimal(deductibleOptions[ThreadLocalRandom.current().nextInt(deductibleOptions.length)]);
    }
    
    // Edge Case Test Data
    
    /**
     * Creates invalid vehicle data for validation testing
     */
    public static VehicleDto createInvalidVehicle() {
        return VehicleDto.builder()
                .make("") // Invalid: empty
                .model("") // Invalid: empty
                .year(1899) // Invalid: too old
                .vin("INVALID") // Invalid: wrong format
                .currentValue(new BigDecimal("-1000")) // Invalid: negative
                .build();
    }
    
    /**
     * Creates invalid driver data for validation testing
     */
    public static DriverDto createInvalidDriver() {
        return DriverDto.builder()
                .firstName("") // Invalid: empty
                .lastName("") // Invalid: empty
                .dateOfBirth(LocalDate.now().plusDays(1)) // Invalid: future date
                .licenseNumber("") // Invalid: empty
                .licenseState("ZZ") // Invalid: not a real state
                .yearsOfExperience(-5) // Invalid: negative
                .build();
    }
    
    /**
     * Creates boundary test data for coverage amounts
     */
    public static QuoteRequestDto createBoundaryQuoteRequest() {
        return QuoteRequestDto.builder()
                .vehicle(createStandardVehicle())
                .drivers(Arrays.asList(createExperiencedDriver()))
                .coverageAmount(new BigDecimal("25000")) // Minimum allowed
                .deductible(new BigDecimal("250")) // Minimum allowed
                .build();
    }
    
    /**
     * Creates maximum boundary test data
     */
    public static QuoteRequestDto createMaxBoundaryQuoteRequest() {
        return QuoteRequestDto.builder()
                .vehicle(createStandardVehicle())
                .drivers(Arrays.asList(createExperiencedDriver()))
                .coverageAmount(new BigDecimal("1000000")) // Maximum allowed
                .deductible(new BigDecimal("10000")) // Maximum allowed
                .build();
    }
}