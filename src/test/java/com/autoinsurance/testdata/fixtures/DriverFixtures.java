package com.autoinsurance.testdata.fixtures;

import com.autoinsurance.quote.dto.DriverDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Comprehensive driver test data fixtures for various testing scenarios.
 * Provides realistic driver profiles for different age groups, risk levels, and locations.
 */
public class DriverFixtures {
    
    // Age-based driver profiles
    public static class AgeProfiles {
        
        /**
         * Teen drivers (16-19 years) - highest risk category
         */
        public static List<DriverDto> getTeenDrivers() {
            List<DriverDto> drivers = new ArrayList<>();
            
            drivers.add(DriverDto.builder()
                    .firstName("Tyler")
                    .lastName("Anderson")
                    .dateOfBirth(LocalDate.of(2007, 4, 15))
                    .licenseNumber("T123456789")
                    .licenseState("CA")
                    .yearsOfExperience(0)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(false)
                    .build());
            
            drivers.add(DriverDto.builder()
                    .firstName("Madison")
                    .lastName("Taylor")
                    .dateOfBirth(LocalDate.of(2006, 11, 22))
                    .licenseNumber("T987654321")
                    .licenseState("TX")
                    .yearsOfExperience(1)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(false)
                    .build());
            
            drivers.add(DriverDto.builder()
                    .firstName("Jordan")
                    .lastName("Martinez")
                    .dateOfBirth(LocalDate.of(2005, 8, 3))
                    .licenseNumber("T555666777")
                    .licenseState("FL")
                    .yearsOfExperience(2)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(true)
                    .build());
            
            return drivers;
        }
        
        /**
         * Young adult drivers (20-25 years) - high risk category
         */
        public static List<DriverDto> getYoungAdultDrivers() {
            List<DriverDto> drivers = new ArrayList<>();
            
            drivers.add(DriverDto.builder()
                    .firstName("Alex")
                    .lastName("Johnson")
                    .dateOfBirth(LocalDate.of(2000, 5, 10))
                    .licenseNumber("Y111222333")
                    .licenseState("NY")
                    .yearsOfExperience(3)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(false)
                    .build());
            
            drivers.add(DriverDto.builder()
                    .firstName("Sophia")
                    .lastName("Williams")
                    .dateOfBirth(LocalDate.of(1999, 2, 28))
                    .licenseNumber("Y444555666")
                    .licenseState("IL")
                    .yearsOfExperience(5)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(false)
                    .build());
            
            return drivers;
        }
        
        /**
         * Middle-aged drivers (35-55 years) - lowest risk category
         */
        public static List<DriverDto> getMiddleAgedDrivers() {
            List<DriverDto> drivers = new ArrayList<>();
            
            drivers.add(DriverDto.builder()
                    .firstName("Michael")
                    .lastName("Chen")
                    .dateOfBirth(LocalDate.of(1975, 9, 15))
                    .licenseNumber("M777888999")
                    .licenseState("CA")
                    .yearsOfExperience(25)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build());
            
            drivers.add(DriverDto.builder()
                    .firstName("Jennifer")
                    .lastName("Garcia")
                    .dateOfBirth(LocalDate.of(1980, 6, 20))
                    .licenseNumber("M222333444")
                    .licenseState("WA")
                    .yearsOfExperience(20)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build());
            
            return drivers;
        }
        
        /**
         * Senior drivers (65+ years) - moderate risk category
         */
        public static List<DriverDto> getSeniorDrivers() {
            List<DriverDto> drivers = new ArrayList<>();
            
            drivers.add(DriverDto.builder()
                    .firstName("William")
                    .lastName("Thompson")
                    .dateOfBirth(LocalDate.of(1955, 3, 10))
                    .licenseNumber("S888999000")
                    .licenseState("FL")
                    .yearsOfExperience(48)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build());
            
            drivers.add(DriverDto.builder()
                    .firstName("Dorothy")
                    .lastName("Brown")
                    .dateOfBirth(LocalDate.of(1950, 12, 5))
                    .licenseNumber("S111222333")
                    .licenseState("AZ")
                    .yearsOfExperience(53)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(false)
                    .build());
            
            return drivers;
        }
    }
    
    // Risk-based driver profiles
    public static class RiskProfiles {
        
        /**
         * High-risk drivers with violations or claims
         */
        public static List<DriverDto> getHighRiskDrivers() {
            List<DriverDto> drivers = new ArrayList<>();
            
            // DUI history
            drivers.add(DriverDto.builder()
                    .firstName("Jake")
                    .lastName("Miller")
                    .dateOfBirth(LocalDate.of(1990, 7, 15))
                    .licenseNumber("HR111222333")
                    .licenseState("NV")
                    .yearsOfExperience(8)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(false)
                    .build());
            
            // Multiple accidents
            drivers.add(DriverDto.builder()
                    .firstName("Ashley")
                    .lastName("Davis")
                    .dateOfBirth(LocalDate.of(1985, 4, 22))
                    .licenseNumber("HR444555666")
                    .licenseState("MI")
                    .yearsOfExperience(15)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(false)
                    .build());
            
            return drivers;
        }
        
        /**
         * Low-risk drivers with clean records
         */
        public static List<DriverDto> getLowRiskDrivers() {
            List<DriverDto> drivers = new ArrayList<>();
            
            drivers.add(DriverDto.builder()
                    .firstName("David")
                    .lastName("Wilson")
                    .dateOfBirth(LocalDate.of(1978, 10, 30))
                    .licenseNumber("LR777888999")
                    .licenseState("MA")
                    .yearsOfExperience(22)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build());
            
            drivers.add(DriverDto.builder()
                    .firstName("Emma")
                    .lastName("Moore")
                    .dateOfBirth(LocalDate.of(1982, 1, 18))
                    .licenseNumber("LR123456789")
                    .licenseState("CT")
                    .yearsOfExperience(18)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build());
            
            return drivers;
        }
    }
    
    // Geographic-based driver profiles
    public static class GeographicProfiles {
        
        /**
         * Urban drivers - higher risk due to traffic density
         */
        public static List<DriverDto> getUrbanDrivers() {
            List<DriverDto> drivers = new ArrayList<>();
            
            // New York City
            drivers.add(DriverDto.builder()
                    .firstName("Anthony")
                    .lastName("Russo")
                    .dateOfBirth(LocalDate.of(1988, 5, 12))
                    .licenseNumber("NYC123456")
                    .licenseState("NY")
                    .yearsOfExperience(12)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(true)
                    .build());
            
            // Los Angeles
            drivers.add(DriverDto.builder()
                    .firstName("Maria")
                    .lastName("Gonzalez")
                    .dateOfBirth(LocalDate.of(1992, 8, 25))
                    .licenseNumber("LA789012")
                    .licenseState("CA")
                    .yearsOfExperience(8)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(false)
                    .build());
            
            // Chicago
            drivers.add(DriverDto.builder()
                    .firstName("Kevin")
                    .lastName("O'Brien")
                    .dateOfBirth(LocalDate.of(1979, 11, 7))
                    .licenseNumber("CHI345678")
                    .licenseState("IL")
                    .yearsOfExperience(20)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build());
            
            return drivers;
        }
        
        /**
         * Rural drivers - lower risk due to less traffic
         */
        public static List<DriverDto> getRuralDrivers() {
            List<DriverDto> drivers = new ArrayList<>();
            
            // Montana
            drivers.add(DriverDto.builder()
                    .firstName("Ranch")
                    .lastName("Walker")
                    .dateOfBirth(LocalDate.of(1975, 3, 15))
                    .licenseNumber("MT987654")
                    .licenseState("MT")
                    .yearsOfExperience(25)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build());
            
            // Wyoming
            drivers.add(DriverDto.builder()
                    .firstName("Grace")
                    .lastName("Harrison")
                    .dateOfBirth(LocalDate.of(1983, 6, 28))
                    .licenseNumber("WY123789")
                    .licenseState("WY")
                    .yearsOfExperience(17)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(false)
                    .build());
            
            return drivers;
        }
        
        /**
         * Suburban drivers - moderate risk
         */
        public static List<DriverDto> getSuburbanDrivers() {
            List<DriverDto> drivers = new ArrayList<>();
            
            drivers.add(DriverDto.builder()
                    .firstName("Christopher")
                    .lastName("Lee")
                    .dateOfBirth(LocalDate.of(1986, 9, 10))
                    .licenseNumber("SUB456789")
                    .licenseState("VA")
                    .yearsOfExperience(14)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build());
            
            drivers.add(DriverDto.builder()
                    .firstName("Linda")
                    .lastName("White")
                    .dateOfBirth(LocalDate.of(1977, 2, 14))
                    .licenseNumber("SUB987123")
                    .licenseState("MD")
                    .yearsOfExperience(23)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build());
            
            return drivers;
        }
    }
    
    // Special case driver profiles
    public static class SpecialCases {
        
        /**
         * Commercial drivers (CDL holders)
         */
        public static DriverDto getCommercialDriver() {
            return DriverDto.builder()
                    .firstName("Frank")
                    .lastName("Peterson")
                    .dateOfBirth(LocalDate.of(1970, 5, 20))
                    .licenseNumber("CDL123456789")
                    .licenseState("TX")
                    .yearsOfExperience(28)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build();
        }
        
        /**
         * International driver with foreign license
         */
        public static DriverDto getInternationalDriver() {
            return DriverDto.builder()
                    .firstName("Hiroshi")
                    .lastName("Tanaka")
                    .dateOfBirth(LocalDate.of(1985, 8, 15))
                    .licenseNumber("INT987654321")
                    .licenseState("CA")
                    .yearsOfExperience(10)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(false)
                    .build();
        }
        
        /**
         * Military/veteran driver with USAA eligibility
         */
        public static DriverDto getMilitaryDriver() {
            return DriverDto.builder()
                    .firstName("James")
                    .lastName("Roberts")
                    .dateOfBirth(LocalDate.of(1988, 11, 11))
                    .licenseNumber("MIL555777999")
                    .licenseState("VA")
                    .yearsOfExperience(12)
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build();
        }
        
        /**
         * Student driver with good grades discount eligibility
         */
        public static DriverDto getStudentDriver() {
            return DriverDto.builder()
                    .firstName("Jessica")
                    .lastName("Adams")
                    .dateOfBirth(LocalDate.of(2003, 9, 5))
                    .licenseNumber("STU111222333")
                    .licenseState("MA")
                    .yearsOfExperience(2)
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(true)
                    .build();
        }
    }
    
    /**
     * Generate a family of drivers (parents + children)
     */
    public static List<DriverDto> getFamilyDrivers() {
        List<DriverDto> family = new ArrayList<>();
        
        // Parent 1
        family.add(DriverDto.builder()
                .firstName("Robert")
                .lastName("Johnson")
                .dateOfBirth(LocalDate.of(1975, 6, 15))
                .licenseNumber("FAM111111111")
                .licenseState("OH")
                .yearsOfExperience(25)
                .safeDriverDiscount(true)
                .multiPolicyDiscount(true)
                .build());
        
        // Parent 2
        family.add(DriverDto.builder()
                .firstName("Susan")
                .lastName("Johnson")
                .dateOfBirth(LocalDate.of(1977, 3, 22))
                .licenseNumber("FAM222222222")
                .licenseState("OH")
                .yearsOfExperience(23)
                .safeDriverDiscount(true)
                .multiPolicyDiscount(true)
                .build());
        
        // Teen child
        family.add(DriverDto.builder()
                .firstName("Mark")
                .lastName("Johnson")
                .dateOfBirth(LocalDate.of(2006, 10, 8))
                .licenseNumber("FAM333333333")
                .licenseState("OH")
                .yearsOfExperience(1)
                .safeDriverDiscount(false)
                .multiPolicyDiscount(true)
                .build());
        
        return family;
    }
    
    /**
     * Generate random batch of drivers for load testing
     */
    public static List<DriverDto> generateRandomDriverBatch(int count) {
        List<DriverDto> drivers = new ArrayList<>();
        String[] firstNames = {"John", "Jane", "Michael", "Sarah", "David", "Lisa", "Robert", "Mary"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller"};
        String[] states = {"CA", "TX", "FL", "NY", "IL", "PA", "OH", "GA", "NC", "MI"};
        
        for (int i = 0; i < count; i++) {
            int birthYear = ThreadLocalRandom.current().nextInt(1950, 2006);
            int experience = Math.min(ThreadLocalRandom.current().nextInt(0, 50), 
                                     LocalDate.now().getYear() - birthYear - 16);
            
            drivers.add(DriverDto.builder()
                    .firstName(firstNames[ThreadLocalRandom.current().nextInt(firstNames.length)])
                    .lastName(lastNames[ThreadLocalRandom.current().nextInt(lastNames.length)])
                    .dateOfBirth(LocalDate.of(birthYear, 
                                             ThreadLocalRandom.current().nextInt(1, 13),
                                             ThreadLocalRandom.current().nextInt(1, 29)))
                    .licenseNumber("RND" + ThreadLocalRandom.current().nextLong(100000000L, 999999999L))
                    .licenseState(states[ThreadLocalRandom.current().nextInt(states.length)])
                    .yearsOfExperience(experience)
                    .safeDriverDiscount(ThreadLocalRandom.current().nextBoolean())
                    .multiPolicyDiscount(ThreadLocalRandom.current().nextBoolean())
                    .build());
        }
        
        return drivers;
    }
}