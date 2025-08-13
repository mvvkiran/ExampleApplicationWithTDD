package com.autoinsurance.quote.controller;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.QuoteResponseDto;
import com.autoinsurance.quote.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/quotes")
@Tag(
    name = "Quote Management", 
    description = "API endpoints for auto insurance quote generation, retrieval, and premium calculations. " +
                  "Supports comprehensive risk assessment, discount calculations, and quote lifecycle management."
)
@SecurityRequirement(name = "bearerAuth")
public class QuoteController {

    private static final Logger log = LoggerFactory.getLogger(QuoteController.class);
    
    private final QuoteService quoteService;
    
    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Generate Insurance Quote",
        description = """
            Generates a comprehensive auto insurance quote based on vehicle information, driver profiles, 
            and coverage requirements. Performs risk assessment, applies eligible discounts, and calculates 
            both annual and monthly premium amounts.
            
            **Process Flow:**
            1. Validates all input data (vehicle, drivers, coverage)
            2. Calculates base premium using risk assessment algorithms
            3. Applies eligible discounts (safe driver, multi-policy, etc.)
            4. Generates quote with 30-day validity period
            5. Saves quote for future retrieval
            
            **Risk Factors Considered:**
            - Vehicle age, make, model, and current value
            - Driver age, experience, and driving history
            - Coverage amount and deductible selection
            - Geographic location and usage patterns
            """,
        tags = {"Quote Generation"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Quote successfully generated",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = QuoteResponseDto.class),
                examples = @ExampleObject(
                    name = "Successful Quote Generation",
                    value = """
                        {
                            "quoteId": "q-12345678-abcd-4567-89ef-123456789012",
                            "premium": 1200.00,
                            "monthlyPremium": 100.00,
                            "coverageAmount": 250000.00,
                            "deductible": 1000.00,
                            "validUntil": "2024-09-15",
                            "discountsApplied": [
                                "Safe Driver Discount - 15%",
                                "Multi-Policy Discount - 10%"
                            ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data - validation errors",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = {
                    @ExampleObject(
                        name = "Driver Age Validation Error",
                        value = """
                            {
                                "message": "Driver must be at least 18 years old. Current age: 17 years",
                                "timestamp": "2024-08-15T10:30:00Z"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Vehicle VIN Validation Error",
                        value = """
                            {
                                "message": "Invalid VIN format: INVALID123. Expected format: ^[A-HJ-NPR-Z0-9]{17}$",
                                "timestamp": "2024-08-15T10:30:00Z"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                            "message": "Authentication token required",
                            "timestamp": "2024-08-15T10:30:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error during quote generation",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                            "message": "Failed to generate quote due to system error",
                            "timestamp": "2024-08-15T10:30:00Z"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<QuoteResponseDto> createQuote(
            @Parameter(
                description = "Quote request containing vehicle information, driver profiles, and coverage requirements",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = QuoteRequestDto.class),
                    examples = @ExampleObject(
                        name = "Complete Quote Request",
                        value = """
                            {
                                "vehicle": {
                                    "make": "Honda",
                                    "model": "Accord",
                                    "year": 2021,
                                    "vin": "1HGCV1F31JA123456",
                                    "currentValue": 30000.00
                                },
                                "drivers": [
                                    {
                                        "firstName": "Jane",
                                        "lastName": "Smith",
                                        "dateOfBirth": "1990-03-20",
                                        "licenseNumber": "S987654321",
                                        "licenseState": "NY",
                                        "yearsOfExperience": 10,
                                        "safeDriverDiscount": true,
                                        "multiPolicyDiscount": true
                                    }
                                ],
                                "coverageAmount": 150000.00,
                                "deductible": 1000.00
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody QuoteRequestDto request) {
        log.info("Creating new quote request");
        
        QuoteResponseDto response = quoteService.generateQuote(request);
        
        log.info("Quote created with ID: {} and premium: {}", response.getQuoteId(), response.getPremium());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Retrieve Insurance Quote",
        description = """
            Retrieves a previously generated insurance quote by its unique identifier. 
            Returns complete quote details including premium calculations, coverage information, 
            and applied discounts.
            
            **Use Cases:**
            - Review previously generated quotes
            - Compare different quote options
            - Retrieve quote details for policy conversion
            - Customer service quote lookup
            
            **Performance:**
            - Results are cached for optimal response times
            - Average response time: < 100ms
            """,
        tags = {"Quote Retrieval"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Quote found and retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = QuoteResponseDto.class),
                examples = @ExampleObject(
                    name = "Retrieved Quote",
                    value = """
                        {
                            "quoteId": "q-12345678-abcd-4567-89ef-123456789012",
                            "premium": 1200.00,
                            "monthlyPremium": 100.00,
                            "coverageAmount": 250000.00,
                            "deductible": 1000.00,
                            "validUntil": "2024-09-15",
                            "discountsApplied": [
                                "Safe Driver Discount - 15%",
                                "Multi-Policy Discount - 10%"
                            ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Quote not found with the provided ID",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Quote Not Found",
                    value = """
                        {
                            "message": "Quote not found with ID: q-nonexistent-quote-id",
                            "timestamp": "2024-08-15T10:30:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid quote ID format",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Invalid ID Format",
                    value = """
                        {
                            "message": "Quote ID cannot be null or empty",
                            "timestamp": "2024-08-15T10:30:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        )
    })
    public ResponseEntity<QuoteResponseDto> getQuote(
            @Parameter(
                description = "Unique identifier of the quote to retrieve",
                required = true,
                example = "q-12345678-abcd-4567-89ef-123456789012"
            )
            @PathVariable String id) {
        log.info("Retrieving quote with ID: {}", id);
        
        QuoteResponseDto response = quoteService.getQuoteById(id);
        
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping(
        value = "/calculate", 
        consumes = MediaType.APPLICATION_JSON_VALUE, 
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Calculate Premium Only",
        description = """
            Calculates the insurance premium without generating a full quote. This endpoint provides 
            quick premium estimation for comparison shopping or preliminary cost assessment.
            
            **Use Cases:**
            - Quick premium estimates for different coverage levels
            - Comparison shopping between different vehicles or coverage options
            - Cost assessment before full quote generation
            - Integration with quote calculators and comparison tools
            
            **Calculation Process:**
            1. Validates input data
            2. Performs risk assessment calculations
            3. Returns base premium (before discounts)
            4. Does not save quote data or apply discounts
            
            **Performance:**
            - Faster than full quote generation
            - No database persistence
            - Results cached for repeated requests
            """,
        tags = {"Premium Calculation"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Premium calculated successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Premium Calculation Result",
                    value = """
                        {
                            "premium": 1350.50
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data - validation errors",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = {
                    @ExampleObject(
                        name = "Missing Vehicle Information",
                        value = """
                            {
                                "message": "Vehicle information is required",
                                "timestamp": "2024-08-15T10:30:00Z"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Invalid Coverage Amount",
                        value = """
                            {
                                "message": "Valid coverage amount is required",
                                "timestamp": "2024-08-15T10:30:00Z"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Premium calculation service error"
        )
    })
    public ResponseEntity<Map<String, BigDecimal>> calculatePremium(
            @Parameter(
                description = "Quote request data for premium calculation (same format as quote generation)",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = QuoteRequestDto.class),
                    examples = @ExampleObject(
                        name = "Premium Calculation Request",
                        value = """
                            {
                                "vehicle": {
                                    "make": "Toyota",
                                    "model": "Camry",
                                    "year": 2020,
                                    "vin": "4T1BF1FK1CU123456",
                                    "currentValue": 25000.00
                                },
                                "drivers": [
                                    {
                                        "firstName": "John",
                                        "lastName": "Doe",
                                        "dateOfBirth": "1985-06-15",
                                        "licenseNumber": "D123456789",
                                        "licenseState": "CA",
                                        "yearsOfExperience": 12,
                                        "safeDriverDiscount": false,
                                        "multiPolicyDiscount": false
                                    }
                                ],
                                "coverageAmount": 200000.00,
                                "deductible": 500.00
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody QuoteRequestDto request) {
        log.info("Calculating premium for quote request");
        
        BigDecimal premium = quoteService.calculatePremium(request);
        
        return ResponseEntity.ok(Map.of("premium", premium));
    }
}