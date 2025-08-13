# Auto Insurance API

> **Comprehensive Auto Insurance Backend API built with Test Driven Development (TDD)**

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green.svg)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)
![Tests](https://img.shields.io/badge/Tests-23%20Passing-brightgreen.svg)
![Coverage](https://img.shields.io/badge/Coverage-80%25+-success.svg)
![TDD](https://img.shields.io/badge/TDD-Red%20Green%20Blue-red.svg)

## 🚗 Overview

The Auto Insurance API provides comprehensive endpoints for managing insurance quotes, premium calculations, and policy information. Built following strict Test Driven Development (TDD) methodology with extensive OpenAPI/Swagger documentation.

### 🔴🟢🔵 TDD Methodology

This API follows the **Red-Green-Blue** cycle:

- **🔴 Red Phase**: Write failing tests first
- **🟢 Green Phase**: Implement minimum code to pass tests  
- **🔵 Blue Phase**: Refactor for quality and performance

## ✨ Features

- **📊 Quote Management**: Generate, retrieve, and calculate insurance quotes
- **💰 Premium Calculations**: Advanced risk assessment with discount calculations
- **🔒 Security**: JWT and API Key authentication support
- **⚡ Performance**: Spring Cache integration for optimal response times
- **📝 Documentation**: Complete OpenAPI/Swagger specifications
- **🧪 Testing**: Comprehensive test suite with 80%+ coverage

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.9+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ExampleApplicationWithTDD
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the API**
   - **Application**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui/index.html
   - **Health Check**: http://localhost:8080/actuator/health

## 📚 API Documentation

### Swagger UI (Interactive)

Access the complete interactive API documentation at:
**http://localhost:8080/swagger-ui/index.html**

### Available Endpoints

#### Quote Management APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/quotes` | Generate comprehensive insurance quote |
| `GET` | `/api/v1/quotes/{id}` | Retrieve quote by ID (cached) |
| `POST` | `/api/v1/quotes/calculate` | Calculate premium without saving |

### API Examples

#### Generate Insurance Quote

```bash
curl -X POST "http://localhost:8080/api/v1/quotes" \
  -H "Content-Type: application/json" \
  -d '{
    "vehicle": {
      "make": "Honda",
      "model": "Accord", 
      "year": 2021,
      "vin": "1HGCV1F31JA123456",
      "currentValue": 30000.00
    },
    "drivers": [{
      "firstName": "Jane",
      "lastName": "Smith",
      "dateOfBirth": "1990-03-20",
      "licenseNumber": "S987654321",
      "licenseState": "NY",
      "yearsOfExperience": 10,
      "safeDriverDiscount": true,
      "multiPolicyDiscount": true
    }],
    "coverageAmount": 150000.00,
    "deductible": 1000.00
  }'
```

#### Response

```json
{
  "quoteId": "q-12345678-abcd-4567-89ef-123456789012",
  "premium": 1200.00,
  "monthlyPremium": 100.00,
  "coverageAmount": 150000.00,
  "deductible": 1000.00,
  "validUntil": "2024-09-15",
  "discountsApplied": [
    "Safe Driver Discount - 15%",
    "Multi-Policy Discount - 10%"
  ]
}
```

## 🏗️ Architecture

### Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: H2 (in-memory for development)
- **ORM**: Hibernate/JPA
- **Testing**: JUnit 5, Mockito
- **Documentation**: SpringDoc OpenAPI 2.2.0
- **Security**: Spring Security
- **Caching**: Spring Cache

### Project Structure

```
src/
├── main/
│   ├── java/com/autoinsurance/
│   │   ├── config/              # Configuration classes
│   │   │   ├── CacheConfig.java
│   │   │   ├── OpenApiConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── SwaggerConfig.java
│   │   ├── quote/               # Quote management domain
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── repository/     # Data repositories
│   │   │   └── service/        # Business logic
│   │   └── exception/          # Global exception handling
│   └── resources/
│       ├── application.yml     # Application configuration
│       └── CLAUDE.md          # Project-specific instructions
└── test/                      # Test classes (Unit & Integration)
```

## 🧪 Testing

### Test Coverage

- **Unit Tests**: 23 tests covering service logic and business rules
- **Integration Tests**: 10 tests covering complete API workflows  
- **Controller Tests**: HTTP flow testing with MockMvc
- **Coverage**: 80%+ code coverage with JaCoCo

### Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Categories

1. **QuoteServiceTest** (13 tests)
   - Premium calculations
   - Discount applications
   - Validation logic
   - Error scenarios

2. **QuoteControllerTest** (10 tests)
   - HTTP request/response handling
   - Status code validation
   - JSON serialization/deserialization

## 🔧 Configuration

### Application Properties

Key configuration in `application.yml`:

```yaml
# Server Configuration
server:
  port: 8080

# Database Configuration
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: 

# Swagger Configuration
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tryItOutEnabled: true

# Insurance Business Rules
insurance:
  quote:
    min-age-driver: 18
    max-age-driver: 85
    base-premium: 500.00
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | JWT signing secret | Auto-generated |
| `SERVER_PORT` | Application port | 8080 |

## 🚨 Data Validation

### Vehicle Information
- **Make/Model**: Required, non-empty strings
- **Year**: 1900-2024 range
- **VIN**: Must match pattern `^[A-HJ-NPR-Z0-9]{17}$`
- **Current Value**: Must be positive

### Driver Information
- **Name**: Required, non-empty strings
- **Date of Birth**: Must be in the past
- **Age**: 18-85 years old
- **License**: Required number and 2-character state code
- **Experience**: Non-negative integer

### Coverage Information
- **Coverage Amount**: $25,000 - $1,000,000
- **Deductible**: $250 - $10,000

## 📊 Performance Features

### Caching Strategy
- Quote retrieval results cached for optimal performance
- Premium calculations cached for repeated requests
- Validation patterns cached for efficiency

### Response Times
- Quote Generation: < 2 seconds
- Quote Retrieval: < 500ms (cached)
- Premium Calculation: < 1 second

## 🔐 Security

### Authentication Methods

1. **JWT Bearer Token**
   ```
   Authorization: Bearer <your-jwt-token>
   ```

2. **API Key**
   ```
   X-API-Key: <your-api-key>
   ```

### Security Features
- CSRF protection disabled for API usage
- CORS configuration for cross-origin requests
- Input validation and sanitization
- Error message sanitization

## 🐛 Error Handling

### HTTP Status Codes
- `200 OK`: Successful request
- `201 Created`: Quote successfully generated
- `400 Bad Request`: Validation errors
- `401 Unauthorized`: Authentication required
- `404 Not Found`: Quote not found
- `500 Internal Server Error`: System error

### Error Response Format
```json
{
  "message": "Driver must be at least 18 years old. Current age: 17 years",
  "timestamp": "2024-08-15T10:30:00Z"
}
```

## 🚀 Deployment

### Local Development

1. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

2. **Build JAR file**
   ```bash
   mvn clean package
   java -jar target/auto-insurance-0.0.1-SNAPSHOT.jar
   ```

### Docker Support (Future Enhancement)

```dockerfile
# Planned Docker configuration
FROM openjdk:17-jdk-slim
COPY target/auto-insurance-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 🎯 Roadmap

### Phase 1: Quote Management ✅
- [x] Generate insurance quotes
- [x] Retrieve quotes by ID
- [x] Premium calculations
- [x] Discount applications
- [x] Comprehensive testing
- [x] API documentation

### Phase 2: Policy Management (Planned)
- [ ] Policy creation from quotes
- [ ] Policy lifecycle management
- [ ] Policy modifications
- [ ] Renewal processing

### Phase 3: Claims Processing (Planned)
- [ ] Claim submission
- [ ] Document management
- [ ] Adjuster assignment
- [ ] Settlement calculations

### Phase 4: Customer Management (Planned)
- [ ] Customer profiles
- [ ] Document verification
- [ ] Payment processing
- [ ] Communication preferences

## 🤝 Contributing

### Development Workflow

1. **Follow TDD Approach**
   ```bash
   # 1. Write failing test (Red)
   # 2. Implement minimum code (Green)
   # 3. Refactor for quality (Blue)
   ```

2. **Code Standards**
   - Use 2 spaces for indentation
   - Follow existing naming conventions
   - Add comprehensive tests for new features
   - Update documentation

3. **Commit Guidelines**
   ```
   feat: add new quote calculation feature
   fix: resolve premium calculation bug
   test: add integration tests for quote API
   docs: update API documentation
   ```

### Testing Requirements

- All new code must have 80%+ test coverage
- Integration tests for new API endpoints
- Unit tests for business logic
- Mock external dependencies

## 📞 Support

### Development Tools

- **H2 Console**: http://localhost:8080/h2-console
- **Actuator Health**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

### Common Issues

1. **Port 8080 already in use**
   ```bash
   lsof -ti:8080 | xargs kill -9
   ```

2. **Tests failing**
   ```bash
   mvn clean test -X  # Debug mode
   ```

3. **Swagger UI not loading**
   - Check http://localhost:8080/swagger-ui/index.html
   - Verify SpringDoc configuration

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built with **Test Driven Development** best practices
- Powered by **Spring Boot** ecosystem
- Interactive documentation with **SpringDoc OpenAPI**
- Comprehensive testing with **JUnit 5** and **Mockito**

---

**🚀 Ready to start? Run `mvn spring-boot:run` and visit http://localhost:8080/swagger-ui/index.html**