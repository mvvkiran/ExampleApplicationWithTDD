# Suggested Commands for Auto Insurance API

## Build and Run Commands
```bash
# Clean and build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev

# Package the application
mvn clean package
```

## Testing Commands
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=QuoteServiceTest

# Run with coverage
mvn clean test jacoco:report

# Run mutation tests
mvn org.pitest:pitest-maven:mutationCoverage

# Run contract tests
./run-contract-tests.sh

# Start WireMock for external service testing
./start-wiremock.sh
```

## Git and Version Control
```bash
# Install git hooks
./.githooks/install-hooks.sh

# Standard git commands
git status
git add .
git commit -m "message"
git push
```

## Development URLs
- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- H2 Console: http://localhost:8080/h2-console
- Actuator Health: http://localhost:8080/actuator/health

## Coverage Requirements
- Unit Tests: Minimum 80% coverage
- Integration Tests: All API endpoints covered
- Contract Tests: All external integrations covered