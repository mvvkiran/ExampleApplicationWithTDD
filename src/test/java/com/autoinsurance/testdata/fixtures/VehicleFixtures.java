package com.autoinsurance.testdata.fixtures;

import com.autoinsurance.quote.dto.VehicleDto;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Fixed test data fixtures for vehicle testing scenarios.
 * Contains predefined vehicle data for consistent testing across different test suites.
 */
public class VehicleFixtures {
    
    // Economy Vehicles
    public static final VehicleDto HONDA_CIVIC_2020 = VehicleDto.builder()
            .make("Honda")
            .model("Civic")
            .year(2020)
            .vin("2HGFC2F50LH123456")
            .currentValue(new BigDecimal("22000.00"))
            .build();
    
    public static final VehicleDto TOYOTA_COROLLA_2019 = VehicleDto.builder()
            .make("Toyota")
            .model("Corolla")
            .year(2019)
            .vin("2T1BURHE5KC123456")
            .currentValue(new BigDecimal("20000.00"))
            .build();
    
    public static final VehicleDto NISSAN_SENTRA_2021 = VehicleDto.builder()
            .make("Nissan")
            .model("Sentra")
            .year(2021)
            .vin("3N1AB8CV5MY123456")
            .currentValue(new BigDecimal("19500.00"))
            .build();
    
    // Mid-Range Vehicles
    public static final VehicleDto HONDA_ACCORD_2021 = VehicleDto.builder()
            .make("Honda")
            .model("Accord")
            .year(2021)
            .vin("1HGCV1F31MA123456")
            .currentValue(new BigDecimal("30000.00"))
            .build();
    
    public static final VehicleDto TOYOTA_CAMRY_2022 = VehicleDto.builder()
            .make("Toyota")
            .model("Camry")
            .year(2022)
            .vin("4T1C11AK5NU123456")
            .currentValue(new BigDecimal("32000.00"))
            .build();
    
    public static final VehicleDto FORD_ESCAPE_2020 = VehicleDto.builder()
            .make("Ford")
            .model("Escape")
            .year(2020)
            .vin("1FMCU9HD9LUB123456")
            .currentValue(new BigDecimal("28000.00"))
            .build();
    
    // SUVs and Trucks
    public static final VehicleDto FORD_F150_2023 = VehicleDto.builder()
            .make("Ford")
            .model("F-150")
            .year(2023)
            .vin("1FTEW1EP5PFB123456")
            .currentValue(new BigDecimal("45000.00"))
            .build();
    
    public static final VehicleDto CHEVROLET_TAHOE_2022 = VehicleDto.builder()
            .make("Chevrolet")
            .model("Tahoe")
            .year(2022)
            .vin("1GNSKBKC7NR123456")
            .currentValue(new BigDecimal("65000.00"))
            .build();
    
    public static final VehicleDto TOYOTA_RAV4_2021 = VehicleDto.builder()
            .make("Toyota")
            .model("RAV4")
            .year(2021)
            .vin("2T3F1RFV5MW123456")
            .currentValue(new BigDecimal("35000.00"))
            .build();
    
    // Luxury Vehicles
    public static final VehicleDto BMW_X5_2023 = VehicleDto.builder()
            .make("BMW")
            .model("X5")
            .year(2023)
            .vin("5UXCR6C0XP9123456")
            .currentValue(new BigDecimal("75000.00"))
            .build();
    
    public static final VehicleDto MERCEDES_E_CLASS_2022 = VehicleDto.builder()
            .make("Mercedes-Benz")
            .model("E-Class")
            .year(2022)
            .vin("WDDZF4JB1NA123456")
            .currentValue(new BigDecimal("68000.00"))
            .build();
    
    public static final VehicleDto AUDI_Q7_2023 = VehicleDto.builder()
            .make("Audi")
            .model("Q7")
            .year(2023)
            .vin("WA1LAAF70PD123456")
            .currentValue(new BigDecimal("72000.00"))
            .build();
    
    // Sports Cars
    public static final VehicleDto FORD_MUSTANG_2022 = VehicleDto.builder()
            .make("Ford")
            .model("Mustang")
            .year(2022)
            .vin("1FA6P8TH7N5123456")
            .currentValue(new BigDecimal("42000.00"))
            .build();
    
    public static final VehicleDto CHEVROLET_CORVETTE_2023 = VehicleDto.builder()
            .make("Chevrolet")
            .model("Corvette")
            .year(2023)
            .vin("1G1YB2D40P5123456")
            .currentValue(new BigDecimal("85000.00"))
            .build();
    
    // Electric Vehicles
    public static final VehicleDto TESLA_MODEL_3_2023 = VehicleDto.builder()
            .make("Tesla")
            .model("Model 3")
            .year(2023)
            .vin("5YJ3E1EA0PF123456")
            .currentValue(new BigDecimal("45000.00"))
            .build();
    
    public static final VehicleDto TESLA_MODEL_S_2024 = VehicleDto.builder()
            .make("Tesla")
            .model("Model S")
            .year(2024)
            .vin("5YJ3E7EB0PF234567")
            .currentValue(new BigDecimal("95000.00"))
            .build();
    
    public static final VehicleDto NISSAN_LEAF_2022 = VehicleDto.builder()
            .make("Nissan")
            .model("Leaf")
            .year(2022)
            .vin("1N4AZ1CP8NC123456")
            .currentValue(new BigDecimal("32000.00"))
            .build();
    
    // Older Vehicles (for depreciation testing)
    public static final VehicleDto HONDA_ACCORD_2015 = VehicleDto.builder()
            .make("Honda")
            .model("Accord")
            .year(2015)
            .vin("1HGCR2F30FA123456")
            .currentValue(new BigDecimal("15000.00"))
            .build();
    
    public static final VehicleDto TOYOTA_CAMRY_2010 = VehicleDto.builder()
            .make("Toyota")
            .model("Camry")
            .year(2010)
            .vin("4T1BF3EK5AU123456")
            .currentValue(new BigDecimal("12000.00"))
            .build();
    
    public static final VehicleDto FORD_FOCUS_2012 = VehicleDto.builder()
            .make("Ford")
            .model("Focus")
            .year(2012)
            .vin("1FAHP3F24CL123456")
            .currentValue(new BigDecimal("8500.00"))
            .build();
    
    // High-Value Vehicles (for coverage testing)
    public static final VehicleDto PORSCHE_911_2024 = VehicleDto.builder()
            .make("Porsche")
            .model("911")
            .year(2024)
            .vin("WP0AA2A9XPS123456")
            .currentValue(new BigDecimal("125000.00"))
            .build();
    
    public static final VehicleDto LAMBORGHINI_HURACAN_2023 = VehicleDto.builder()
            .make("Lamborghini")
            .model("Huracan")
            .year(2023)
            .vin("ZHWUC1ZF7PLA123456")
            .currentValue(new BigDecimal("250000.00"))
            .build();
    
    // Commercial Vehicles
    public static final VehicleDto FORD_TRANSIT_2022 = VehicleDto.builder()
            .make("Ford")
            .model("Transit")
            .year(2022)
            .vin("1FTBW2CM7NKA123456")
            .currentValue(new BigDecimal("38000.00"))
            .build();
    
    // Invalid/Edge Case Vehicles for Validation Testing
    public static final VehicleDto INVALID_EMPTY_MAKE = VehicleDto.builder()
            .make("")
            .model("TestModel")
            .year(2020)
            .vin("1HGCV1F31MA123456")
            .currentValue(new BigDecimal("25000.00"))
            .build();
    
    public static final VehicleDto INVALID_OLD_YEAR = VehicleDto.builder()
            .make("Ford")
            .model("Model T")
            .year(1899)
            .vin("1HGCV1F31MA123456")
            .currentValue(new BigDecimal("5000.00"))
            .build();
    
    public static final VehicleDto INVALID_FUTURE_YEAR = VehicleDto.builder()
            .make("Honda")
            .model("FutureCar")
            .year(2030)
            .vin("1HGCV1F31MA123456")
            .currentValue(new BigDecimal("50000.00"))
            .build();
    
    public static final VehicleDto INVALID_SHORT_VIN = VehicleDto.builder()
            .make("Toyota")
            .model("Camry")
            .year(2020)
            .vin("SHORT")
            .currentValue(new BigDecimal("25000.00"))
            .build();
    
    public static final VehicleDto INVALID_NEGATIVE_VALUE = VehicleDto.builder()
            .make("Honda")
            .model("Civic")
            .year(2020)
            .vin("1HGCV1F31MA123456")
            .currentValue(new BigDecimal("-5000.00"))
            .build();
    
    public static final VehicleDto INVALID_ZERO_VALUE = VehicleDto.builder()
            .make("Ford")
            .model("Escape")
            .year(2020)
            .vin("1HGCV1F31MA123456")
            .currentValue(BigDecimal.ZERO)
            .build();
    
    // Collections for different test scenarios
    
    /**
     * Economy vehicles for basic coverage testing
     */
    public static final List<VehicleDto> ECONOMY_VEHICLES = Arrays.asList(
        HONDA_CIVIC_2020,
        TOYOTA_COROLLA_2019,
        NISSAN_SENTRA_2021
    );
    
    /**
     * Luxury vehicles for high-premium testing
     */
    public static final List<VehicleDto> LUXURY_VEHICLES = Arrays.asList(
        BMW_X5_2023,
        MERCEDES_E_CLASS_2022,
        AUDI_Q7_2023
    );
    
    /**
     * Sports cars for high-risk testing
     */
    public static final List<VehicleDto> SPORTS_CARS = Arrays.asList(
        FORD_MUSTANG_2022,
        CHEVROLET_CORVETTE_2023,
        PORSCHE_911_2024
    );
    
    /**
     * Electric vehicles for green discount testing
     */
    public static final List<VehicleDto> ELECTRIC_VEHICLES = Arrays.asList(
        TESLA_MODEL_3_2023,
        TESLA_MODEL_S_2024,
        NISSAN_LEAF_2022
    );
    
    /**
     * Older vehicles for depreciation testing
     */
    public static final List<VehicleDto> OLDER_VEHICLES = Arrays.asList(
        HONDA_ACCORD_2015,
        TOYOTA_CAMRY_2010,
        FORD_FOCUS_2012
    );
    
    /**
     * High-value vehicles for coverage limit testing
     */
    public static final List<VehicleDto> HIGH_VALUE_VEHICLES = Arrays.asList(
        TESLA_MODEL_S_2024,
        PORSCHE_911_2024,
        LAMBORGHINI_HURACAN_2023
    );
    
    /**
     * Invalid vehicles for validation testing
     */
    public static final List<VehicleDto> INVALID_VEHICLES = Arrays.asList(
        INVALID_EMPTY_MAKE,
        INVALID_OLD_YEAR,
        INVALID_FUTURE_YEAR,
        INVALID_SHORT_VIN,
        INVALID_NEGATIVE_VALUE,
        INVALID_ZERO_VALUE
    );
    
    /**
     * All valid vehicles for comprehensive testing
     */
    public static final List<VehicleDto> ALL_VALID_VEHICLES = Arrays.asList(
        HONDA_CIVIC_2020, TOYOTA_COROLLA_2019, NISSAN_SENTRA_2021,
        HONDA_ACCORD_2021, TOYOTA_CAMRY_2022, FORD_ESCAPE_2020,
        FORD_F150_2023, CHEVROLET_TAHOE_2022, TOYOTA_RAV4_2021,
        BMW_X5_2023, MERCEDES_E_CLASS_2022, AUDI_Q7_2023,
        FORD_MUSTANG_2022, CHEVROLET_CORVETTE_2023,
        TESLA_MODEL_3_2023, TESLA_MODEL_S_2024, NISSAN_LEAF_2022,
        HONDA_ACCORD_2015, TOYOTA_CAMRY_2010, FORD_FOCUS_2012,
        PORSCHE_911_2024, LAMBORGHINI_HURACAN_2023, FORD_TRANSIT_2022
    );
}