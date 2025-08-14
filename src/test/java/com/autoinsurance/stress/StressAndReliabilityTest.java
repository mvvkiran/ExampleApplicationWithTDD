package com.autoinsurance.stress;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.QuoteResponseDto;
import com.autoinsurance.quote.dto.VehicleDto;
import com.autoinsurance.quote.dto.DriverDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Stress & Reliability Test Suite
 * 
 * Following TDD Red-Green-Blue methodology:
 * RED: Tests fail when system cannot handle stress conditions reliably
 * GREEN: System performs consistently under stress and recovers from failures
 * BLUE: Optimize system resilience and performance under extreme conditions
 * 
 * Tests Stress & Reliability Scenarios:
 * - High concurrent load handling
 * - Memory usage under stress
 * - Database connection pool stress
 * - Response time consistency under load
 * - System recovery after failures
 * - Resource exhaustion scenarios
 * - Long-running operation stability
 * - Error rate monitoring under stress
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb_stress",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",
    "logging.level.com.autoinsurance=WARN", // Reduce logging for stress tests
    "spring.datasource.hikari.maximum-pool-size=20", // Increase pool for stress testing
    "spring.datasource.hikari.minimum-idle=5"
})
@DisplayName("Stress & Reliability Tests")
class StressAndReliabilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private QuoteRequestDto standardQuoteRequest;
    private List<QuoteRequestDto> diverseQuoteRequests;

    @BeforeEach
    void setUp() {
        // RED: Setup diverse test data for stress testing

        // Standard quote request
        VehicleDto standardVehicle = VehicleDto.builder()
            .make("Honda")
            .model("Civic")
            .year(2020)
            .vin("1HGFC2F53JA123456")
            .currentValue(new BigDecimal("25000.00"))
            .build();

        DriverDto standardDriver = DriverDto.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(1990, 5, 15))
            .licenseNumber("D123456789")
            .licenseState("CA")
            .yearsOfExperience(8)
            .build();

        standardQuoteRequest = new QuoteRequestDto(
            standardVehicle,
            List.of(standardDriver),
            new BigDecimal("100000.00"),
            new BigDecimal("1000.00")
        );

        // Create diverse quote requests for varied stress testing
        diverseQuoteRequests = new ArrayList<>();
        
        // Add different vehicle types and driver profiles
        String[] makes = {"Honda", "Toyota", "Ford", "BMW", "Mercedes", "Audi"};
        String[] models = {"Civic", "Camry", "F150", "X5", "C-Class", "A4"};
        String[] states = {"CA", "NY", "TX", "FL", "IL", "PA"};
        
        for (int i = 0; i < 10; i++) {
            VehicleDto vehicle = VehicleDto.builder()
                .make(makes[i % makes.length])
                .model(models[i % models.length])
                .year(2018 + (i % 5))
                .vin(String.format("1HGBH41JXMN1091%02d", 80 + i)) // Valid 17-char VIN pattern
                .currentValue(new BigDecimal(Math.min(900000, 20000 + (i * 25000)))) // Keep under 1M limit
                .build();

            DriverDto driver = DriverDto.builder()
                .firstName("Driver" + i)
                .lastName("Test" + i)
                .dateOfBirth(LocalDate.of(1980 + (i % 20), 1 + (i % 12), 1 + (i % 28)))
                .licenseNumber("D" + String.format("%09d", i))
                .licenseState(states[i % states.length])
                .yearsOfExperience(5 + (i % 15))
                .build();

            QuoteRequestDto request = new QuoteRequestDto(
                vehicle,
                List.of(driver),
                new BigDecimal(Math.max(25000, Math.min(1000000, 50000 + (i * 25000)))), // Keep in valid range
                new BigDecimal(Math.max(250, Math.min(10000, 500 + (i * 250)))) // Keep in valid range
            );
            
            diverseQuoteRequests.add(request);
        }
    }

    @Test
    @Timeout(30) // 30 second timeout
    @DisplayName("Should handle high concurrent load")
    void should_HandleHighConcurrentLoad() throws Exception {
        // RED: Test fails if system cannot handle concurrent requests

        int numberOfConcurrentRequests = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executorService);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);

        long startTime = System.currentTimeMillis();

        // Submit concurrent requests
        for (int i = 0; i < numberOfConcurrentRequests; i++) {
            final int requestIndex = i;
            completionService.submit(() -> {
                try {
                    QuoteRequestDto request = diverseQuoteRequests.get(requestIndex % diverseQuoteRequests.size());
                    String requestBody = objectMapper.writeValueAsString(request);
                    
                    long requestStart = System.currentTimeMillis();
                    MvcResult result = mockMvc.perform(post("/api/v1/quotes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                            .andExpect(status().isCreated())
                            .andReturn();
                    long requestEnd = System.currentTimeMillis();
                    
                    totalResponseTime.addAndGet(requestEnd - requestStart);
                    successCount.incrementAndGet();
                    
                    // Verify response structure
                    String responseJson = result.getResponse().getContentAsString();
                    QuoteResponseDto response = objectMapper.readValue(responseJson, QuoteResponseDto.class);
                    assertThat(response.getQuoteId()).isNotNull();
                    assertThat(response.getPremium()).isGreaterThan(BigDecimal.ZERO);
                    
                    return true;
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    return false;
                }
            });
        }

        // Wait for all requests to complete
        for (int i = 0; i < numberOfConcurrentRequests; i++) {
            completionService.take().get();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double averageResponseTime = (double) totalResponseTime.get() / successCount.get();

        executorService.shutdown();

        // GREEN: System should handle all requests successfully
        System.out.println("Concurrent Load Test Results:");
        System.out.println("  Total Requests: " + numberOfConcurrentRequests);
        System.out.println("  Successful: " + successCount.get());
        System.out.println("  Errors: " + errorCount.get());
        System.out.println("  Total Time: " + totalTime + "ms");
        System.out.println("  Average Response Time: " + String.format("%.2f", averageResponseTime) + "ms");
        System.out.println("  Requests/Second: " + String.format("%.2f", (double) numberOfConcurrentRequests / (totalTime / 1000.0)));

        // Assertions for stress test success
        assertThat(successCount.get()).isGreaterThan((int)(numberOfConcurrentRequests * 0.95)); // At least 95% success rate
        assertThat(averageResponseTime).isLessThan(5000.0); // Average response under 5 seconds
        assertThat(errorCount.get()).isLessThan((int)(numberOfConcurrentRequests * 0.05)); // Error rate under 5%
    }

    @Test
    @Timeout(60) // 60 second timeout for sustained load
    @DisplayName("Should maintain performance under sustained load")
    void should_MaintainPerformanceUnderSustainedLoad() throws Exception {
        // RED: Test fails if performance degrades significantly under sustained load

        int requestsPerSecond = 10;
        int durationInSeconds = 30;
        int totalRequests = requestsPerSecond * durationInSeconds;
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();
        
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        
        long startTime = System.currentTimeMillis();
        
        // Schedule requests at regular intervals
        AtomicInteger requestsSent = new AtomicInteger(0);
        ScheduledFuture<?> requestScheduler = scheduler.scheduleAtFixedRate(() -> {
            if (requestsSent.get() >= totalRequests) {
                return;
            }
            
            executorService.submit(() -> {
                try {
                    int requestIndex = requestsSent.getAndIncrement();
                    if (requestIndex >= totalRequests) {
                        return;
                    }
                    
                    QuoteRequestDto request = diverseQuoteRequests.get(requestIndex % diverseQuoteRequests.size());
                    String requestBody = objectMapper.writeValueAsString(request);
                    
                    long requestStart = System.currentTimeMillis();
                    mockMvc.perform(post("/api/v1/quotes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                            .andExpect(status().isCreated());
                    long requestEnd = System.currentTimeMillis();
                    
                    responseTimes.add(requestEnd - requestStart);
                    successCount.incrementAndGet();
                    
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                }
            });
        }, 0, 1000 / requestsPerSecond, TimeUnit.MILLISECONDS);

        // Wait for test duration plus buffer
        Thread.sleep((durationInSeconds + 5) * 1000);
        
        requestScheduler.cancel(true);
        scheduler.shutdown();
        executorService.shutdown();
        
        boolean terminated = executorService.awaitTermination(10, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // Calculate performance metrics
        double averageResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
        
        long maxResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        
        long minResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0L);

        // GREEN: Performance should be consistent throughout the test
        System.out.println("Sustained Load Test Results:");
        System.out.println("  Duration: " + (totalTime / 1000.0) + " seconds");
        System.out.println("  Total Requests Sent: " + requestsSent.get());
        System.out.println("  Successful: " + successCount.get());
        System.out.println("  Errors: " + errorCount.get());
        System.out.println("  Average Response Time: " + String.format("%.2f", averageResponseTime) + "ms");
        System.out.println("  Min Response Time: " + minResponseTime + "ms");
        System.out.println("  Max Response Time: " + maxResponseTime + "ms");
        System.out.println("  Actual RPS: " + String.format("%.2f", (double) successCount.get() / (totalTime / 1000.0)));

        // Assertions for sustained performance
        assertThat(terminated).isTrue(); // All tasks completed within timeout
        assertThat(successCount.get()).isGreaterThan((int)(totalRequests * 0.9)); // At least 90% success rate
        assertThat(averageResponseTime).isLessThan(3000.0); // Average response under 3 seconds
        assertThat(maxResponseTime).isLessThan(10000L); // No response exceeds 10 seconds
    }

    @Test
    @Timeout(45)
    @DisplayName("Should handle database connection pool stress")
    void should_HandleDatabaseConnectionPoolStress() throws Exception {
        // RED: Test fails if database connection pool is exhausted

        int numberOfConcurrentConnections = 25; // Slightly above pool size to test limits
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfConcurrentConnections);
        CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executorService);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger connectionErrors = new AtomicInteger(0);
        
        // Submit requests that will hold database connections
        for (int i = 0; i < numberOfConcurrentConnections; i++) {
            final int requestIndex = i;
            completionService.submit(() -> {
                try {
                    // Use different request to vary database load
                    QuoteRequestDto request = diverseQuoteRequests.get(requestIndex % diverseQuoteRequests.size());
                    String requestBody = objectMapper.writeValueAsString(request);
                    
                    MvcResult createResult = mockMvc.perform(post("/api/v1/quotes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                            .andExpect(status().isCreated())
                            .andReturn();
                    
                    // Extract quote ID and immediately retrieve it (double database hit)
                    String createResponseJson = createResult.getResponse().getContentAsString();
                    QuoteResponseDto createdQuote = objectMapper.readValue(createResponseJson, QuoteResponseDto.class);
                    
                    mockMvc.perform(get("/api/v1/quotes/" + createdQuote.getQuoteId())
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.quoteId").value(createdQuote.getQuoteId()));
                    
                    successCount.incrementAndGet();
                    return true;
                    
                } catch (Exception e) {
                    // Check if error is related to connection pool exhaustion
                    if (e.getMessage() != null && 
                        (e.getMessage().contains("connection") || 
                         e.getMessage().contains("pool") ||
                         e.getMessage().contains("timeout"))) {
                        connectionErrors.incrementAndGet();
                    }
                    return false;
                }
            });
        }

        // Wait for all tasks to complete
        int completed = 0;
        while (completed < numberOfConcurrentConnections) {
            completionService.take().get();
            completed++;
        }

        executorService.shutdown();

        // GREEN: System should handle connection pool stress gracefully
        System.out.println("Database Connection Pool Stress Results:");
        System.out.println("  Concurrent Connections Attempted: " + numberOfConcurrentConnections);
        System.out.println("  Successful Operations: " + successCount.get());
        System.out.println("  Connection-Related Errors: " + connectionErrors.get());
        System.out.println("  Success Rate: " + String.format("%.1f", (double) successCount.get() / numberOfConcurrentConnections * 100) + "%");

        // Assertions - should handle most requests even under connection stress
        assertThat(successCount.get()).isGreaterThan((int)(numberOfConcurrentConnections * 0.8)); // At least 80% success
        // Note: Some connection timeouts may occur, which is acceptable behavior
    }

    @Test
    @Timeout(30)
    @DisplayName("Should maintain consistent response times under varying load")
    void should_MaintainConsistentResponseTimesUnderVaryingLoad() throws Exception {
        // RED: Test fails if response times become highly inconsistent under load

        List<Long> lightLoadTimes = new ArrayList<>();
        List<Long> heavyLoadTimes = new ArrayList<>();
        
        // Phase 1: Light load baseline
        System.out.println("Phase 1: Establishing light load baseline...");
        for (int i = 0; i < 10; i++) {
            QuoteRequestDto request = diverseQuoteRequests.get(i % diverseQuoteRequests.size());
            String requestBody = objectMapper.writeValueAsString(request);
            
            long startTime = System.currentTimeMillis();
            mockMvc.perform(post("/api/v1/quotes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isCreated());
            long endTime = System.currentTimeMillis();
            
            lightLoadTimes.add(endTime - startTime);
            Thread.sleep(100); // Small delay between requests
        }
        
        // Phase 2: Heavy concurrent load
        System.out.println("Phase 2: Testing under heavy concurrent load...");
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        CompletionService<Long> completionService = new ExecutorCompletionService<>(executorService);
        
        int heavyLoadRequests = 30;
        for (int i = 0; i < heavyLoadRequests; i++) {
            final int requestIndex = i;
            completionService.submit(() -> {
                try {
                    QuoteRequestDto request = diverseQuoteRequests.get(requestIndex % diverseQuoteRequests.size());
                    String requestBody = objectMapper.writeValueAsString(request);
                    
                    long startTime = System.currentTimeMillis();
                    mockMvc.perform(post("/api/v1/quotes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                            .andExpect(status().isCreated());
                    long endTime = System.currentTimeMillis();
                    
                    return endTime - startTime;
                } catch (Exception e) {
                    return -1L; // Error indicator
                }
            });
        }
        
        // Collect heavy load results
        for (int i = 0; i < heavyLoadRequests; i++) {
            Long responseTime = completionService.take().get();
            if (responseTime > 0) {
                heavyLoadTimes.add(responseTime);
            }
        }
        
        executorService.shutdown();
        
        // Calculate statistics
        double lightLoadAvg = lightLoadTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double heavyLoadAvg = heavyLoadTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        
        long lightLoadMax = lightLoadTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
        long heavyLoadMax = heavyLoadTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
        
        // GREEN: Response times should be reasonably consistent
        System.out.println("Response Time Consistency Results:");
        System.out.println("  Light Load Average: " + String.format("%.2f", lightLoadAvg) + "ms");
        System.out.println("  Heavy Load Average: " + String.format("%.2f", heavyLoadAvg) + "ms");
        System.out.println("  Light Load Max: " + lightLoadMax + "ms");
        System.out.println("  Heavy Load Max: " + heavyLoadMax + "ms");
        System.out.println("  Performance Degradation: " + String.format("%.1f", (heavyLoadAvg / lightLoadAvg - 1) * 100) + "%");

        // Assertions for consistency
        assertThat(heavyLoadTimes).hasSizeGreaterThan((int)(heavyLoadRequests * 0.9)); // Most requests succeeded
        assertThat(heavyLoadAvg).isLessThan(lightLoadAvg * 5); // Heavy load not more than 5x slower
        assertThat(heavyLoadMax).isLessThan(15000L); // No individual request exceeds 15 seconds
    }

    @Test
    @Timeout(20)
    @DisplayName("Should recover gracefully from simulated failures")
    void should_RecoverGracefullyFromSimulatedFailures() throws Exception {
        // RED: Test fails if system cannot recover from error conditions

        AtomicInteger totalRequests = new AtomicInteger(0);
        AtomicInteger successfulRequests = new AtomicInteger(0);
        AtomicInteger failedRequests = new AtomicInteger(0);
        AtomicInteger recoveryRequests = new AtomicInteger(0);
        
        // Phase 1: Normal operations
        System.out.println("Phase 1: Normal operations baseline...");
        for (int i = 0; i < 10; i++) {
            try {
                QuoteRequestDto request = diverseQuoteRequests.get(i % diverseQuoteRequests.size());
                String requestBody = objectMapper.writeValueAsString(request);
                
                mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isCreated());
                
                totalRequests.incrementAndGet();
                successfulRequests.incrementAndGet();
            } catch (Exception e) {
                totalRequests.incrementAndGet();
                failedRequests.incrementAndGet();
            }
        }
        
        // Phase 2: Introduce invalid requests (simulated failures)
        System.out.println("Phase 2: Introducing simulated failures...");
        for (int i = 0; i < 5; i++) {
            try {
                // Create deliberately invalid request
                VehicleDto invalidVehicle = VehicleDto.builder()
                    .make("") // Invalid - empty make
                    .model("")
                    .year(1800) // Invalid year
                    .vin("INVALID")
                    .currentValue(new BigDecimal("-1000")) // Negative value
                    .build();

                QuoteRequestDto invalidRequest = new QuoteRequestDto(
                    invalidVehicle,
                    List.of(), // Empty drivers list
                    new BigDecimal("-50000"), // Invalid coverage
                    new BigDecimal("0") // Invalid deductible
                );
                
                String requestBody = objectMapper.writeValueAsString(invalidRequest);
                
                mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isBadRequest()); // Expect this to fail
                
                totalRequests.incrementAndGet();
                failedRequests.incrementAndGet();
            } catch (Exception e) {
                totalRequests.incrementAndGet();
                failedRequests.incrementAndGet();
            }
        }
        
        // Phase 3: Recovery - normal requests should still work
        System.out.println("Phase 3: Testing system recovery...");
        for (int i = 0; i < 15; i++) {
            try {
                QuoteRequestDto request = diverseQuoteRequests.get(i % diverseQuoteRequests.size());
                String requestBody = objectMapper.writeValueAsString(request);
                
                mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isCreated());
                
                totalRequests.incrementAndGet();
                successfulRequests.incrementAndGet();
                recoveryRequests.incrementAndGet();
            } catch (Exception e) {
                totalRequests.incrementAndGet();
                failedRequests.incrementAndGet();
            }
        }

        // GREEN: System should recover and process valid requests normally
        System.out.println("Failure Recovery Test Results:");
        System.out.println("  Total Requests: " + totalRequests.get());
        System.out.println("  Successful Requests: " + successfulRequests.get());
        System.out.println("  Failed Requests: " + failedRequests.get());
        System.out.println("  Recovery Phase Requests: " + recoveryRequests.get());
        System.out.println("  Overall Success Rate: " + String.format("%.1f", (double) successfulRequests.get() / totalRequests.get() * 100) + "%");
        System.out.println("  Recovery Phase Success: " + String.format("%.1f", (double) recoveryRequests.get() / 15 * 100) + "%");

        // Assertions for recovery capability
        assertThat(recoveryRequests.get()).isGreaterThan(12); // At least 80% of recovery requests succeeded
        assertThat(successfulRequests.get()).isGreaterThan(20); // Overall high success rate for valid requests
    }

    @Test
    @Timeout(25)
    @DisplayName("Should handle mixed read/write operations under stress")
    void should_HandleMixedReadWriteOperationsUnderStress() throws Exception {
        // RED: Test fails if mixed operations cause deadlocks or performance issues

        ExecutorService executorService = Executors.newFixedThreadPool(6);
        CompletionService<String> completionService = new ExecutorCompletionService<>(executorService);
        
        AtomicInteger createCount = new AtomicInteger(0);
        AtomicInteger readCount = new AtomicInteger(0);
        AtomicInteger calculateCount = new AtomicInteger(0);
        List<String> createdQuoteIds = new CopyOnWriteArrayList<>();
        
        int totalOperations = 60;
        
        // Submit mixed operations
        for (int i = 0; i < totalOperations; i++) {
            final int operationType = i % 3; // 0=create, 1=read, 2=calculate
            final int requestIndex = i;
            
            completionService.submit(() -> {
                try {
                    switch (operationType) {
                        case 0: // Create quote
                            QuoteRequestDto createRequest = diverseQuoteRequests.get(requestIndex % diverseQuoteRequests.size());
                            String createBody = objectMapper.writeValueAsString(createRequest);
                            
                            MvcResult createResult = mockMvc.perform(post("/api/v1/quotes")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(createBody))
                                    .andExpect(status().isCreated())
                                    .andReturn();
                            
                            String createResponseJson = createResult.getResponse().getContentAsString();
                            QuoteResponseDto created = objectMapper.readValue(createResponseJson, QuoteResponseDto.class);
                            createdQuoteIds.add(created.getQuoteId());
                            createCount.incrementAndGet();
                            return "CREATE_SUCCESS";
                            
                        case 1: // Read quote
                            if (!createdQuoteIds.isEmpty()) {
                                String quoteId = createdQuoteIds.get(requestIndex % createdQuoteIds.size());
                                mockMvc.perform(get("/api/v1/quotes/" + quoteId)
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.quoteId").value(quoteId));
                                readCount.incrementAndGet();
                                return "READ_SUCCESS";
                            } else {
                                return "READ_SKIPPED";
                            }
                            
                        case 2: // Calculate premium
                            QuoteRequestDto calculateRequest = diverseQuoteRequests.get(requestIndex % diverseQuoteRequests.size());
                            String calculateBody = objectMapper.writeValueAsString(calculateRequest);
                            
                            mockMvc.perform(post("/api/v1/quotes/calculate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(calculateBody))
                                    .andExpect(status().isOk())
                                    .andExpect(jsonPath("$.premium").exists());
                            calculateCount.incrementAndGet();
                            return "CALCULATE_SUCCESS";
                            
                        default:
                            return "UNKNOWN_OPERATION";
                    }
                } catch (Exception e) {
                    return "ERROR: " + e.getClass().getSimpleName();
                }
            });
        }

        // Wait for all operations to complete
        int successfulOperations = 0;
        for (int i = 0; i < totalOperations; i++) {
            String result = completionService.take().get();
            if (result.endsWith("_SUCCESS")) {
                successfulOperations++;
            }
        }

        executorService.shutdown();

        // GREEN: Mixed operations should all complete successfully
        System.out.println("Mixed Operations Stress Test Results:");
        System.out.println("  Total Operations: " + totalOperations);
        System.out.println("  Successful Operations: " + successfulOperations);
        System.out.println("  Create Operations: " + createCount.get());
        System.out.println("  Read Operations: " + readCount.get());
        System.out.println("  Calculate Operations: " + calculateCount.get());
        System.out.println("  Success Rate: " + String.format("%.1f", (double) successfulOperations / totalOperations * 100) + "%");
        System.out.println("  Created Quotes: " + createdQuoteIds.size());

        // Assertions for mixed operation success
        assertThat(successfulOperations).isGreaterThan((int)(totalOperations * 0.9)); // 90% success rate
        assertThat(createCount.get()).isGreaterThan(15); // At least some creates
        assertThat(calculateCount.get()).isGreaterThan(15); // At least some calculations
        assertThat(createdQuoteIds).hasSizeGreaterThan(15); // Quotes were actually created
    }
}