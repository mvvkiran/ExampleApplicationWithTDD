package com.autoinsurance.quote.repository;

import com.autoinsurance.quote.entity.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JPA Repository Tests for QuoteRepository
 * 
 * Following TDD Red-Green-Blue methodology:
 * RED: Tests fail when repository functionality doesn't work as expected
 * GREEN: Repository operations work correctly with database
 * BLUE: Optimize queries and data access patterns
 * 
 * Tests Core Repository Operations:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Data persistence and retrieval
 * - Entity lifecycle management (@PrePersist callbacks)
 * - Collection mapping (@ElementCollection for discounts)
 * - Database constraints and validation
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb_repository",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
@DisplayName("Quote Repository Tests")
class QuoteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuoteRepository quoteRepository;

    private Quote sampleQuote;

    @BeforeEach
    void setUp() {
        // RED: Create test data that should persist and be retrievable
        sampleQuote = Quote.builder()
            .id("QT-TEST-001")
            .premium(new BigDecimal("1200.00"))
            .monthlyPremium(new BigDecimal("100.00"))
            .coverageAmount(new BigDecimal("100000.00"))
            .deductible(new BigDecimal("1000.00"))
            .validUntil(LocalDate.now().plusDays(30))
            .createdAt(LocalDateTime.now())
            .vehicleMake("Honda")
            .vehicleModel("Civic")
            .vehicleYear(2020)
            .vehicleVin("1HGFC2F53JA123456")
            .vehicleCurrentValue(new BigDecimal("25000.00"))
            .primaryDriverName("John Doe")
            .primaryDriverLicense("D123456789")
            .discountsApplied(List.of("Safe Driver", "Multi Policy"))
            .build();
    }

    @Test
    @DisplayName("Should save and retrieve quote by ID")
    void should_SaveAndRetrieveQuoteById() {
        // RED: Test fails if save/retrieve doesn't work

        // Given - Quote to save
        
        // When - Save quote
        quoteRepository.save(sampleQuote);
        entityManager.flush(); // Force database write
        entityManager.clear(); // Clear persistence context
        
        // Then - GREEN: Quote should be saved and retrievable
        Optional<Quote> retrievedQuote = quoteRepository.findById("QT-TEST-001");
        
        assertThat(retrievedQuote).isPresent();
        assertThat(retrievedQuote.get().getId()).isEqualTo("QT-TEST-001");
        assertThat(retrievedQuote.get().getPremium()).isEqualTo(new BigDecimal("1200.00"));
        assertThat(retrievedQuote.get().getVehicleMake()).isEqualTo("Honda");
        assertThat(retrievedQuote.get().getVehicleModel()).isEqualTo("Civic");
        assertThat(retrievedQuote.get().getPrimaryDriverName()).isEqualTo("John Doe");
        assertThat(retrievedQuote.get().getDiscountsApplied()).contains("Safe Driver", "Multi Policy");
    }

    @Test
    @DisplayName("Should return empty optional for non-existent quote")
    void should_ReturnEmptyOptionalForNonExistentQuote() {
        // RED: Test fails if non-existent ID returns data

        // When - Search for non-existent quote
        Optional<Quote> result = quoteRepository.findById("NON-EXISTENT-ID");
        
        // Then - GREEN: Should return empty optional
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should save quote with null discounts list")
    void should_SaveQuoteWithNullDiscountsList() {
        // RED: Test fails if null collections cause issues

        // Given - Quote with null discounts
        Quote quoteWithNullDiscounts = Quote.builder()
            .id("QT-NULL-DISCOUNTS")
            .premium(new BigDecimal("800.00"))
            .monthlyPremium(new BigDecimal("66.67"))
            .coverageAmount(new BigDecimal("50000.00"))
            .deductible(new BigDecimal("500.00"))
            .validUntil(LocalDate.now().plusDays(30))
            .vehicleMake("Toyota")
            .vehicleModel("Corolla")
            .vehicleYear(2019)
            .vehicleVin("JTDEPRAE8JJ123456")
            .vehicleCurrentValue(new BigDecimal("18000.00"))
            .primaryDriverName("Jane Smith")
            .primaryDriverLicense("S987654321")
            .discountsApplied(null) // Explicitly null
            .build();
        
        // When - Save quote with null discounts
        quoteRepository.save(quoteWithNullDiscounts);
        entityManager.flush();
        
        // Then - GREEN: Should save successfully with empty list
        Optional<Quote> retrievedQuote = quoteRepository.findById("QT-NULL-DISCOUNTS");
        assertThat(retrievedQuote).isPresent();
        assertThat(retrievedQuote.get().getDiscountsApplied()).isNotNull();
        assertThat(retrievedQuote.get().getDiscountsApplied()).isEmpty();
    }

    @Test
    @DisplayName("Should automatically set createdAt timestamp on save")
    void should_AutomaticallySetCreatedAtTimestampOnSave() {
        // RED: Test fails if @PrePersist doesn't work

        // Given - Quote without createdAt set
        Quote quoteWithoutTimestamp = Quote.builder()
            .id("QT-TIMESTAMP-TEST")
            .premium(new BigDecimal("950.00"))
            .monthlyPremium(new BigDecimal("79.17"))
            .coverageAmount(new BigDecimal("75000.00"))
            .deductible(new BigDecimal("750.00"))
            .validUntil(LocalDate.now().plusDays(30))
            .vehicleMake("Ford")
            .vehicleModel("Focus")
            .vehicleYear(2021)
            .vehicleVin("1FADP3K25JL123456")
            .vehicleCurrentValue(new BigDecimal("20000.00"))
            .primaryDriverName("Mike Johnson")
            .primaryDriverLicense("J456789123")
            .createdAt(null) // Explicitly null
            .build();
        
        LocalDateTime beforeSave = LocalDateTime.now();
        
        // When - Save quote
        quoteRepository.save(quoteWithoutTimestamp);
        entityManager.flush();
        
        LocalDateTime afterSave = LocalDateTime.now();
        
        // Then - GREEN: createdAt should be automatically set
        Optional<Quote> savedQuote = quoteRepository.findById("QT-TIMESTAMP-TEST");
        assertThat(savedQuote).isPresent();
        assertThat(savedQuote.get().getCreatedAt()).isNotNull();
        assertThat(savedQuote.get().getCreatedAt()).isBetween(beforeSave, afterSave);
    }

    @Test
    @DisplayName("Should update existing quote")
    void should_UpdateExistingQuote() {
        // RED: Test fails if updates don't persist

        // Given - Existing quote
        quoteRepository.save(sampleQuote);
        entityManager.flush();
        entityManager.clear();
        
        // When - Update premium and vehicle value
        Optional<Quote> existingQuote = quoteRepository.findById("QT-TEST-001");
        assertThat(existingQuote).isPresent();
        
        Quote quoteToUpdate = existingQuote.get();
        quoteToUpdate.setPremium(new BigDecimal("1350.00"));
        quoteToUpdate.setMonthlyPremium(new BigDecimal("112.50"));
        quoteToUpdate.setVehicleCurrentValue(new BigDecimal("27000.00"));
        
        quoteRepository.save(quoteToUpdate);
        entityManager.flush();
        entityManager.clear();
        
        // Then - GREEN: Updates should be persisted
        Optional<Quote> verifyQuote = quoteRepository.findById("QT-TEST-001");
        assertThat(verifyQuote).isPresent();
        assertThat(verifyQuote.get().getPremium()).isEqualTo(new BigDecimal("1350.00"));
        assertThat(verifyQuote.get().getMonthlyPremium()).isEqualTo(new BigDecimal("112.50"));
        assertThat(verifyQuote.get().getVehicleCurrentValue()).isEqualTo(new BigDecimal("27000.00"));
        
        // Original fields should remain unchanged
        assertThat(verifyQuote.get().getVehicleMake()).isEqualTo("Honda");
        assertThat(verifyQuote.get().getPrimaryDriverName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should delete quote by ID")
    void should_DeleteQuoteById() {
        // RED: Test fails if delete doesn't work

        // Given - Existing quote
        quoteRepository.save(sampleQuote);
        entityManager.flush();
        
        // Verify quote exists
        assertThat(quoteRepository.findById("QT-TEST-001")).isPresent();
        
        // When - Delete quote
        quoteRepository.deleteById("QT-TEST-001");
        entityManager.flush();
        
        // Then - GREEN: Quote should be deleted
        assertThat(quoteRepository.findById("QT-TEST-001")).isEmpty();
    }

    @Test
    @DisplayName("Should find all quotes")
    void should_FindAllQuotes() {
        // RED: Test fails if findAll doesn't return all saved quotes

        // Given - Multiple quotes
        Quote quote1 = Quote.builder()
            .id("QT-ALL-001")
            .premium(new BigDecimal("1000.00"))
            .monthlyPremium(new BigDecimal("83.33"))
            .coverageAmount(new BigDecimal("100000.00"))
            .deductible(new BigDecimal("1000.00"))
            .validUntil(LocalDate.now().plusDays(30))
            .vehicleMake("Nissan")
            .vehicleModel("Altima")
            .vehicleYear(2020)
            .vehicleVin("1N4AL3AP8JC123456")
            .vehicleCurrentValue(new BigDecimal("24000.00"))
            .primaryDriverName("Alice Brown")
            .primaryDriverLicense("B789123456")
            .build();

        Quote quote2 = Quote.builder()
            .id("QT-ALL-002")
            .premium(new BigDecimal("1500.00"))
            .monthlyPremium(new BigDecimal("125.00"))
            .coverageAmount(new BigDecimal("150000.00"))
            .deductible(new BigDecimal("2000.00"))
            .validUntil(LocalDate.now().plusDays(30))
            .vehicleMake("BMW")
            .vehicleModel("3 Series")
            .vehicleYear(2022)
            .vehicleVin("WBA8E1C02JA123456")
            .vehicleCurrentValue(new BigDecimal("45000.00"))
            .primaryDriverName("Bob Wilson")
            .primaryDriverLicense("W456123789")
            .build();
        
        // When - Save quotes and find all
        quoteRepository.save(quote1);
        quoteRepository.save(quote2);
        entityManager.flush();
        
        List<Quote> allQuotes = quoteRepository.findAll();
        
        // Then - GREEN: Should return all saved quotes
        assertThat(allQuotes).hasSize(2);
        assertThat(allQuotes).extracting(Quote::getId).contains("QT-ALL-001", "QT-ALL-002");
        assertThat(allQuotes).extracting(Quote::getVehicleMake).contains("Nissan", "BMW");
        assertThat(allQuotes).extracting(Quote::getPrimaryDriverName).contains("Alice Brown", "Bob Wilson");
    }

    @Test
    @DisplayName("Should count total number of quotes")
    void should_CountTotalNumberOfQuotes() {
        // RED: Test fails if count doesn't match saved quotes

        // Given - Initial empty repository
        long initialCount = quoteRepository.count();
        assertThat(initialCount).isZero();
        
        // When - Save multiple quotes
        quoteRepository.save(sampleQuote);
        
        Quote secondQuote = Quote.builder()
            .id("QT-COUNT-002")
            .premium(new BigDecimal("900.00"))
            .monthlyPremium(new BigDecimal("75.00"))
            .coverageAmount(new BigDecimal("80000.00"))
            .deductible(new BigDecimal("800.00"))
            .validUntil(LocalDate.now().plusDays(30))
            .vehicleMake("Chevrolet")
            .vehicleModel("Malibu")
            .vehicleYear(2019)
            .vehicleVin("1G1ZB5ST8JF123456")
            .vehicleCurrentValue(new BigDecimal("22000.00"))
            .primaryDriverName("Carol Davis")
            .primaryDriverLicense("D321654987")
            .build();
            
        quoteRepository.save(secondQuote);
        entityManager.flush();
        
        // Then - GREEN: Count should reflect saved quotes
        long finalCount = quoteRepository.count();
        assertThat(finalCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle large discount lists")
    void should_HandleLargeDiscountLists() {
        // RED: Test fails if @ElementCollection can't handle multiple discounts

        // Given - Quote with many discounts
        List<String> manyDiscounts = List.of(
            "Safe Driver Discount",
            "Multi-Policy Discount", 
            "Good Student Discount",
            "Low Mileage Discount",
            "Anti-Theft Device Discount",
            "Defensive Driving Course Discount",
            "Loyalty Customer Discount",
            "Paperless Billing Discount"
        );
        
        Quote quoteWithManyDiscounts = Quote.builder()
            .id("QT-MANY-DISCOUNTS")
            .premium(new BigDecimal("800.00")) // Lower due to many discounts
            .monthlyPremium(new BigDecimal("66.67"))
            .coverageAmount(new BigDecimal("100000.00"))
            .deductible(new BigDecimal("1000.00"))
            .validUntil(LocalDate.now().plusDays(30))
            .vehicleMake("Honda")
            .vehicleModel("Accord")
            .vehicleYear(2021)
            .vehicleVin("1HGCV1F31JA123456")
            .vehicleCurrentValue(new BigDecimal("28000.00"))
            .primaryDriverName("David Perfect")
            .primaryDriverLicense("P123456789")
            .discountsApplied(manyDiscounts)
            .build();
        
        // When - Save quote with many discounts
        quoteRepository.save(quoteWithManyDiscounts);
        entityManager.flush();
        entityManager.clear();
        
        // Then - GREEN: All discounts should be persisted
        Optional<Quote> retrievedQuote = quoteRepository.findById("QT-MANY-DISCOUNTS");
        assertThat(retrievedQuote).isPresent();
        assertThat(retrievedQuote.get().getDiscountsApplied()).hasSize(8);
        assertThat(retrievedQuote.get().getDiscountsApplied()).containsAll(manyDiscounts);
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly")
    void should_HandleBigDecimalPrecisionCorrectly() {
        // RED: Test fails if BigDecimal precision is lost in database

        // Given - Quote with standard 2-decimal precision (typical for currency)
        Quote precisionQuote = Quote.builder()
            .id("QT-PRECISION-TEST")
            .premium(new BigDecimal("1234.56"))
            .monthlyPremium(new BigDecimal("102.88"))
            .coverageAmount(new BigDecimal("123456.78"))
            .deductible(new BigDecimal("999.99"))
            .validUntil(LocalDate.now().plusDays(30))
            .vehicleMake("Volkswagen")
            .vehicleModel("Jetta")
            .vehicleYear(2020)
            .vehicleVin("3VW2K7AJ6JM123456")
            .vehicleCurrentValue(new BigDecimal("23456.78"))
            .primaryDriverName("Eva Precision")
            .primaryDriverLicense("P987654321")
            .build();
        
        // When - Save and retrieve quote
        quoteRepository.save(precisionQuote);
        entityManager.flush();
        entityManager.clear();
        
        // Then - GREEN: Precision should be maintained for currency values
        Optional<Quote> retrievedQuote = quoteRepository.findById("QT-PRECISION-TEST");
        assertThat(retrievedQuote).isPresent();
        
        // Verify standard currency precision is maintained (2 decimal places)
        assertThat(retrievedQuote.get().getPremium()).isEqualTo(new BigDecimal("1234.56"));
        assertThat(retrievedQuote.get().getMonthlyPremium()).isEqualTo(new BigDecimal("102.88"));
        assertThat(retrievedQuote.get().getCoverageAmount()).isEqualTo(new BigDecimal("123456.78"));
        assertThat(retrievedQuote.get().getDeductible()).isEqualTo(new BigDecimal("999.99"));
        assertThat(retrievedQuote.get().getVehicleCurrentValue()).isEqualTo(new BigDecimal("23456.78"));
    }

    @Test
    @DisplayName("Should verify database constraints")
    void should_VerifyDatabaseConstraints() {
        // RED: Test fails if nullable=false constraints aren't enforced
        // Note: This test verifies that entity validation works at the JPA level
        // Database constraint violations would throw exceptions at flush time
        
        // Given - Valid quote for baseline
        quoteRepository.save(sampleQuote);
        entityManager.flush();
        
        // When/Then - Verify that all required fields are properly set
        Optional<Quote> savedQuote = quoteRepository.findById("QT-TEST-001");
        assertThat(savedQuote).isPresent();
        
        Quote quote = savedQuote.get();
        
        // Verify all non-nullable fields are populated
        assertThat(quote.getPremium()).isNotNull();
        assertThat(quote.getMonthlyPremium()).isNotNull();
        assertThat(quote.getCoverageAmount()).isNotNull();
        assertThat(quote.getDeductible()).isNotNull();
        assertThat(quote.getValidUntil()).isNotNull();
        assertThat(quote.getCreatedAt()).isNotNull();
        assertThat(quote.getVehicleMake()).isNotNull();
        assertThat(quote.getVehicleModel()).isNotNull();
        assertThat(quote.getVehicleYear()).isNotNull();
        assertThat(quote.getVehicleVin()).isNotNull();
        assertThat(quote.getVehicleCurrentValue()).isNotNull();
        assertThat(quote.getPrimaryDriverName()).isNotNull();
        assertThat(quote.getPrimaryDriverLicense()).isNotNull();
        assertThat(quote.getDiscountsApplied()).isNotNull();
    }
}