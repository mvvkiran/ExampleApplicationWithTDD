# Code Style and Conventions

## Java Code Style
- **Java Version**: 17
- **Indentation**: 4 spaces (standard Java)
- **Naming Conventions**:
  - Classes: PascalCase (e.g., QuoteService, DriverDto)
  - Methods: camelCase (e.g., calculatePremium, generateQuote)
  - Constants: UPPER_SNAKE_CASE (e.g., MAX_COVERAGE_AMOUNT)
  - Packages: lowercase (e.g., com.autoinsurance.quote)

## Spring Boot Patterns
- **Annotations**: Use appropriate Spring annotations (@Service, @RestController, @Repository)
- **Dependency Injection**: Constructor-based injection preferred
- **DTOs**: Separate request/response DTOs with validation annotations
- **Builders**: Lombok @Builder pattern for entity/DTO construction

## Testing Conventions
- **Test Class Naming**: ClassNameTest (e.g., QuoteServiceTest)
- **Test Method Naming**: should_expectedBehavior_when_condition
- **Test Organization**: Nested classes for grouping related tests
- **Assertions**: Use AssertJ for fluent assertions
- **Test Data**: Use TestDataFactory for consistent test data generation

## Documentation
- **JavaDoc**: For public APIs and complex methods
- **OpenAPI**: Annotations for API documentation
- **Comments**: Focus on "why" not "what"

## Error Handling
- **Custom Exceptions**: Domain-specific exceptions (e.g., InvalidQuoteRequestException)
- **Global Exception Handler**: Centralized error handling
- **Validation**: Bean validation with @Valid annotations