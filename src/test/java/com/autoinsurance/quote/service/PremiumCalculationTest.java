package com.autoinsurance.quote.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PremiumCalculation Tests")
class PremiumCalculationTest {

    @Nested
    @DisplayName("Constructor and Getters Tests")
    class ConstructorAndGettersTests {

        @Test
        @DisplayName("Should create PremiumCalculation with all fields")
        void should_CreatePremiumCalculation_When_AllFieldsProvided() {
            // Given
            BigDecimal basePremium = new BigDecimal("1000.00");
            BigDecimal totalDiscount = new BigDecimal("150.00");
            BigDecimal finalPremium = new BigDecimal("850.00");
            BigDecimal monthlyPremium = new BigDecimal("70.83");
            List<String> appliedDiscounts = Arrays.asList(
                    "Safe Driver Discount - 15%",
                    "Multi-Policy Discount - 10%"
            );

            // When
            PremiumCalculation premiumCalc = new PremiumCalculation(
                    basePremium,
                    totalDiscount,
                    finalPremium,
                    monthlyPremium,
                    appliedDiscounts
            );

            // Then
            assertThat(premiumCalc.getBasePremium()).isEqualTo(basePremium);
            assertThat(premiumCalc.getTotalDiscount()).isEqualTo(totalDiscount);
            assertThat(premiumCalc.getFinalPremium()).isEqualTo(finalPremium);
            assertThat(premiumCalc.getMonthlyPremium()).isEqualTo(monthlyPremium);
            assertThat(premiumCalc.getAppliedDiscounts()).containsExactly(
                    "Safe Driver Discount - 15%",
                    "Multi-Policy Discount - 10%"
            );
        }

        @Test
        @DisplayName("Should handle null discounts list")
        void should_HandleNullDiscounts_When_DiscountsListIsNull() {
            // Given
            BigDecimal basePremium = new BigDecimal("1200.00");
            BigDecimal totalDiscount = BigDecimal.ZERO;
            BigDecimal finalPremium = new BigDecimal("1200.00");
            BigDecimal monthlyPremium = new BigDecimal("100.00");

            // When
            PremiumCalculation premiumCalc = new PremiumCalculation(
                    basePremium,
                    totalDiscount,
                    finalPremium,
                    monthlyPremium,
                    null
            );

            // Then
            assertThat(premiumCalc.getAppliedDiscounts()).isNotNull();
            assertThat(premiumCalc.getAppliedDiscounts()).isEmpty();
        }

        @Test
        @DisplayName("Should handle empty discounts list")
        void should_HandleEmptyDiscounts_When_DiscountsListIsEmpty() {
            // Given
            BigDecimal basePremium = new BigDecimal("800.00");
            BigDecimal totalDiscount = BigDecimal.ZERO;
            BigDecimal finalPremium = new BigDecimal("800.00");
            BigDecimal monthlyPremium = new BigDecimal("66.67");
            List<String> appliedDiscounts = Collections.emptyList();

            // When
            PremiumCalculation premiumCalc = new PremiumCalculation(
                    basePremium,
                    totalDiscount,
                    finalPremium,
                    monthlyPremium,
                    appliedDiscounts
            );

            // Then
            assertThat(premiumCalc.getAppliedDiscounts()).isEmpty();
        }

        @Test
        @DisplayName("Should create immutable copy of discounts list")
        void should_CreateImmutableCopy_When_MutableListProvided() {
            // Given
            BigDecimal basePremium = new BigDecimal("900.00");
            BigDecimal totalDiscount = new BigDecimal("90.00");
            BigDecimal finalPremium = new BigDecimal("810.00");
            BigDecimal monthlyPremium = new BigDecimal("67.50");
            List<String> mutableDiscounts = new ArrayList<>();
            mutableDiscounts.add("Safe Driver Discount - 15%");

            // When
            PremiumCalculation premiumCalc = new PremiumCalculation(
                    basePremium,
                    totalDiscount,
                    finalPremium,
                    monthlyPremium,
                    mutableDiscounts
            );

            // Try to modify the original list
            mutableDiscounts.add("Another Discount");

            // Then - the internal list should not be affected
            assertThat(premiumCalc.getAppliedDiscounts()).hasSize(1);
            assertThat(premiumCalc.getAppliedDiscounts()).containsExactly("Safe Driver Discount - 15%");

            // Try to modify the returned list - should throw exception
            assertThatThrownBy(() -> premiumCalc.getAppliedDiscounts().add("New Discount"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Should handle zero values correctly")
        void should_HandleZeroValues_When_ZeroAmountsProvided() {
            // Given & When
            PremiumCalculation premiumCalc = new PremiumCalculation(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    Collections.emptyList()
            );

            // Then
            assertThat(premiumCalc.getBasePremium()).isEqualTo(BigDecimal.ZERO);
            assertThat(premiumCalc.getTotalDiscount()).isEqualTo(BigDecimal.ZERO);
            assertThat(premiumCalc.getFinalPremium()).isEqualTo(BigDecimal.ZERO);
            assertThat(premiumCalc.getMonthlyPremium()).isEqualTo(BigDecimal.ZERO);
            assertThat(premiumCalc.getAppliedDiscounts()).isEmpty();
        }

        @Test
        @DisplayName("Should handle large decimal values")
        void should_HandleLargeValues_When_LargeAmountsProvided() {
            // Given
            BigDecimal basePremium = new BigDecimal("999999.99");
            BigDecimal totalDiscount = new BigDecimal("100000.00");
            BigDecimal finalPremium = new BigDecimal("899999.99");
            BigDecimal monthlyPremium = new BigDecimal("75000.00");
            List<String> appliedDiscounts = Arrays.asList(
                    "Discount 1",
                    "Discount 2",
                    "Discount 3"
            );

            // When
            PremiumCalculation premiumCalc = new PremiumCalculation(
                    basePremium,
                    totalDiscount,
                    finalPremium,
                    monthlyPremium,
                    appliedDiscounts
            );

            // Then
            assertThat(premiumCalc.getBasePremium()).isEqualTo(new BigDecimal("999999.99"));
            assertThat(premiumCalc.getTotalDiscount()).isEqualTo(new BigDecimal("100000.00"));
            assertThat(premiumCalc.getFinalPremium()).isEqualTo(new BigDecimal("899999.99"));
            assertThat(premiumCalc.getMonthlyPremium()).isEqualTo(new BigDecimal("75000.00"));
            assertThat(premiumCalc.getAppliedDiscounts()).hasSize(3);
        }

        @Test
        @DisplayName("Should handle negative values")
        void should_HandleNegativeValues_When_NegativeAmountsProvided() {
            // Given - this shouldn't happen in real scenarios but testing edge case
            BigDecimal basePremium = new BigDecimal("1000.00");
            BigDecimal totalDiscount = new BigDecimal("-100.00");
            BigDecimal finalPremium = new BigDecimal("1100.00");
            BigDecimal monthlyPremium = new BigDecimal("91.67");
            List<String> appliedDiscounts = Arrays.asList("Penalty - 10%");

            // When
            PremiumCalculation premiumCalc = new PremiumCalculation(
                    basePremium,
                    totalDiscount,
                    finalPremium,
                    monthlyPremium,
                    appliedDiscounts
            );

            // Then
            assertThat(premiumCalc.getTotalDiscount()).isEqualTo(new BigDecimal("-100.00"));
            assertThat(premiumCalc.getFinalPremium()).isEqualTo(new BigDecimal("1100.00"));
        }
    }

    @Nested
    @DisplayName("ToString Method Tests")
    class ToStringMethodTests {

        @Test
        @DisplayName("Should return string representation with all fields")
        void should_ReturnStringRepresentation_When_ToStringCalled() {
            // Given
            BigDecimal basePremium = new BigDecimal("1500.00");
            BigDecimal totalDiscount = new BigDecimal("225.00");
            BigDecimal finalPremium = new BigDecimal("1275.00");
            BigDecimal monthlyPremium = new BigDecimal("106.25");
            List<String> appliedDiscounts = Arrays.asList(
                    "Safe Driver Discount - 15%"
            );

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    basePremium,
                    totalDiscount,
                    finalPremium,
                    monthlyPremium,
                    appliedDiscounts
            );

            // When
            String result = premiumCalc.toString();

            // Then
            assertThat(result).contains("PremiumCalculation");
            assertThat(result).contains("basePremium=1500.00");
            assertThat(result).contains("totalDiscount=225.00");
            assertThat(result).contains("finalPremium=1275.00");
            assertThat(result).contains("monthlyPremium=106.25");
            assertThat(result).contains("appliedDiscounts=[Safe Driver Discount - 15%]");
        }

        @Test
        @DisplayName("Should handle null discounts in toString")
        void should_HandleNullDiscountsInToString_When_DiscountsAreNull() {
            // Given
            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("100.00"),
                    BigDecimal.ZERO,
                    new BigDecimal("100.00"),
                    new BigDecimal("8.33"),
                    null
            );

            // When
            String result = premiumCalc.toString();

            // Then
            assertThat(result).contains("appliedDiscounts=[]");
            assertThat(result).doesNotContain("null");
        }

        @Test
        @DisplayName("Should handle empty discounts in toString")
        void should_HandleEmptyDiscountsInToString_When_DiscountsAreEmpty() {
            // Given
            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("200.00"),
                    BigDecimal.ZERO,
                    new BigDecimal("200.00"),
                    new BigDecimal("16.67"),
                    Collections.emptyList()
            );

            // When
            String result = premiumCalc.toString();

            // Then
            assertThat(result).contains("appliedDiscounts=[]");
        }

        @Test
        @DisplayName("Should handle multiple discounts in toString")
        void should_HandleMultipleDiscountsInToString_When_MultipleDiscountsProvided() {
            // Given
            List<String> discounts = Arrays.asList(
                    "Discount 1",
                    "Discount 2",
                    "Discount 3"
            );

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("500.00"),
                    new BigDecimal("125.00"),
                    new BigDecimal("375.00"),
                    new BigDecimal("31.25"),
                    discounts
            );

            // When
            String result = premiumCalc.toString();

            // Then
            assertThat(result).contains("appliedDiscounts=[Discount 1, Discount 2, Discount 3]");
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should maintain immutability of discounts list")
        void should_MaintainImmutability_When_ExternalModificationAttempted() {
            // Given
            List<String> originalDiscounts = new ArrayList<>();
            originalDiscounts.add("Initial Discount");

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("1000.00"),
                    new BigDecimal("100.00"),
                    new BigDecimal("900.00"),
                    new BigDecimal("75.00"),
                    originalDiscounts
            );

            // When - try to modify original list after creation
            originalDiscounts.add("Added After Creation");
            originalDiscounts.clear();

            // Then - internal list should remain unchanged
            assertThat(premiumCalc.getAppliedDiscounts()).hasSize(1);
            assertThat(premiumCalc.getAppliedDiscounts()).containsExactly("Initial Discount");
        }

        @Test
        @DisplayName("Should prevent modification of returned discounts list")
        void should_PreventModification_When_ReturnedListIsAccessed() {
            // Given
            PremiumCalculation premiumCalc = new PremiumCalculation(
                    new BigDecimal("2000.00"),
                    new BigDecimal("300.00"),
                    new BigDecimal("1700.00"),
                    new BigDecimal("141.67"),
                    Arrays.asList("Discount 1", "Discount 2")
            );

            // When & Then - attempt to modify should throw exception
            List<String> discounts = premiumCalc.getAppliedDiscounts();
            
            assertThatThrownBy(() -> discounts.add("New Discount"))
                    .isInstanceOf(UnsupportedOperationException.class);
            
            assertThatThrownBy(() -> discounts.remove(0))
                    .isInstanceOf(UnsupportedOperationException.class);
            
            assertThatThrownBy(() -> discounts.clear())
                    .isInstanceOf(UnsupportedOperationException.class);
            
            assertThatThrownBy(() -> discounts.set(0, "Modified"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Should preserve BigDecimal immutability")
        void should_PreserveBigDecimalImmutability_When_ValuesAreAccessed() {
            // Given
            BigDecimal basePremium = new BigDecimal("1000.00");
            BigDecimal totalDiscount = new BigDecimal("150.00");
            BigDecimal finalPremium = new BigDecimal("850.00");
            BigDecimal monthlyPremium = new BigDecimal("70.83");

            PremiumCalculation premiumCalc = new PremiumCalculation(
                    basePremium,
                    totalDiscount,
                    finalPremium,
                    monthlyPremium,
                    Collections.emptyList()
            );

            // When - BigDecimal is immutable by design, but verify references
            BigDecimal retrievedBase = premiumCalc.getBasePremium();
            BigDecimal retrievedDiscount = premiumCalc.getTotalDiscount();
            BigDecimal retrievedFinal = premiumCalc.getFinalPremium();
            BigDecimal retrievedMonthly = premiumCalc.getMonthlyPremium();

            // Then - should return the same references (BigDecimal is immutable)
            assertThat(retrievedBase).isSameAs(basePremium);
            assertThat(retrievedDiscount).isSameAs(totalDiscount);
            assertThat(retrievedFinal).isSameAs(finalPremium);
            assertThat(retrievedMonthly).isSameAs(monthlyPremium);
        }
    }
}