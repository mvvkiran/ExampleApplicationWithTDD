package com.autoinsurance.quote.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Quote Entity Tests")
class QuoteTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create Quote with all-args constructor")
        void should_CreateQuote_When_AllArgsConstructorUsed() {
            // Given
            String id = "quote-123";
            BigDecimal premium = new BigDecimal("1000.00");
            BigDecimal monthlyPremium = new BigDecimal("83.33");
            BigDecimal coverageAmount = new BigDecimal("100000");
            BigDecimal deductible = new BigDecimal("1000");
            LocalDate validUntil = LocalDate.now().plusDays(30);
            LocalDateTime createdAt = LocalDateTime.now();
            String vehicleMake = "Toyota";
            String vehicleModel = "Camry";
            Integer vehicleYear = 2020;
            String vehicleVin = "1HGBH41JXMN109186";
            BigDecimal vehicleCurrentValue = new BigDecimal("25000");
            String primaryDriverName = "John Doe";
            String primaryDriverLicense = "D123456789";
            List<String> discountsApplied = Arrays.asList("Safe Driver Discount - 15%");

            // When
            Quote quote = new Quote(id, premium, monthlyPremium, coverageAmount, deductible,
                    validUntil, createdAt, vehicleMake, vehicleModel, vehicleYear, vehicleVin,
                    vehicleCurrentValue, primaryDriverName, primaryDriverLicense, discountsApplied);

            // Then
            assertThat(quote.getId()).isEqualTo(id);
            assertThat(quote.getPremium()).isEqualTo(premium);
            assertThat(quote.getMonthlyPremium()).isEqualTo(monthlyPremium);
            assertThat(quote.getCoverageAmount()).isEqualTo(coverageAmount);
            assertThat(quote.getDeductible()).isEqualTo(deductible);
            assertThat(quote.getValidUntil()).isEqualTo(validUntil);
            assertThat(quote.getCreatedAt()).isEqualTo(createdAt);
            assertThat(quote.getVehicleMake()).isEqualTo(vehicleMake);
            assertThat(quote.getVehicleModel()).isEqualTo(vehicleModel);
            assertThat(quote.getVehicleYear()).isEqualTo(vehicleYear);
            assertThat(quote.getVehicleVin()).isEqualTo(vehicleVin);
            assertThat(quote.getVehicleCurrentValue()).isEqualTo(vehicleCurrentValue);
            assertThat(quote.getPrimaryDriverName()).isEqualTo(primaryDriverName);
            assertThat(quote.getPrimaryDriverLicense()).isEqualTo(primaryDriverLicense);
            assertThat(quote.getDiscountsApplied()).containsExactly("Safe Driver Discount - 15%");
        }

        @Test
        @DisplayName("Should create Quote with no-args constructor")
        void should_CreateQuote_When_NoArgsConstructorUsed() {
            // When
            Quote quote = new Quote();

            // Then
            assertThat(quote).isNotNull();
            assertThat(quote.getId()).isNull();
            assertThat(quote.getPremium()).isNull();
            assertThat(quote.getCreatedAt()).isNull(); // Not set until @PrePersist is triggered
        }

        @Test
        @DisplayName("Should handle null discounts in constructor")
        void should_HandleNullDiscounts_When_ConstructorReceivesNull() {
            // When
            Quote quote = new Quote("id", new BigDecimal("1000"), new BigDecimal("83.33"),
                    new BigDecimal("100000"), new BigDecimal("1000"), LocalDate.now().plusDays(30),
                    LocalDateTime.now(), "Toyota", "Camry", 2020, "VIN123", new BigDecimal("25000"),
                    "John Doe", "LICENSE123", null);

            // Then
            assertThat(quote.getDiscountsApplied()).isNotNull();
            assertThat(quote.getDiscountsApplied()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all properties")
        void should_SetAndGet_When_AllPropertiesSet() {
            // Given
            Quote quote = new Quote();
            String id = "quote-456";
            BigDecimal premium = new BigDecimal("1500.00");
            BigDecimal monthlyPremium = new BigDecimal("125.00");
            BigDecimal coverageAmount = new BigDecimal("150000");
            BigDecimal deductible = new BigDecimal("500");
            LocalDate validUntil = LocalDate.now().plusDays(30);
            LocalDateTime createdAt = LocalDateTime.now();
            String vehicleMake = "Honda";
            String vehicleModel = "Accord";
            Integer vehicleYear = 2021;
            String vehicleVin = "1HGBH41JXMN109187";
            BigDecimal vehicleCurrentValue = new BigDecimal("30000");
            String primaryDriverName = "Jane Smith";
            String primaryDriverLicense = "S987654321";
            List<String> discountsApplied = Arrays.asList("Multi-Policy Discount - 10%");

            // When
            quote.setId(id);
            quote.setPremium(premium);
            quote.setMonthlyPremium(monthlyPremium);
            quote.setCoverageAmount(coverageAmount);
            quote.setDeductible(deductible);
            quote.setValidUntil(validUntil);
            quote.setCreatedAt(createdAt);
            quote.setVehicleMake(vehicleMake);
            quote.setVehicleModel(vehicleModel);
            quote.setVehicleYear(vehicleYear);
            quote.setVehicleVin(vehicleVin);
            quote.setVehicleCurrentValue(vehicleCurrentValue);
            quote.setPrimaryDriverName(primaryDriverName);
            quote.setPrimaryDriverLicense(primaryDriverLicense);
            quote.setDiscountsApplied(discountsApplied);

            // Then
            assertThat(quote.getId()).isEqualTo(id);
            assertThat(quote.getPremium()).isEqualTo(premium);
            assertThat(quote.getMonthlyPremium()).isEqualTo(monthlyPremium);
            assertThat(quote.getCoverageAmount()).isEqualTo(coverageAmount);
            assertThat(quote.getDeductible()).isEqualTo(deductible);
            assertThat(quote.getValidUntil()).isEqualTo(validUntil);
            assertThat(quote.getCreatedAt()).isEqualTo(createdAt);
            assertThat(quote.getVehicleMake()).isEqualTo(vehicleMake);
            assertThat(quote.getVehicleModel()).isEqualTo(vehicleModel);
            assertThat(quote.getVehicleYear()).isEqualTo(vehicleYear);
            assertThat(quote.getVehicleVin()).isEqualTo(vehicleVin);
            assertThat(quote.getVehicleCurrentValue()).isEqualTo(vehicleCurrentValue);
            assertThat(quote.getPrimaryDriverName()).isEqualTo(primaryDriverName);
            assertThat(quote.getPrimaryDriverLicense()).isEqualTo(primaryDriverLicense);
            assertThat(quote.getDiscountsApplied()).containsExactly("Multi-Policy Discount - 10%");
        }

        @Test
        @DisplayName("Should handle null discounts in setter")
        void should_HandleNullDiscounts_When_SetterReceivesNull() {
            // Given
            Quote quote = new Quote();

            // When
            quote.setDiscountsApplied(null);

            // Then
            assertThat(quote.getDiscountsApplied()).isNotNull();
            assertThat(quote.getDiscountsApplied()).isEmpty();
        }

        @Test
        @DisplayName("Should handle non-null discounts in setter")
        void should_HandleNonNullDiscounts_When_SetterReceivesList() {
            // Given
            Quote quote = new Quote();
            List<String> discounts = Arrays.asList("Discount 1", "Discount 2");

            // When
            quote.setDiscountsApplied(discounts);

            // Then
            assertThat(quote.getDiscountsApplied()).containsExactly("Discount 1", "Discount 2");
        }
    }

    @Nested
    @DisplayName("PrePersist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Should set createdAt when persisting")
        void should_SetCreatedAt_When_PrePersistCalled() throws Exception {
            // Given
            Quote quote = new Quote();
            quote.setCreatedAt(null);

            // When - Use reflection to access private method
            java.lang.reflect.Method prePersistMethod = Quote.class.getDeclaredMethod("prePersist");
            prePersistMethod.setAccessible(true);
            prePersistMethod.invoke(quote);

            // Then
            assertThat(quote.getCreatedAt()).isNotNull();
            assertThat(quote.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("Should not override existing createdAt")
        void should_NotOverrideCreatedAt_When_AlreadySet() throws Exception {
            // Given
            Quote quote = new Quote();
            LocalDateTime originalCreatedAt = LocalDateTime.of(2023, 1, 1, 10, 0);
            quote.setCreatedAt(originalCreatedAt);

            // When - Use reflection to access private method
            java.lang.reflect.Method prePersistMethod = Quote.class.getDeclaredMethod("prePersist");
            prePersistMethod.setAccessible(true);
            prePersistMethod.invoke(quote);

            // Then
            assertThat(quote.getCreatedAt()).isEqualTo(originalCreatedAt);
        }
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should build Quote with all fields")
        void should_BuildQuote_When_AllFieldsProvided() {
            // Given & When
            Quote quote = Quote.builder()
                    .id("quote-789")
                    .premium(new BigDecimal("2000.00"))
                    .monthlyPremium(new BigDecimal("166.67"))
                    .coverageAmount(new BigDecimal("200000"))
                    .deductible(new BigDecimal("2000"))
                    .validUntil(LocalDate.now().plusDays(30))
                    .createdAt(LocalDateTime.now())
                    .vehicleMake("Ford")
                    .vehicleModel("F-150")
                    .vehicleYear(2022)
                    .vehicleVin("1HGBH41JXMN109188")
                    .vehicleCurrentValue(new BigDecimal("35000"))
                    .primaryDriverName("Bob Johnson")
                    .primaryDriverLicense("J123456789")
                    .discountsApplied(Arrays.asList("Safe Driver Discount"))
                    .build();

            // Then
            assertThat(quote).isNotNull();
            assertThat(quote.getId()).isEqualTo("quote-789");
            assertThat(quote.getPremium()).isEqualTo(new BigDecimal("2000.00"));
            assertThat(quote.getMonthlyPremium()).isEqualTo(new BigDecimal("166.67"));
            assertThat(quote.getCoverageAmount()).isEqualTo(new BigDecimal("200000"));
            assertThat(quote.getDeductible()).isEqualTo(new BigDecimal("2000"));
            assertThat(quote.getVehicleMake()).isEqualTo("Ford");
            assertThat(quote.getVehicleModel()).isEqualTo("F-150");
            assertThat(quote.getVehicleYear()).isEqualTo(2022);
            assertThat(quote.getVehicleVin()).isEqualTo("1HGBH41JXMN109188");
            assertThat(quote.getVehicleCurrentValue()).isEqualTo(new BigDecimal("35000"));
            assertThat(quote.getPrimaryDriverName()).isEqualTo("Bob Johnson");
            assertThat(quote.getPrimaryDriverLicense()).isEqualTo("J123456789");
            assertThat(quote.getDiscountsApplied()).containsExactly("Safe Driver Discount");
        }

        @Test
        @DisplayName("Should build Quote with default values")
        void should_BuildQuote_When_MinimalFieldsProvided() {
            // Given & When
            Quote quote = Quote.builder()
                    .id("quote-minimal")
                    .premium(new BigDecimal("500.00"))
                    .build();

            // Then
            assertThat(quote).isNotNull();
            assertThat(quote.getId()).isEqualTo("quote-minimal");
            assertThat(quote.getPremium()).isEqualTo(new BigDecimal("500.00"));
            assertThat(quote.getDiscountsApplied()).isEmpty(); // Default empty list
        }

        @Test
        @DisplayName("Should handle null discounts in builder")
        void should_HandleNullDiscounts_When_BuilderReceivesNull() {
            // Given & When
            Quote quote = Quote.builder()
                    .id("quote-null-discounts")
                    .premium(new BigDecimal("750.00"))
                    .discountsApplied(null)
                    .build();

            // Then
            assertThat(quote.getDiscountsApplied()).isNotNull();
            assertThat(quote.getDiscountsApplied()).isEmpty();
        }

        @Test
        @DisplayName("Should create new builder instance")
        void should_CreateNewBuilder_When_BuilderMethodCalled() {
            // When
            Quote.QuoteBuilder builder1 = Quote.builder();
            Quote.QuoteBuilder builder2 = Quote.builder();

            // Then
            assertThat(builder1).isNotNull();
            assertThat(builder2).isNotNull();
            assertThat(builder1).isNotSameAs(builder2);
        }

        @Test
        @DisplayName("Should chain builder methods")
        void should_ChainMethods_When_BuilderUsed() {
            // Given
            Quote.QuoteBuilder builder = Quote.builder();

            // When
            Quote.QuoteBuilder result = builder
                    .id("chain-test")
                    .premium(new BigDecimal("1000"))
                    .monthlyPremium(new BigDecimal("83.33"))
                    .coverageAmount(new BigDecimal("100000"))
                    .deductible(new BigDecimal("1000"))
                    .validUntil(LocalDate.now())
                    .createdAt(LocalDateTime.now())
                    .vehicleMake("Tesla")
                    .vehicleModel("Model 3")
                    .vehicleYear(2023)
                    .vehicleVin("VIN123")
                    .vehicleCurrentValue(new BigDecimal("40000"))
                    .primaryDriverName("Test Driver")
                    .primaryDriverLicense("LICENSE123")
                    .discountsApplied(new ArrayList<>());

            // Then
            assertThat(result).isSameAs(builder);
            Quote quote = result.build();
            assertThat(quote.getId()).isEqualTo("chain-test");
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle all null values")
        void should_HandleAllNulls_When_AllFieldsNull() {
            // When
            Quote quote = new Quote(null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null);

            // Then
            assertThat(quote).isNotNull();
            assertThat(quote.getId()).isNull();
            assertThat(quote.getPremium()).isNull();
            assertThat(quote.getMonthlyPremium()).isNull();
            assertThat(quote.getCoverageAmount()).isNull();
            assertThat(quote.getDeductible()).isNull();
            assertThat(quote.getValidUntil()).isNull();
            assertThat(quote.getCreatedAt()).isNull();
            assertThat(quote.getVehicleMake()).isNull();
            assertThat(quote.getVehicleModel()).isNull();
            assertThat(quote.getVehicleYear()).isNull();
            assertThat(quote.getVehicleVin()).isNull();
            assertThat(quote.getVehicleCurrentValue()).isNull();
            assertThat(quote.getPrimaryDriverName()).isNull();
            assertThat(quote.getPrimaryDriverLicense()).isNull();
            assertThat(quote.getDiscountsApplied()).isEmpty(); // Converted to empty list
        }

        @Test
        @DisplayName("Should handle empty strings")
        void should_HandleEmptyStrings_When_EmptyStringsProvided() {
            // When
            Quote quote = Quote.builder()
                    .id("")
                    .vehicleMake("")
                    .vehicleModel("")
                    .vehicleVin("")
                    .primaryDriverName("")
                    .primaryDriverLicense("")
                    .build();

            // Then
            assertThat(quote.getId()).isEmpty();
            assertThat(quote.getVehicleMake()).isEmpty();
            assertThat(quote.getVehicleModel()).isEmpty();
            assertThat(quote.getVehicleVin()).isEmpty();
            assertThat(quote.getPrimaryDriverName()).isEmpty();
            assertThat(quote.getPrimaryDriverLicense()).isEmpty();
        }

        @Test
        @DisplayName("Should handle large BigDecimal values")
        void should_HandleLargeValues_When_LargeBigDecimalsProvided() {
            // Given
            BigDecimal largePremium = new BigDecimal("999999999.99");
            BigDecimal largeCoverage = new BigDecimal("9999999999.99");

            // When
            Quote quote = Quote.builder()
                    .premium(largePremium)
                    .coverageAmount(largeCoverage)
                    .build();

            // Then
            assertThat(quote.getPremium()).isEqualTo(largePremium);
            assertThat(quote.getCoverageAmount()).isEqualTo(largeCoverage);
        }

        @Test
        @DisplayName("Should handle zero values")
        void should_HandleZeroValues_When_ZeroValuesProvided() {
            // When
            Quote quote = Quote.builder()
                    .premium(BigDecimal.ZERO)
                    .monthlyPremium(BigDecimal.ZERO)
                    .coverageAmount(BigDecimal.ZERO)
                    .deductible(BigDecimal.ZERO)
                    .vehicleYear(0)
                    .vehicleCurrentValue(BigDecimal.ZERO)
                    .build();

            // Then
            assertThat(quote.getPremium()).isEqualTo(BigDecimal.ZERO);
            assertThat(quote.getMonthlyPremium()).isEqualTo(BigDecimal.ZERO);
            assertThat(quote.getCoverageAmount()).isEqualTo(BigDecimal.ZERO);
            assertThat(quote.getDeductible()).isEqualTo(BigDecimal.ZERO);
            assertThat(quote.getVehicleYear()).isEqualTo(0);
            assertThat(quote.getVehicleCurrentValue()).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should handle dates in past and future")
        void should_HandleDates_When_PastAndFutureDatesProvided() {
            // Given
            LocalDate futureDate = LocalDate.of(2030, 12, 31);
            LocalDateTime pastDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);

            // When
            Quote quote = Quote.builder()
                    .validUntil(futureDate)
                    .createdAt(pastDateTime)
                    .build();

            // Then
            assertThat(quote.getValidUntil()).isEqualTo(futureDate);
            assertThat(quote.getCreatedAt()).isEqualTo(pastDateTime);
        }
    }
}