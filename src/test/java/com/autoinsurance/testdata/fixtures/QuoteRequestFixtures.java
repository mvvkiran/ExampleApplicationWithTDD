package com.autoinsurance.testdata.fixtures;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.VehicleDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Comprehensive quote request fixtures for various insurance scenarios.
 * Provides realistic quote combinations for different risk levels and coverage needs.
 */
public class QuoteRequestFixtures {
    
    // Standard quote scenarios
    public static class StandardScenarios {
        
        /**
         * Minimum coverage quote request
         */
        public static QuoteRequestDto getMinimumCoverageQuote() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.EconomyVehicles.getUsedEconomyCar())
                    .drivers(Arrays.asList(DriverFixtures.RiskProfiles.getLowRiskDrivers().get(0)))
                    .coverageAmount(new BigDecimal("25000.00")) // State minimum
                    .deductible(new BigDecimal("2500.00")) // High deductible for lower premium
                    .build();
        }
        
        /**
         * Standard coverage quote request
         */
        public static QuoteRequestDto getStandardCoverageQuote() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.StandardVehicles.getMidSizeSedan())
                    .drivers(Arrays.asList(DriverFixtures.AgeProfiles.getMiddleAgedDrivers().get(0)))
                    .coverageAmount(new BigDecimal("250000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
        }
        
        /**
         * Premium coverage quote request
         */
        public static QuoteRequestDto getPremiumCoverageQuote() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.LuxuryVehicles.getLuxurySedan())
                    .drivers(Arrays.asList(DriverFixtures.RiskProfiles.getLowRiskDrivers().get(0)))
                    .coverageAmount(new BigDecimal("500000.00"))
                    .deductible(new BigDecimal("500.00"))
                    .build();
        }
        
        /**
         * Maximum coverage quote request
         */
        public static QuoteRequestDto getMaximumCoverageQuote() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.LuxuryVehicles.getSuperCar())
                    .drivers(Arrays.asList(DriverFixtures.AgeProfiles.getMiddleAgedDrivers().get(0)))
                    .coverageAmount(new BigDecimal("1000000.00"))
                    .deductible(new BigDecimal("250.00"))
                    .build();
        }
    }
    
    // Family quote scenarios
    public static class FamilyScenarios {
        
        /**
         * Single parent with teen driver
         */
        public static QuoteRequestDto getSingleParentWithTeen() {
            List<DriverDto> drivers = new ArrayList<>();
            drivers.add(DriverFixtures.AgeProfiles.getMiddleAgedDrivers().get(0));
            drivers.add(DriverFixtures.AgeProfiles.getTeenDrivers().get(0));
            
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.FamilyVehicles.getMiniVan())
                    .drivers(drivers)
                    .coverageAmount(new BigDecimal("300000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
        }
        
        /**
         * Two parents with multiple vehicles
         */
        public static QuoteRequestDto getTwoParentFamily() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.FamilyVehicles.getThreeRowSUV())
                    .drivers(Arrays.asList(
                            DriverFixtures.getFamilyDrivers().get(0),
                            DriverFixtures.getFamilyDrivers().get(1)
                    ))
                    .coverageAmount(new BigDecimal("350000.00"))
                    .deductible(new BigDecimal("750.00"))
                    .build();
        }
        
        /**
         * Full family with teen drivers
         */
        public static QuoteRequestDto getFullFamilyWithTeens() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.FamilyVehicles.getStationWagon())
                    .drivers(DriverFixtures.getFamilyDrivers())
                    .coverageAmount(new BigDecimal("400000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
        }
    }
    
    // High-risk scenarios
    public static class HighRiskScenarios {
        
        /**
         * Young driver with sports car
         */
        public static QuoteRequestDto getYoungDriverSportsCar() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.PerformanceVehicles.getSportsCar())
                    .drivers(Arrays.asList(DriverFixtures.AgeProfiles.getYoungAdultDrivers().get(0)))
                    .coverageAmount(new BigDecimal("300000.00"))
                    .deductible(new BigDecimal("500.00"))
                    .build();
        }
        
        /**
         * High-risk driver with luxury vehicle
         */
        public static QuoteRequestDto getHighRiskDriverLuxuryCar() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.LuxuryVehicles.getLuxurySUV())
                    .drivers(Arrays.asList(DriverFixtures.RiskProfiles.getHighRiskDrivers().get(0)))
                    .coverageAmount(new BigDecimal("500000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
        }
        
        /**
         * Teen driver as primary on performance car
         */
        public static QuoteRequestDto getTeenDriverPerformanceCar() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.PerformanceVehicles.getMuscleCar())
                    .drivers(Arrays.asList(DriverFixtures.AgeProfiles.getTeenDrivers().get(0)))
                    .coverageAmount(new BigDecimal("250000.00"))
                    .deductible(new BigDecimal("500.00"))
                    .build();
        }
    }
    
    // Low-risk scenarios
    public static class LowRiskScenarios {
        
        /**
         * Senior driver with economy car
         */
        public static QuoteRequestDto getSeniorDriverEconomyCar() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.EconomyVehicles.getCompactCar())
                    .drivers(Arrays.asList(DriverFixtures.AgeProfiles.getSeniorDrivers().get(0)))
                    .coverageAmount(new BigDecimal("100000.00"))
                    .deductible(new BigDecimal("2000.00"))
                    .build();
        }
        
        /**
         * Experienced driver with hybrid vehicle
         */
        public static QuoteRequestDto getExperiencedDriverHybrid() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.ElectricVehicles.getHybridSedan())
                    .drivers(Arrays.asList(DriverFixtures.RiskProfiles.getLowRiskDrivers().get(0)))
                    .coverageAmount(new BigDecimal("200000.00"))
                    .deductible(new BigDecimal("1500.00"))
                    .build();
        }
        
        /**
         * Rural driver with standard vehicle
         */
        public static QuoteRequestDto getRuralDriverStandardVehicle() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.StandardVehicles.getCompactSUV())
                    .drivers(Arrays.asList(DriverFixtures.GeographicProfiles.getRuralDrivers().get(0)))
                    .coverageAmount(new BigDecimal("150000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
        }
    }
    
    // Commercial scenarios
    public static class CommercialScenarios {
        
        /**
         * Delivery driver quote
         */
        public static QuoteRequestDto getDeliveryDriverQuote() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.CommercialVehicles.getDeliveryVan())
                    .drivers(Arrays.asList(DriverFixtures.SpecialCases.getCommercialDriver()))
                    .coverageAmount(new BigDecimal("500000.00"))
                    .deductible(new BigDecimal("2500.00"))
                    .build();
        }
        
        /**
         * Rideshare driver quote
         */
        public static QuoteRequestDto getRideshareDriverQuote() {
            VehicleDto rideshareVehicle = VehicleDto.builder()
                    .make("Toyota")
                    .model("Camry")
                    .year(2021)
                    .vin("RIDE12345678901")
                    .currentValue(new BigDecimal("28000.00"))
                    .build();
            
            DriverDto rideshareDriver = DriverDto.builder()
                    .firstName("Carlos")
                    .lastName("Rodriguez")
                    .dateOfBirth(LocalDate.of(1985, 7, 10))
                    .licenseNumber("RS987654321")
                    .licenseState("CA")
                    .yearsOfExperience(15)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(false)
                    .build();
            
            return QuoteRequestDto.builder()
                    .vehicle(rideshareVehicle)
                    .drivers(Arrays.asList(rideshareDriver))
                    .coverageAmount(new BigDecimal("350000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
        }
    }
    
    // Geographic-specific scenarios
    public static class GeographicScenarios {
        
        /**
         * Urban high-traffic area quote
         */
        public static QuoteRequestDto getUrbanHighTrafficQuote() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.StandardVehicles.getCompactCar())
                    .drivers(Arrays.asList(DriverFixtures.GeographicProfiles.getUrbanDrivers().get(0)))
                    .coverageAmount(new BigDecimal("300000.00"))
                    .deductible(new BigDecimal("750.00"))
                    .build();
        }
        
        /**
         * Suburban commuter quote
         */
        public static QuoteRequestDto getSuburbanCommuterQuote() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.StandardVehicles.getMidSizeSUV())
                    .drivers(Arrays.asList(DriverFixtures.GeographicProfiles.getSuburbanDrivers().get(0)))
                    .coverageAmount(new BigDecimal("250000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
        }
        
        /**
         * Rural low-mileage quote
         */
        public static QuoteRequestDto getRuralLowMileageQuote() {
            return QuoteRequestDto.builder()
                    .vehicle(VehicleFixtures.TruckVehicles.getPickupTruck())
                    .drivers(Arrays.asList(DriverFixtures.GeographicProfiles.getRuralDrivers().get(0)))
                    .coverageAmount(new BigDecimal("150000.00"))
                    .deductible(new BigDecimal("1500.00"))
                    .build();
        }
    }
    
    // Edge case scenarios for testing
    public static class EdgeCaseScenarios {
        
        /**
         * Minimum valid values
         */
        public static QuoteRequestDto getMinimumValidQuote() {
            VehicleDto oldestValidVehicle = VehicleDto.builder()
                    .make("Ford")
                    .model("Model T")
                    .year(1990) // Assuming 1990 is minimum year
                    .vin("MIN1234567890123")
                    .currentValue(new BigDecimal("1000.00"))
                    .build();
            
            DriverDto oldestValidDriver = DriverDto.builder()
                    .firstName("A")
                    .lastName("B")
                    .dateOfBirth(LocalDate.of(1940, 1, 1))
                    .licenseNumber("M1")
                    .licenseState("AL")
                    .yearsOfExperience(0)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(false)
                    .build();
            
            return QuoteRequestDto.builder()
                    .vehicle(oldestValidVehicle)
                    .drivers(Arrays.asList(oldestValidDriver))
                    .coverageAmount(new BigDecimal("25000.00"))
                    .deductible(new BigDecimal("250.00"))
                    .build();
        }
        
        /**
         * Maximum valid values
         */
        public static QuoteRequestDto getMaximumValidQuote() {
            VehicleDto newestVehicle = VehicleDto.builder()
                    .make("Bugatti")
                    .model("Chiron")
                    .year(LocalDate.now().getYear() + 1) // Next year's model
                    .vin("MAX9876543210987")
                    .currentValue(new BigDecimal("3000000.00"))
                    .build();
            
            // Create 5 drivers (assuming max allowed)
            List<DriverDto> maxDrivers = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                maxDrivers.add(DriverDto.builder()
                        .firstName("Driver" + i)
                        .lastName("Test" + i)
                        .dateOfBirth(LocalDate.of(1980 - i, 1, 1))
                        .licenseNumber("MAX" + i + "123456789")
                        .licenseState("CA")
                        .yearsOfExperience(20 + i)
                        .safeDriverDiscount(true)
                        .multiPolicyDiscount(true)
                        .build());
            }
            
            return QuoteRequestDto.builder()
                    .vehicle(newestVehicle)
                    .drivers(maxDrivers)
                    .coverageAmount(new BigDecimal("1000000.00"))
                    .deductible(new BigDecimal("10000.00"))
                    .build();
        }
        
        /**
         * Quote with all discounts applied
         */
        public static QuoteRequestDto getAllDiscountsQuote() {
            VehicleDto safetyVehicle = VehicleDto.builder()
                    .make("Volvo")
                    .model("XC90")
                    .year(2023)
                    .vin("SAFE123456789012")
                    .currentValue(new BigDecimal("65000.00"))
                    .build();
            
            List<DriverDto> discountDrivers = Arrays.asList(
                    DriverDto.builder()
                            .firstName("Perfect")
                            .lastName("Driver")
                            .dateOfBirth(LocalDate.of(1975, 6, 15))
                            .licenseNumber("DISC123456789")
                            .licenseState("MA")
                            .yearsOfExperience(25)
                            .safeDriverDiscount(true)
                            .multiPolicyDiscount(true)
                            .build()
            );
            
            return QuoteRequestDto.builder()
                    .vehicle(safetyVehicle)
                    .drivers(discountDrivers)
                    .coverageAmount(new BigDecimal("300000.00"))
                    .deductible(new BigDecimal("1000.00"))
                    .build();
        }
    }
    
    /**
     * Generate a list of diverse quote requests for comprehensive testing
     */
    public static List<QuoteRequestDto> getAllScenarios() {
        List<QuoteRequestDto> scenarios = new ArrayList<>();
        
        // Add standard scenarios
        scenarios.add(StandardScenarios.getMinimumCoverageQuote());
        scenarios.add(StandardScenarios.getStandardCoverageQuote());
        scenarios.add(StandardScenarios.getPremiumCoverageQuote());
        
        // Add family scenarios
        scenarios.add(FamilyScenarios.getSingleParentWithTeen());
        scenarios.add(FamilyScenarios.getTwoParentFamily());
        
        // Add risk scenarios
        scenarios.add(HighRiskScenarios.getYoungDriverSportsCar());
        scenarios.add(LowRiskScenarios.getSeniorDriverEconomyCar());
        
        // Add geographic scenarios
        scenarios.add(GeographicScenarios.getUrbanHighTrafficQuote());
        scenarios.add(GeographicScenarios.getRuralLowMileageQuote());
        
        return scenarios;
    }
}