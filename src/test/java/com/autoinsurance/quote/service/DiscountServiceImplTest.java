package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.VehicleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiscountServiceImpl Tests")
class DiscountServiceImplTest {

    @Mock
    private RiskCalculationService riskCalculationService;

    private DiscountServiceImpl discountService;

    @BeforeEach
    void setUp() {
        discountService = new DiscountServiceImpl(riskCalculationService);
    }

    @Nested
    @DisplayName("Total Discount Calculation Tests")
    class TotalDiscountCalculationTests {

        @Test
        @DisplayName("Should calculate no discount when no discount flags are set")
        void should_CalculateNoDiscount_When_NoDiscountFlagsSet() {
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
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(false)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                    .thenReturn(new BigDecimal("1000.00"));

            // When
            BigDecimal discount = discountService.calculateTotalDiscount(request);

            // Then
            assertThat(discount).isEqualTo(new BigDecimal("0.00"));
            verify(riskCalculationService).calculateBasePremium(request);
        }

        @Test
        @DisplayName("Should apply safe driver discount")
        void should_ApplySafeDriverDiscount_When_SafeDriverFlagIsTrue() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Honda")
                    .model("Accord")
                    .year(2021)
                    .vin("1HGBH41JXMN109187")
                    .currentValue(new BigDecimal("30000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .dateOfBirth(LocalDate.of(1985, 5, 15))
                    .licenseNumber("S987654321")
                    .licenseState("NY")
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(false)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                    .thenReturn(new BigDecimal("1000.00"));

            // When
            BigDecimal discount = discountService.calculateTotalDiscount(request);

            // Then
            assertThat(discount).isEqualTo(new BigDecimal("150.00")); // 15% of 1000
        }

        @Test
        @DisplayName("Should apply multi-policy discount")
        void should_ApplyMultiPolicyDiscount_When_MultiPolicyFlagIsTrue() {
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
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(true)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                    .thenReturn(new BigDecimal("1000.00"));

            // When
            BigDecimal discount = discountService.calculateTotalDiscount(request);

            // Then
            assertThat(discount).isEqualTo(new BigDecimal("100.00")); // 10% of 1000
        }

        @Test
        @DisplayName("Should apply both discounts when both flags are true")
        void should_ApplyBothDiscounts_When_BothFlagsAreTrue() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Tesla")
                    .model("Model 3")
                    .year(2023)
                    .vin("1HGBH41JXMN109189")
                    .currentValue(new BigDecimal("40000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Alice")
                    .lastName("Williams")
                    .dateOfBirth(LocalDate.of(1980, 6, 10))
                    .licenseNumber("W987654321")
                    .licenseState("FL")
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                    .thenReturn(new BigDecimal("1000.00"));

            // When
            BigDecimal discount = discountService.calculateTotalDiscount(request);

            // Then
            assertThat(discount).isEqualTo(new BigDecimal("250.00")); // 25% of 1000 (capped)
        }

        @Test
        @DisplayName("Should cap total discount at 25%")
        void should_CapDiscountAt25Percent_When_MultipleDiscountsExceed25() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("BMW")
                    .model("X5")
                    .year(2023)
                    .vin("1HGBH41JXMN109190")
                    .currentValue(new BigDecimal("75000"))
                    .build();

            DriverDto driver1 = DriverDto.builder()
                    .firstName("Driver")
                    .lastName("One")
                    .dateOfBirth(LocalDate.of(1970, 1, 1))
                    .licenseNumber("D111111111")
                    .licenseState("CA")
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build();

            DriverDto driver2 = DriverDto.builder()
                    .firstName("Driver")
                    .lastName("Two")
                    .dateOfBirth(LocalDate.of(1975, 1, 1))
                    .licenseNumber("D222222222")
                    .licenseState("CA")
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(false)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(Arrays.asList(driver1, driver2))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                    .thenReturn(new BigDecimal("2000.00"));

            // When
            BigDecimal discount = discountService.calculateTotalDiscount(request);

            // Then
            // Total would be 15% + 10% + 15% = 40%, but capped at 25%
            assertThat(discount).isEqualTo(new BigDecimal("500.00")); // 25% of 2000
        }

        @Test
        @DisplayName("Should handle null discount flags")
        void should_HandleNullDiscountFlags_When_FlagsAreNull() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Nissan")
                    .model("Altima")
                    .year(2020)
                    .vin("1HGBH41JXMN109191")
                    .currentValue(new BigDecimal("26000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Null")
                    .lastName("Flags")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .licenseNumber("N333333333")
                    .licenseState("NV")
                    .safeDriverDiscount(null)
                    .multiPolicyDiscount(null)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                    .thenReturn(new BigDecimal("1000.00"));

            // When
            BigDecimal discount = discountService.calculateTotalDiscount(request);

            // Then
            assertThat(discount).isEqualTo(new BigDecimal("0.00"));
        }

        @Test
        @DisplayName("Should maintain precision with rounding")
        void should_MaintainPrecision_When_CalculatingDiscount() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Mazda")
                    .model("CX-5")
                    .year(2022)
                    .vin("1HGBH41JXMN109192")
                    .currentValue(new BigDecimal("32000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Precise")
                    .lastName("Customer")
                    .dateOfBirth(LocalDate.of(1982, 3, 15))
                    .licenseNumber("P444444444")
                    .licenseState("NY")
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(false)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            when(riskCalculationService.calculateBasePremium(any(QuoteRequestDto.class)))
                    .thenReturn(new BigDecimal("999.99"));

            // When
            BigDecimal discount = discountService.calculateTotalDiscount(request);

            // Then
            assertThat(discount.scale()).isEqualTo(2);
            assertThat(discount).isEqualTo(new BigDecimal("150.00")); // 15% of 999.99 rounded
        }
    }

    @Nested
    @DisplayName("Applied Discounts List Tests")
    class AppliedDiscountsListTests {

        @Test
        @DisplayName("Should return empty list when no discounts applied")
        void should_ReturnEmptyList_When_NoDiscountsApplied() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Chevrolet")
                    .model("Malibu")
                    .year(2021)
                    .vin("1HGBH41JXMN109193")
                    .currentValue(new BigDecimal("28000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("No")
                    .lastName("Discount")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .licenseNumber("N555555555")
                    .licenseState("CA")
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(false)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            List<String> discounts = discountService.getAppliedDiscounts(request);

            // Then
            assertThat(discounts).isEmpty();
        }

        @Test
        @DisplayName("Should return safe driver discount description")
        void should_ReturnSafeDriverDescription_When_SafeDriverApplied() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Hyundai")
                    .model("Sonata")
                    .year(2021)
                    .vin("1HGBH41JXMN109194")
                    .currentValue(new BigDecimal("27000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Safe")
                    .lastName("Driver")
                    .dateOfBirth(LocalDate.of(1985, 7, 15))
                    .licenseNumber("S666666666")
                    .licenseState("AZ")
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(false)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            List<String> discounts = discountService.getAppliedDiscounts(request);

            // Then
            assertThat(discounts).hasSize(1);
            assertThat(discounts).contains("Safe Driver Discount - 15%");
        }

        @Test
        @DisplayName("Should return multi-policy discount description")
        void should_ReturnMultiPolicyDescription_When_MultiPolicyApplied() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Subaru")
                    .model("Outback")
                    .year(2021)
                    .vin("1HGBH41JXMN109195")
                    .currentValue(new BigDecimal("33000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Multi")
                    .lastName("Policy")
                    .dateOfBirth(LocalDate.of(1980, 1, 1))
                    .licenseNumber("M777777777")
                    .licenseState("CO")
                    .multiPolicyDiscount(true)
                    .safeDriverDiscount(false)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            List<String> discounts = discountService.getAppliedDiscounts(request);

            // Then
            assertThat(discounts).hasSize(1);
            assertThat(discounts).contains("Multi-Policy Discount - 10%");
        }

        @Test
        @DisplayName("Should return both discount descriptions when both applied")
        void should_ReturnBothDescriptions_When_BothDiscountsApplied() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Audi")
                    .model("A4")
                    .year(2022)
                    .vin("1HGBH41JXMN109196")
                    .currentValue(new BigDecimal("45000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Both")
                    .lastName("Discounts")
                    .dateOfBirth(LocalDate.of(1982, 3, 15))
                    .licenseNumber("B888888888")
                    .licenseState("NY")
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(true)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            List<String> discounts = discountService.getAppliedDiscounts(request);

            // Then
            assertThat(discounts).hasSize(2);
            assertThat(discounts).containsExactlyInAnyOrder(
                    "Safe Driver Discount - 15%",
                    "Multi-Policy Discount - 10%"
            );
        }

        @Test
        @DisplayName("Should handle multiple drivers with different discounts")
        void should_HandleMultipleDrivers_When_DifferentDiscountsApplied() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Mercedes")
                    .model("C-Class")
                    .year(2023)
                    .vin("1HGBH41JXMN109197")
                    .currentValue(new BigDecimal("50000"))
                    .build();

            DriverDto driver1 = DriverDto.builder()
                    .firstName("First")
                    .lastName("Driver")
                    .dateOfBirth(LocalDate.of(1970, 1, 1))
                    .licenseNumber("F999999999")
                    .licenseState("CA")
                    .safeDriverDiscount(true)
                    .multiPolicyDiscount(false)
                    .build();

            DriverDto driver2 = DriverDto.builder()
                    .firstName("Second")
                    .lastName("Driver")
                    .dateOfBirth(LocalDate.of(1975, 1, 1))
                    .licenseNumber("S000000000")
                    .licenseState("CA")
                    .safeDriverDiscount(false)
                    .multiPolicyDiscount(true)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(Arrays.asList(driver1, driver2))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            List<String> discounts = discountService.getAppliedDiscounts(request);

            // Then
            assertThat(discounts).hasSize(2);
            assertThat(discounts).containsExactlyInAnyOrder(
                    "Safe Driver Discount - 15%",
                    "Multi-Policy Discount - 10%"
            );
        }

        @Test
        @DisplayName("Should handle null discount flags in list")
        void should_HandleNullFlags_When_GettingAppliedDiscounts() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Volkswagen")
                    .model("Jetta")
                    .year(2020)
                    .vin("1HGBH41JXMN109198")
                    .currentValue(new BigDecimal("24000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Null")
                    .lastName("Flags")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .licenseNumber("N111111111")
                    .licenseState("NV")
                    .safeDriverDiscount(null)
                    .multiPolicyDiscount(null)
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // When
            List<String> discounts = discountService.getAppliedDiscounts(request);

            // Then
            assertThat(discounts).isEmpty();
        }
    }
}