package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.VehicleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("RiskCalculationServiceImpl Tests")
class RiskCalculationServiceImplTest {

    private RiskCalculationServiceImpl riskCalculationService;
    private BigDecimal basePremium = new BigDecimal("500.00");

    @BeforeEach
    void setUp() {
        riskCalculationService = new RiskCalculationServiceImpl();
        ReflectionTestUtils.setField(riskCalculationService, "basePremium", basePremium);
    }

    @Nested
    @DisplayName("Base Premium Calculation Tests")
    class BasePremiumCalculationTests {

        @Test
        @DisplayName("Should calculate base premium with standard coverage")
        void should_CalculateBasePremium_When_StandardCoverageProvided() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Toyota")
                    .model("Camry")
                    .year(2020)
                    .vin("1HGBH41JXMN109186")
                    .currentValue(new BigDecimal("25000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .licenseNumber("D123456789")
                    .licenseState("CA")
                    .yearsOfExperience(10)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            BigDecimal premium = riskCalculationService.calculateBasePremium(request);

            // Then
            assertThat(premium).isNotNull();
            assertThat(premium).isGreaterThan(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should apply coverage amount factor correctly")
        void should_ApplyCoverageAmountFactor_When_HigherCoverageRequested() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Honda")
                    .model("Accord")
                    .year(2023)
                    .vin("1HGBH41JXMN109187")
                    .currentValue(new BigDecimal("30000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .dateOfBirth(LocalDate.of(1985, 5, 15))
                    .licenseNumber("S987654321")
                    .licenseState("NY")
                    .yearsOfExperience(15)
                    .build();

            QuoteRequestDto request1 = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            QuoteRequestDto request2 = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("200000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            BigDecimal premium1 = riskCalculationService.calculateBasePremium(request1);
            BigDecimal premium2 = riskCalculationService.calculateBasePremium(request2);

            // Then
            assertThat(premium2).isGreaterThan(premium1);
            assertThat(premium2.divide(premium1, 2, java.math.RoundingMode.HALF_UP))
                    .isEqualTo(new BigDecimal("2.00"));
        }

        @Test
        @DisplayName("Should apply deductible factor correctly")
        void should_ApplyDeductibleFactor_When_LowerDeductibleSelected() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Ford")
                    .model("F-150")
                    .year(2022)
                    .vin("1HGBH41JXMN109188")
                    .currentValue(new BigDecimal("35000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Bob")
                    .lastName("Johnson")
                    .dateOfBirth(LocalDate.of(1975, 3, 20))
                    .licenseNumber("J123456789")
                    .licenseState("TX")
                    .yearsOfExperience(20)
                    .build();

            QuoteRequestDto highDeductible = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("2000"))
                    .build();

            QuoteRequestDto lowDeductible = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("500"))
                    .build();

            // When
            BigDecimal highDeductiblePremium = riskCalculationService.calculateBasePremium(highDeductible);
            BigDecimal lowDeductiblePremium = riskCalculationService.calculateBasePremium(lowDeductible);

            // Then
            assertThat(lowDeductiblePremium).isGreaterThan(highDeductiblePremium);
        }
    }

    @Nested
    @DisplayName("Vehicle Age Factor Tests")
    class VehicleAgeFactorTests {

        @Test
        @DisplayName("Should apply vehicle age factor for older vehicles")
        void should_ApplyVehicleAgeFactor_When_VehicleIsOlder() {
            // Given
            DriverDto driver = DriverDto.builder()
                    .firstName("Alice")
                    .lastName("Williams")
                    .dateOfBirth(LocalDate.of(1980, 6, 10))
                    .licenseNumber("W987654321")
                    .licenseState("FL")
                    .yearsOfExperience(18)
                    .build();

            VehicleDto newVehicle = VehicleDto.builder()
                    .make("Tesla")
                    .model("Model 3")
                    .year(LocalDate.now().getYear())
                    .vin("1HGBH41JXMN109189")
                    .currentValue(new BigDecimal("40000"))
                    .build();

            VehicleDto oldVehicle = VehicleDto.builder()
                    .make("Tesla")
                    .model("Model 3")
                    .year(LocalDate.now().getYear() - 10)
                    .vin("1HGBH41JXMN109190")
                    .currentValue(new BigDecimal("20000"))
                    .build();

            QuoteRequestDto newVehicleRequest = QuoteRequestDto.builder()
                    .vehicle(newVehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            QuoteRequestDto oldVehicleRequest = QuoteRequestDto.builder()
                    .vehicle(oldVehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            BigDecimal newVehiclePremium = riskCalculationService.calculateBasePremium(newVehicleRequest);
            BigDecimal oldVehiclePremium = riskCalculationService.calculateBasePremium(oldVehicleRequest);

            // Then
            assertThat(oldVehiclePremium).isGreaterThan(newVehiclePremium);
        }
    }

    @Nested
    @DisplayName("Driver Risk Factor Tests")
    class DriverRiskFactorTests {

        @Test
        @DisplayName("Should apply higher risk factor for young drivers")
        void should_ApplyHigherRiskFactor_When_DriverIsYoung() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Chevrolet")
                    .model("Malibu")
                    .year(2021)
                    .vin("1HGBH41JXMN109191")
                    .currentValue(new BigDecimal("28000"))
                    .build();

            DriverDto youngDriver = DriverDto.builder()
                    .firstName("Tom")
                    .lastName("Young")
                    .dateOfBirth(LocalDate.now().minusYears(22))
                    .licenseNumber("Y123456789")
                    .licenseState("CA")
                    .yearsOfExperience(2)
                    .build();

            DriverDto middleAgedDriver = DriverDto.builder()
                    .firstName("Mike")
                    .lastName("Middle")
                    .dateOfBirth(LocalDate.now().minusYears(40))
                    .licenseNumber("M987654321")
                    .licenseState("CA")
                    .yearsOfExperience(20)
                    .build();

            QuoteRequestDto youngDriverRequest = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(youngDriver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            QuoteRequestDto middleAgedDriverRequest = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(middleAgedDriver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            BigDecimal youngDriverPremium = riskCalculationService.calculateBasePremium(youngDriverRequest);
            BigDecimal middleAgedDriverPremium = riskCalculationService.calculateBasePremium(middleAgedDriverRequest);

            // Then
            assertThat(youngDriverPremium).isGreaterThan(middleAgedDriverPremium);
        }

        @Test
        @DisplayName("Should apply slightly higher risk factor for senior drivers")
        void should_ApplySlightlyHigherRiskFactor_When_DriverIsSenior() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Nissan")
                    .model("Altima")
                    .year(2020)
                    .vin("1HGBH41JXMN109192")
                    .currentValue(new BigDecimal("26000"))
                    .build();

            DriverDto seniorDriver = DriverDto.builder()
                    .firstName("George")
                    .lastName("Senior")
                    .dateOfBirth(LocalDate.now().minusYears(70))
                    .licenseNumber("S111111111")
                    .licenseState("FL")
                    .yearsOfExperience(45)
                    .build();

            DriverDto middleAgedDriver = DriverDto.builder()
                    .firstName("Mark")
                    .lastName("Adult")
                    .dateOfBirth(LocalDate.now().minusYears(45))
                    .licenseNumber("A222222222")
                    .licenseState("FL")
                    .yearsOfExperience(25)
                    .build();

            QuoteRequestDto seniorDriverRequest = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(seniorDriver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            QuoteRequestDto middleAgedDriverRequest = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(middleAgedDriver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            BigDecimal seniorDriverPremium = riskCalculationService.calculateBasePremium(seniorDriverRequest);
            BigDecimal middleAgedDriverPremium = riskCalculationService.calculateBasePremium(middleAgedDriverRequest);

            // Then
            assertThat(seniorDriverPremium).isGreaterThan(middleAgedDriverPremium);
        }

        @Test
        @DisplayName("Should apply experience discount for experienced drivers")
        void should_ApplyExperienceDiscount_When_DriverIsExperienced() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Mazda")
                    .model("CX-5")
                    .year(2022)
                    .vin("1HGBH41JXMN109193")
                    .currentValue(new BigDecimal("32000"))
                    .build();

            DriverDto inexperiencedDriver = DriverDto.builder()
                    .firstName("Rookie")
                    .lastName("Driver")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .licenseNumber("R333333333")
                    .licenseState("NV")
                    .yearsOfExperience(3)
                    .build();

            DriverDto experiencedDriver = DriverDto.builder()
                    .firstName("Expert")
                    .lastName("Driver")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .licenseNumber("E444444444")
                    .licenseState("NV")
                    .yearsOfExperience(10)
                    .build();

            QuoteRequestDto inexperiencedRequest = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(inexperiencedDriver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            QuoteRequestDto experiencedRequest = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(experiencedDriver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            BigDecimal inexperiencedPremium = riskCalculationService.calculateBasePremium(inexperiencedRequest);
            BigDecimal experiencedPremium = riskCalculationService.calculateBasePremium(experiencedRequest);

            // Then
            assertThat(experiencedPremium).isLessThan(inexperiencedPremium);
        }

        @Test
        @DisplayName("Should handle null years of experience")
        void should_HandleNullExperience_When_ExperienceNotProvided() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Hyundai")
                    .model("Sonata")
                    .year(2021)
                    .vin("1HGBH41JXMN109194")
                    .currentValue(new BigDecimal("27000"))
                    .build();

            DriverDto driverWithNullExperience = DriverDto.builder()
                    .firstName("Unknown")
                    .lastName("Experience")
                    .dateOfBirth(LocalDate.of(1985, 7, 15))
                    .licenseNumber("U555555555")
                    .licenseState("AZ")
                    .yearsOfExperience(null)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driverWithNullExperience))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When & Then - should not throw exception
            BigDecimal premium = riskCalculationService.calculateBasePremium(request);
            assertThat(premium).isNotNull();
            assertThat(premium).isGreaterThan(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Multiple Drivers Tests")
    class MultipleDriversTests {

        @Test
        @DisplayName("Should calculate premium for multiple drivers")
        void should_CalculatePremium_When_MultipleDriversProvided() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Subaru")
                    .model("Outback")
                    .year(2021)
                    .vin("1HGBH41JXMN109195")
                    .currentValue(new BigDecimal("33000"))
                    .build();

            DriverDto primaryDriver = DriverDto.builder()
                    .firstName("Primary")
                    .lastName("Driver")
                    .dateOfBirth(LocalDate.of(1980, 1, 1))
                    .licenseNumber("P666666666")
                    .licenseState("CO")
                    .yearsOfExperience(20)
                    .build();

            DriverDto secondaryDriver = DriverDto.builder()
                    .firstName("Secondary")
                    .lastName("Driver")
                    .dateOfBirth(LocalDate.now().minusYears(23))  // Young driver - will increase premium
                    .licenseNumber("S777777777")
                    .licenseState("CO")
                    .yearsOfExperience(3)
                    .build();

            QuoteRequestDto singleDriverRequest = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(primaryDriver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            QuoteRequestDto multiDriverRequest = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(Arrays.asList(primaryDriver, secondaryDriver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            BigDecimal singleDriverPremium = riskCalculationService.calculateBasePremium(singleDriverRequest);
            BigDecimal multiDriverPremium = riskCalculationService.calculateBasePremium(multiDriverRequest);

            // Then
            assertThat(multiDriverPremium).isGreaterThan(singleDriverPremium);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Precision Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very high coverage amounts")
        void should_HandleHighCoverage_When_VeryHighCoverageRequested() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("BMW")
                    .model("X5")
                    .year(2023)
                    .vin("1HGBH41JXMN109196")
                    .currentValue(new BigDecimal("75000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Rich")
                    .lastName("Client")
                    .dateOfBirth(LocalDate.of(1970, 1, 1))
                    .licenseNumber("R888888888")
                    .licenseState("CA")
                    .yearsOfExperience(30)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("1000000"))
                    .deductible(new BigDecimal("5000"))
                    .build();

            // When
            BigDecimal premium = riskCalculationService.calculateBasePremium(request);

            // Then
            assertThat(premium).isNotNull();
            assertThat(premium.scale()).isEqualTo(2);
            assertThat(premium).isGreaterThan(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should maintain precision with rounding")
        void should_MaintainPrecision_When_CalculatingPremium() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Audi")
                    .model("A4")
                    .year(2022)
                    .vin("1HGBH41JXMN109197")
                    .currentValue(new BigDecimal("45000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Precise")
                    .lastName("Customer")
                    .dateOfBirth(LocalDate.of(1982, 3, 15))
                    .licenseNumber("P999999999")
                    .licenseState("NY")
                    .yearsOfExperience(17)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("123456"))
                    .deductible(new BigDecimal("789"))
                    .build();

            // When
            BigDecimal premium = riskCalculationService.calculateBasePremium(request);

            // Then
            assertThat(premium).isNotNull();
            assertThat(premium.scale()).isEqualTo(2); // Should always have 2 decimal places
        }
    }
}