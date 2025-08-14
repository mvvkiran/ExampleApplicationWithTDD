package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.DriverDto;
import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.VehicleDto;
import com.autoinsurance.quote.entity.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("QuoteEntityBuilder Tests")
class QuoteEntityBuilderTest {

    private QuoteEntityBuilder quoteEntityBuilder;

    @BeforeEach
    void setUp() {
        quoteEntityBuilder = new QuoteEntityBuilder();
    }

    @Nested
    @DisplayName("Build Quote Entity Tests")
    class BuildQuoteEntityTests {

        @Test
        @DisplayName("Should build Quote entity with all fields populated")
        void should_BuildQuoteEntity_When_AllFieldsProvided() {
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
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            List<String> appliedDiscounts = Arrays.asList(
                    "Safe Driver Discount - 15%",
                    "Multi-Policy Discount - 10%"
            );

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("1000.00"),
                    new BigDecimal("250.00"),
                    new BigDecimal("750.00"),
                    new BigDecimal("62.50"),
                    appliedDiscounts
            );

            // When
            Quote quote = quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);

            // Then
            assertThat(quote).isNotNull();
            assertThat(quote.getId()).isNotNull();
            assertThat(quote.getId()).matches("[a-f0-9\\-]{36}"); // UUID pattern
            assertThat(quote.getPremium()).isEqualTo(new BigDecimal("750.00"));
            assertThat(quote.getMonthlyPremium()).isEqualTo(new BigDecimal("62.50"));
            assertThat(quote.getCoverageAmount()).isEqualTo(new BigDecimal("100000"));
            assertThat(quote.getDeductible()).isEqualTo(new BigDecimal("1000"));
            assertThat(quote.getValidUntil()).isEqualTo(LocalDate.now().plusDays(30));
            assertThat(quote.getVehicleMake()).isEqualTo("Toyota");
            assertThat(quote.getVehicleModel()).isEqualTo("Camry");
            assertThat(quote.getVehicleYear()).isEqualTo(2020);
            assertThat(quote.getVehicleVin()).isEqualTo("1HGBH41JXMN109186");
            assertThat(quote.getVehicleCurrentValue()).isEqualTo(new BigDecimal("25000"));
            assertThat(quote.getPrimaryDriverName()).isEqualTo("John Doe");
            assertThat(quote.getPrimaryDriverLicense()).isEqualTo("D123456789");
            assertThat(quote.getDiscountsApplied()).containsExactly(
                    "Safe Driver Discount - 15%",
                    "Multi-Policy Discount - 10%"
            );
        }

        @Test
        @DisplayName("Should handle empty discounts list")
        void should_HandleEmptyDiscounts_When_NoDiscountsApplied() {
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
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("150000"))
                    .deductible(new BigDecimal("500"))
                    .build();

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("1200.00"),
                    BigDecimal.ZERO,
                    new BigDecimal("1200.00"),
                    new BigDecimal("100.00"),
                    Collections.emptyList()
            );

            // When
            Quote quote = quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);

            // Then
            assertThat(quote.getDiscountsApplied()).isEmpty();
            assertThat(quote.getPrimaryDriverName()).isEqualTo("Jane Smith");
        }

        @Test
        @DisplayName("Should handle null discounts list")
        void should_HandleNullDiscounts_When_DiscountsAreNull() {
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
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("200000"))
                    .deductible(new BigDecimal("2000"))
                    .build();

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("1500.00"),
                    BigDecimal.ZERO,
                    new BigDecimal("1500.00"),
                    new BigDecimal("125.00"),
                    null
            );

            // When
            Quote quote = quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);

            // Then
            assertThat(quote.getDiscountsApplied()).isEmpty();
            assertThat(quote.getPrimaryDriverName()).isEqualTo("Bob Johnson");
        }

        @Test
        @DisplayName("Should use first driver as primary driver")
        void should_UseFirstDriver_When_MultipleDriversProvided() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Tesla")
                    .model("Model 3")
                    .year(2023)
                    .vin("1HGBH41JXMN109189")
                    .currentValue(new BigDecimal("40000"))
                    .build();

            DriverDto primaryDriver = DriverDto.builder()
                    .firstName("Alice")
                    .lastName("Williams")
                    .dateOfBirth(LocalDate.of(1980, 6, 10))
                    .licenseNumber("W987654321")
                    .licenseState("FL")
                    .build();

            DriverDto secondaryDriver = DriverDto.builder()
                    .firstName("Charlie")
                    .lastName("Brown")
                    .dateOfBirth(LocalDate.of(1982, 8, 20))
                    .licenseNumber("B111111111")
                    .licenseState("FL")
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(Arrays.asList(primaryDriver, secondaryDriver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("1100.00"),
                    new BigDecimal("100.00"),
                    new BigDecimal("1000.00"),
                    new BigDecimal("83.33"),
                    List.of("Multi-Policy Discount - 10%")
            );

            // When
            Quote quote = quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);

            // Then
            assertThat(quote.getPrimaryDriverName()).isEqualTo("Alice Williams");
            assertThat(quote.getPrimaryDriverLicense()).isEqualTo("W987654321");
        }

        @Test
        @DisplayName("Should create immutable copy of discounts list")
        void should_CreateImmutableCopy_When_DiscountsProvided() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Chevrolet")
                    .model("Malibu")
                    .year(2021)
                    .vin("1HGBH41JXMN109190")
                    .currentValue(new BigDecimal("28000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("David")
                    .lastName("Miller")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .licenseNumber("M222222222")
                    .licenseState("CA")
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            List<String> mutableDiscounts = new ArrayList<>();
            mutableDiscounts.add("Safe Driver Discount - 15%");

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("900.00"),
                    new BigDecimal("135.00"),
                    new BigDecimal("765.00"),
                    new BigDecimal("63.75"),
                    mutableDiscounts
            );

            // When
            Quote quote = quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);
            
            // Try to modify the original list
            mutableDiscounts.add("Another Discount");

            // Then
            assertThat(quote.getDiscountsApplied()).hasSize(1);
            assertThat(quote.getDiscountsApplied()).containsExactly("Safe Driver Discount - 15%");
            
            // Try to modify the quote's discounts list - should throw exception
            assertThatThrownBy(() -> quote.getDiscountsApplied().add("New Discount"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Should set valid until date to 30 days from now")
        void should_SetValidUntilDate_When_BuildingQuote() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Nissan")
                    .model("Altima")
                    .year(2020)
                    .vin("1HGBH41JXMN109191")
                    .currentValue(new BigDecimal("26000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Emma")
                    .lastName("Davis")
                    .dateOfBirth(LocalDate.of(1988, 4, 25))
                    .licenseNumber("D333333333")
                    .licenseState("NV")
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("850.00"),
                    BigDecimal.ZERO,
                    new BigDecimal("850.00"),
                    new BigDecimal("70.83"),
                    Collections.emptyList()
            );

            LocalDate expectedValidUntil = LocalDate.now().plusDays(30);

            // When
            Quote quote = quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);

            // Then
            assertThat(quote.getValidUntil()).isEqualTo(expectedValidUntil);
        }

        @Test
        @DisplayName("Should generate unique UUID for each quote")
        void should_GenerateUniqueId_When_BuildingMultipleQuotes() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Mazda")
                    .model("CX-5")
                    .year(2022)
                    .vin("1HGBH41JXMN109192")
                    .currentValue(new BigDecimal("32000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Frank")
                    .lastName("Wilson")
                    .dateOfBirth(LocalDate.of(1982, 3, 15))
                    .licenseNumber("W444444444")
                    .licenseState("NY")
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("950.00"),
                    BigDecimal.ZERO,
                    new BigDecimal("950.00"),
                    new BigDecimal("79.17"),
                    Collections.emptyList()
            );

            // When
            Quote quote1 = quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);
            Quote quote2 = quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);
            Quote quote3 = quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);

            // Then
            assertThat(quote1.getId()).isNotEqualTo(quote2.getId());
            assertThat(quote2.getId()).isNotEqualTo(quote3.getId());
            assertThat(quote1.getId()).isNotEqualTo(quote3.getId());
            
            // All should be valid UUIDs
            assertThat(quote1.getId()).matches("[a-f0-9\\-]{36}");
            assertThat(quote2.getId()).matches("[a-f0-9\\-]{36}");
            assertThat(quote3.getId()).matches("[a-f0-9\\-]{36}");
        }

        @Test
        @DisplayName("Should handle driver names with special characters")
        void should_HandleSpecialCharacters_When_DriverNameHasSpecialChars() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Hyundai")
                    .model("Sonata")
                    .year(2021)
                    .vin("1HGBH41JXMN109193")
                    .currentValue(new BigDecimal("27000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Jean-Pierre")
                    .lastName("O'Connor-Smith")
                    .dateOfBirth(LocalDate.of(1985, 7, 15))
                    .licenseNumber("O555555555")
                    .licenseState("AZ")
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("880.00"),
                    BigDecimal.ZERO,
                    new BigDecimal("880.00"),
                    new BigDecimal("73.33"),
                    Collections.emptyList()
            );

            // When
            Quote quote = quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);

            // Then
            assertThat(quote.getPrimaryDriverName()).isEqualTo("Jean-Pierre O'Connor-Smith");
        }

        @Test
        @DisplayName("Should handle large decimal values correctly")
        void should_HandleLargeValues_When_LargeCoverageAndPremium() {
            // Given
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Lamborghini")
                    .model("Aventador")
                    .year(2023)
                    .vin("1HGBH41JXMN109194")
                    .currentValue(new BigDecimal("500000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Rich")
                    .lastName("Customer")
                    .dateOfBirth(LocalDate.of(1970, 1, 1))
                    .licenseNumber("R666666666")
                    .licenseState("CA")
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("1000000"))
                    .deductible(new BigDecimal("10000"))
                    .build();

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("25000.00"),
                    new BigDecimal("3750.00"),
                    new BigDecimal("21250.00"),
                    new BigDecimal("1770.83"),
                    Arrays.asList("Safe Driver Discount - 15%")
            );

            // When
            Quote quote = quoteEntityBuilder.buildQuoteEntity(request, premiumCalc);

            // Then
            assertThat(quote.getPremium()).isEqualTo(new BigDecimal("21250.00"));
            assertThat(quote.getMonthlyPremium()).isEqualTo(new BigDecimal("1770.83"));
            assertThat(quote.getCoverageAmount()).isEqualTo(new BigDecimal("1000000"));
            assertThat(quote.getDeductible()).isEqualTo(new BigDecimal("10000"));
            assertThat(quote.getVehicleCurrentValue()).isEqualTo(new BigDecimal("500000"));
        }

        @Test
        @DisplayName("Should handle null discounts list boundary condition - line 58 mutation")
        void should_HandleNullDiscountsListBoundary() {
            // Given - Test the exact boundary condition for null list (line 58 RemoveConditionalMutator_EQUAL_ELSE)
            VehicleDto vehicle = VehicleDto.builder()
                    .make("Boundary")
                    .model("Test")
                    .year(2023)
                    .vin("1NULLDISCOUNTS")
                    .currentValue(new BigDecimal("30000"))
                    .build();

            DriverDto driver = DriverDto.builder()
                    .firstName("Null")
                    .lastName("Discounts")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .licenseNumber("NULL123")
                    .licenseState("CA")
                    .build();

            QuoteRequestDto request = QuoteRequestDto.builder()
                    .vehicle(vehicle)
                    .drivers(List.of(driver))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .build();

            // Test with null discounts list
            PremiumCalculation premiumCalcWithNull = new PremiumCalculation(
                    new BigDecimal("1000.00"),
                    BigDecimal.ZERO,
                    new BigDecimal("1000.00"),
                    new BigDecimal("83.33"),
                    null // This tests the null condition
            );

            // Test with empty discounts list
            PremiumCalculation premiumCalcWithEmpty = new PremiumCalculation(
                    new BigDecimal("1000.00"),
                    BigDecimal.ZERO,
                    new BigDecimal("1000.00"),
                    new BigDecimal("83.33"),
                    Collections.emptyList() // This tests the empty condition
            );

            // When
            Quote quoteWithNull = quoteEntityBuilder.buildQuoteEntity(request, premiumCalcWithNull);
            Quote quoteWithEmpty = quoteEntityBuilder.buildQuoteEntity(request, premiumCalcWithEmpty);

            // Then - Both should result in empty immutable lists
            assertThat(quoteWithNull.getDiscountsApplied()).isNotNull();
            assertThat(quoteWithNull.getDiscountsApplied()).isEmpty();
            assertThat(quoteWithEmpty.getDiscountsApplied()).isNotNull();
            assertThat(quoteWithEmpty.getDiscountsApplied()).isEmpty();
            
            // Both should be immutable
            assertThatThrownBy(() -> quoteWithNull.getDiscountsApplied().add("test"))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> quoteWithEmpty.getDiscountsApplied().add("test"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}