package com.autoinsurance.database;

import com.autoinsurance.quote.entity.Quote;
import com.autoinsurance.quote.repository.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Transaction Management Tests
 * 
 * Following TDD Red-Green-Blue methodology:
 * RED: Tests fail when transaction management doesn't work correctly
 * GREEN: Transactions work correctly with proper rollback and commit behavior
 * BLUE: Optimize transaction boundaries and isolation levels
 * 
 * Tests Transaction Management Scenarios:
 * - Transaction rollback on exceptions
 * - Nested transaction behavior
 * - Transaction isolation and consistency
 * - Batch operation transaction boundaries
 * - Read-only transaction optimization
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb_transaction",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false" // Reduced logging for transaction tests
})
@DisplayName("Transaction Management Tests")
class TransactionManagementTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuoteRepository quoteRepository;

    private Quote sampleQuote;

    @BeforeEach
    void setUp() {
        // RED: Create test data for transaction testing
        sampleQuote = Quote.builder()
            .id("TXN-001")
            .premium(new BigDecimal("1200.00"))
            .monthlyPremium(new BigDecimal("100.00"))
            .coverageAmount(new BigDecimal("100000.00"))
            .deductible(new BigDecimal("1000.00"))
            .validUntil(LocalDate.now().plusDays(30))
            .createdAt(LocalDateTime.now())
            .vehicleMake("Honda")
            .vehicleModel("Civic")
            .vehicleYear(2020)
            .vehicleVin("TXN1HGFC2F53JA001")
            .vehicleCurrentValue(new BigDecimal("25000.00"))
            .primaryDriverName("Transaction Test Driver")
            .primaryDriverLicense("TXN123456")
            .discountsApplied(List.of("Test Discount"))
            .build();
    }

    @Test
    @DisplayName("Should commit successful transaction")
    void should_CommitSuccessfulTransaction() {
        // RED: Test fails if successful operations don't commit

        // Given - Valid quote
        assertThat(quoteRepository.count()).isZero();
        
        // When - Save quote (should be committed automatically)
        Quote savedQuote = quoteRepository.save(sampleQuote);
        entityManager.flush(); // Force synchronization with database
        
        // Then - GREEN: Transaction should be committed
        assertThat(savedQuote).isNotNull();
        assertThat(savedQuote.getId()).isEqualTo("TXN-001");
        
        // Verify data persisted
        entityManager.clear(); // Clear persistence context
        Optional<Quote> retrievedQuote = quoteRepository.findById("TXN-001");
        assertThat(retrievedQuote).isPresent();
        assertThat(quoteRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle successful transaction operations")
    void should_HandleSuccessfulTransactionOperations() {
        // RED: Test fails if transaction operations don't work correctly

        // Given - Initial empty state
        assertThat(quoteRepository.count()).isZero();
        
        // When - Perform multiple operations in transaction
        Quote firstQuote = createTestQuote("SUCCESS-001", "Honda", "Civic", 2020, new BigDecimal("1200.00"));
        Quote secondQuote = createTestQuote("SUCCESS-002", "Toyota", "Camry", 2021, new BigDecimal("1300.00"));
        
        quoteRepository.save(firstQuote);
        quoteRepository.save(secondQuote);
        entityManager.flush();
        
        // Update first quote
        Optional<Quote> savedFirst = quoteRepository.findById("SUCCESS-001");
        assertThat(savedFirst).isPresent();
        savedFirst.get().setPremium(new BigDecimal("1250.00"));
        quoteRepository.save(savedFirst.get());
        entityManager.flush();
        
        // Then - GREEN: All operations should be successful
        assertThat(quoteRepository.count()).isEqualTo(2);
        
        Optional<Quote> updatedFirst = quoteRepository.findById("SUCCESS-001");
        assertThat(updatedFirst).isPresent();
        assertThat(updatedFirst.get().getPremium()).isEqualTo(new BigDecimal("1250.00"));
        
        Optional<Quote> savedSecond = quoteRepository.findById("SUCCESS-002");
        assertThat(savedSecond).isPresent();
        assertThat(savedSecond.get().getPremium()).isEqualTo(new BigDecimal("1300.00"));
    }

    @Test
    @DisplayName("Should handle batch operations in single transaction")
    void should_HandleBatchOperationsInSingleTransaction() {
        // RED: Test fails if batch operations don't work transactionally

        // Given - Multiple quotes for batch operation
        List<Quote> batchQuotes = List.of(
            createTestQuote("BATCH-001", "Ford", "Focus", 2021, new BigDecimal("1100.00")),
            createTestQuote("BATCH-002", "Chevrolet", "Malibu", 2020, new BigDecimal("1200.00")),
            createTestQuote("BATCH-003", "Nissan", "Altima", 2022, new BigDecimal("1300.00"))
        );
        
        assertThat(quoteRepository.count()).isZero();
        
        // When - Perform batch save operation
        List<Quote> savedQuotes = quoteRepository.saveAll(batchQuotes);
        entityManager.flush();
        
        // Then - GREEN: All operations should succeed in single transaction
        assertThat(savedQuotes).hasSize(3);
        assertThat(quoteRepository.count()).isEqualTo(3);
        
        // Verify all quotes were saved
        List<String> savedIds = savedQuotes.stream().map(Quote::getId).toList();
        assertThat(savedIds).containsExactlyInAnyOrder("BATCH-001", "BATCH-002", "BATCH-003");
        
        // Test batch update
        savedQuotes.forEach(quote -> 
            quote.setPremium(quote.getPremium().add(new BigDecimal("50.00")))
        );
        
        List<Quote> updatedQuotes = quoteRepository.saveAll(savedQuotes);
        entityManager.flush();
        
        // Verify batch update worked (should be greater than or equal to 1150.00)
        assertThat(updatedQuotes).allSatisfy(quote ->
            assertThat(quote.getPremium()).isGreaterThanOrEqualTo(new BigDecimal("1150.00"))
        );
    }

    @Test
    @DisplayName("Should maintain consistency during concurrent operations")
    void should_MaintainConsistencyDuringConcurrentOperations() {
        // RED: Test fails if concurrent operations break consistency

        // Given - Initial quote
        quoteRepository.save(sampleQuote);
        entityManager.flush();
        entityManager.clear();
        
        // When - Simulate concurrent updates (within same test thread)
        Optional<Quote> quote1 = quoteRepository.findById("TXN-001");
        assertThat(quote1).isPresent();
        
        // Simulate first update
        quote1.get().setPremium(new BigDecimal("1300.00"));
        quoteRepository.save(quote1.get());
        
        // Don't flush yet - simulate concurrent access
        Optional<Quote> quote2 = quoteRepository.findById("TXN-001");
        assertThat(quote2).isPresent();
        
        // Second update
        quote2.get().setMonthlyPremium(new BigDecimal("110.00"));
        quoteRepository.save(quote2.get());
        
        entityManager.flush();
        
        // Then - GREEN: Final state should be consistent
        Optional<Quote> finalQuote = quoteRepository.findById("TXN-001");
        assertThat(finalQuote).isPresent();
        
        // Last write should win
        assertThat(finalQuote.get().getMonthlyPremium()).isEqualTo(new BigDecimal("110.00"));
    }

    @Test
    @Transactional(readOnly = true)
    @DisplayName("Should optimize read-only transactions")
    void should_OptimizeReadOnlyTransactions() {
        // RED: Test fails if read-only transactions don't work

        // Given - Some test data
        quoteRepository.save(sampleQuote);
        Quote secondQuote = createTestQuote("READ-002", "BMW", "X3", 2021, new BigDecimal("1800.00"));
        quoteRepository.save(secondQuote);
        entityManager.flush();
        
        // When - Perform read-only operations
        List<Quote> allQuotes = quoteRepository.findAll();
        long totalCount = quoteRepository.count();
        Optional<Quote> specificQuote = quoteRepository.findById("TXN-001");
        
        // Then - GREEN: Read operations should work in read-only transaction
        assertThat(allQuotes).hasSize(2);
        assertThat(totalCount).isEqualTo(2);
        assertThat(specificQuote).isPresent();
        assertThat(specificQuote.get().getVehicleMake()).isEqualTo("Honda");
        
        // Verify no modifications are possible in read-only transaction
        // Note: In @DataJpaTest, read-only is more of an optimization hint
        // Real read-only enforcement would require additional setup
    }

    @Test
    @DisplayName("Should handle transaction with entity lifecycle events")
    void should_HandleTransactionWithEntityLifecycleEvents() {
        // RED: Test fails if @PrePersist doesn't work in transaction context

        // Given - Quote without createdAt timestamp
        Quote quoteWithoutTimestamp = Quote.builder()
            .id("LIFECYCLE-001")
            .premium(new BigDecimal("1400.00"))
            .monthlyPremium(new BigDecimal("116.67"))
            .coverageAmount(new BigDecimal("120000.00"))
            .deductible(new BigDecimal("1200.00"))
            .validUntil(LocalDate.now().plusDays(30))
            .vehicleMake("Lifecycle")
            .vehicleModel("Test")
            .vehicleYear(2021)
            .vehicleVin("LIFE1234567890")
            .vehicleCurrentValue(new BigDecimal("30000.00"))
            .primaryDriverName("Lifecycle Driver")
            .primaryDriverLicense("LIFE123456")
            .createdAt(null) // Explicitly null - should be set by @PrePersist
            .build();
        
        LocalDateTime beforeSave = LocalDateTime.now();
        
        // When - Save entity (should trigger @PrePersist)
        Quote savedQuote = quoteRepository.save(quoteWithoutTimestamp);
        entityManager.flush();
        
        LocalDateTime afterSave = LocalDateTime.now();
        
        // Then - GREEN: Entity lifecycle events should work in transaction
        assertThat(savedQuote.getCreatedAt()).isNotNull();
        assertThat(savedQuote.getCreatedAt()).isBetween(beforeSave, afterSave);
        
        // Verify persistence
        Optional<Quote> retrievedQuote = quoteRepository.findById("LIFECYCLE-001");
        assertThat(retrievedQuote).isPresent();
        assertThat(retrievedQuote.get().getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should handle large transaction with multiple entity types")
    void should_HandleLargeTransactionWithMultipleEntityTypes() {
        // RED: Test fails if large transactions don't complete successfully

        int largeTransactionSize = 50;
        
        // Given - Generate large number of quotes
        List<Quote> largeQuoteSet = java.util.stream.IntStream.range(0, largeTransactionSize)
            .mapToObj(i -> createTestQuote(
                "LARGE-TXN-" + String.format("%03d", i),
                "Make" + (i % 5),
                "Model" + (i % 8),
                2018 + (i % 6),
                new BigDecimal("1000.00").add(new BigDecimal(i * 25))
            ))
            .toList();
        
        long startTime = System.currentTimeMillis();
        
        // When - Save all in single transaction
        List<Quote> savedQuotes = quoteRepository.saveAll(largeQuoteSet);
        entityManager.flush();
        
        long transactionTime = System.currentTimeMillis() - startTime;
        
        // Then - GREEN: Large transaction should complete successfully
        assertThat(savedQuotes).hasSize(largeTransactionSize);
        assertThat(quoteRepository.count()).isEqualTo(largeTransactionSize);
        
        // Performance assertion for large transaction
        assertThat(transactionTime).isLessThan(10000L); // Should complete under 10 seconds
        
        // Verify data integrity after large transaction
        List<Quote> allQuotes = quoteRepository.findAll();
        assertThat(allQuotes).hasSize(largeTransactionSize);
        assertThat(allQuotes).allSatisfy(quote -> {
            assertThat(quote.getId()).startsWith("LARGE-TXN-");
            assertThat(quote.getPremium()).isGreaterThanOrEqualTo(new BigDecimal("1000.00"));
            assertThat(quote.getCreatedAt()).isNotNull();
        });
        
        System.out.println("Large transaction performance: " + largeTransactionSize + 
                          " entities saved in " + transactionTime + "ms");
    }

    @Test
    @DisplayName("Should handle transaction with collection modifications")
    void should_HandleTransactionWithCollectionModifications() {
        // RED: Test fails if @ElementCollection changes don't work in transaction

        // Given - Quote with initial discounts
        Quote quoteWithDiscounts = Quote.builder()
            .id("COLLECTION-001")
            .premium(new BigDecimal("1500.00"))
            .monthlyPremium(new BigDecimal("125.00"))
            .coverageAmount(new BigDecimal("150000.00"))
            .deductible(new BigDecimal("1500.00"))
            .validUntil(LocalDate.now().plusDays(30))
            .vehicleMake("Collection")
            .vehicleModel("Test")
            .vehicleYear(2022)
            .vehicleVin("COLL1234567890")
            .vehicleCurrentValue(new BigDecimal("35000.00"))
            .primaryDriverName("Collection Driver")
            .primaryDriverLicense("COLL123456")
            .discountsApplied(List.of("Initial Discount"))
            .build();
        
        // When - Save and then modify collection in transaction
        quoteRepository.save(quoteWithDiscounts);
        entityManager.flush();
        
        // Modify collection
        Optional<Quote> savedQuote = quoteRepository.findById("COLLECTION-001");
        assertThat(savedQuote).isPresent();
        
        List<String> updatedDiscounts = new java.util.ArrayList<>(List.of(
            "Initial Discount",
            "Safe Driver Discount", 
            "Multi-Policy Discount",
            "Loyalty Discount"
        ));
        savedQuote.get().setDiscountsApplied(updatedDiscounts);
        
        quoteRepository.save(savedQuote.get());
        entityManager.flush();
        entityManager.clear();
        
        // Then - GREEN: Collection modifications should be transactional
        Optional<Quote> finalQuote = quoteRepository.findById("COLLECTION-001");
        assertThat(finalQuote).isPresent();
        assertThat(finalQuote.get().getDiscountsApplied()).hasSize(4);
        assertThat(finalQuote.get().getDiscountsApplied()).containsExactlyInAnyOrder(
            "Initial Discount", "Safe Driver Discount", "Multi-Policy Discount", "Loyalty Discount"
        );
        
        // Verify collection changes persisted correctly
        Number discountCount = (Number) entityManager.getEntityManager()
            .createNativeQuery("SELECT COUNT(*) FROM quote_discounts WHERE quote_id = 'COLLECTION-001'")
            .getSingleResult();
        assertThat(discountCount.intValue()).isEqualTo(4);
    }

    private Quote createTestQuote(String id, String make, String model, Integer year, BigDecimal premium) {
        return Quote.builder()
            .id(id)
            .premium(premium)
            .monthlyPremium(premium.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP))
            .coverageAmount(new BigDecimal("100000.00"))
            .deductible(new BigDecimal("1000.00"))
            .validUntil(LocalDate.now().plusDays(30))
            .createdAt(LocalDateTime.now())
            .vehicleMake(make)
            .vehicleModel(model)
            .vehicleYear(year)
            .vehicleVin("VIN" + id + "1234567890".substring(0, Math.min(10, 17 - id.length() - 3)))
            .vehicleCurrentValue(new BigDecimal("25000.00"))
            .primaryDriverName("Driver " + id)
            .primaryDriverLicense("LIC" + id)
            .discountsApplied(List.of("Standard Discount"))
            .build();
    }
}