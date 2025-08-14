package com.autoinsurance.performance;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Auto Insurance API Load Test Simulation
 * 
 * Performance testing for Quote API following TDD Red-Green-Blue cycle:
 * RED: Test fails when API performance doesn't meet requirements
 * GREEN: API meets performance thresholds under load
 * BLUE: Refactor API code while maintaining performance standards
 * 
 * Performance Requirements:
 * - Quote Generation: < 2 seconds
 * - API Response: < 500ms for simple requests
 * - Concurrent Users: Support 100+ users
 * - Success Rate: > 95%
 */
public class LoadSimulation extends Simulation {

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .userAgentHeader("Gatling Performance Test");

    // Sample Quote Request Data
    String quoteRequestBody = """
        {
            "customerAge": 28,
            "vehicleYear": 2020,
            "vehicleModel": "Honda Civic",
            "coverageType": "COMPREHENSIVE",
            "deductible": 1000.00,
            "annualMileage": 12000
        }
        """;

    String quoteRequestBodyYoungDriver = """
        {
            "customerAge": 22,
            "vehicleYear": 2018,
            "vehicleModel": "Toyota Corolla", 
            "coverageType": "LIABILITY",
            "deductible": 500.00,
            "annualMileage": 15000
        }
        """;

    String quoteRequestBodyOlderDriver = """
        {
            "customerAge": 45,
            "vehicleYear": 2023,
            "vehicleModel": "BMW X5",
            "coverageType": "COMPREHENSIVE",
            "deductible": 2000.00,
            "annualMileage": 8000
        }
        """;

    // Quote Generation Scenario
    ScenarioBuilder quoteGenerationScenario = scenario("Quote Generation Load Test")
        .feed(
            listFeeder(java.util.List.of(
                Map.of("quoteBody", quoteRequestBody, "userType", "Standard"),
                Map.of("quoteBody", quoteRequestBodyYoungDriver, "userType", "Young"),
                Map.of("quoteBody", quoteRequestBodyOlderDriver, "userType", "Experienced")
            )).random()
        )
        .exec(
            http("Generate Quote - #{userType} Driver")
                .post("/api/v1/quotes")
                .body(StringBody("#{quoteBody}"))
                .check(
                    status().is(200),
                    responseTimeInMillis().lt(2000), // Must be under 2 seconds
                    jsonPath("$.premium").exists(),
                    jsonPath("$.quoteId").exists()
                )
        )
        .pause(Duration.ofSeconds(1), Duration.ofSeconds(3));

    // Health Check Scenario
    ScenarioBuilder healthCheckScenario = scenario("Health Check Performance")
        .exec(
            http("Health Check")
                .get("/actuator/health")
                .check(
                    status().is(200),
                    responseTimeInMillis().lt(500) // Must be under 500ms
                )
        )
        .pause(Duration.ofSeconds(2), Duration.ofSeconds(5));

    // API Documentation Access Scenario  
    ScenarioBuilder swaggerScenario = scenario("API Documentation Access")
        .exec(
            http("Swagger UI Access")
                .get("/swagger-ui/index.html")
                .check(
                    status().is(200),
                    responseTimeInMillis().lt(1000)
                )
        )
        .pause(Duration.ofSeconds(5), Duration.ofSeconds(10));

    // Load Test Setup
    {
        setUp(
            // Primary load: Quote generation
            quoteGenerationScenario.injectOpen(
                rampUsers(20).during(Duration.ofSeconds(30)),     // Ramp up to 20 users
                constantUsersPerSec(5).during(Duration.ofMinutes(2)),  // Maintain 5 users per second
                rampUsers(50).during(Duration.ofSeconds(30))      // Peak load test
            ),
            
            // Background load: Health checks
            healthCheckScenario.injectOpen(
                constantUsersPerSec(1).during(Duration.ofMinutes(3))
            ),
            
            // Occasional load: Documentation access
            swaggerScenario.injectOpen(
                rampUsers(10).during(Duration.ofMinutes(3))
            )
        ).protocols(httpProtocol)
        .assertions(
            // Performance Assertions (TDD Red-Green-Blue)
            global().responseTime().max().lt(5000),              // No response > 5 seconds
            global().responseTime().mean().lt(1000),             // Average response < 1 second
            global().successfulRequests().percent().gt(95.0),    // Success rate > 95%
            
            // Quote-specific assertions
            forAll().responseTime().percentile3().lt(3000),      // 95th percentile < 3 seconds
            forAll().failedRequests().percent().lt(5.0)          // Failed requests < 5%
        );
    }
}