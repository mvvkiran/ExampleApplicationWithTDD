# WireMock Service Virtualization Setup

## Overview

This project includes comprehensive WireMock service virtualization for all external API dependencies. This allows development and testing without requiring actual external services.

## External Services Virtualized

### 1. Risk Assessment Service
- **Port:** 9001
- **Endpoint:** `POST http://localhost:9001/api/v1/risk-assessment`
- **Purpose:** Provides risk scoring for insurance quotes

### 2. Credit Check Service  
- **Port:** 9002
- **Endpoint:** `POST http://localhost:9002/api/v1/credit-check`
- **Purpose:** Provides credit scoring with authentication

## Starting WireMock Services

### Option 1: Use the convenience script
```bash
./start-wiremock.sh
```

### Option 2: Use Maven directly
```bash
mvn exec:java -Dexec.mainClass="com.autoinsurance.wiremock.standalone.WireMockStandaloneServer" -Dexec.classpathScope="test"
```

## Application Configuration

When WireMock servers are running, configure your application to use them:

```yaml
# application-wiremock.yml
external:
  risk-assessment:
    base-url: http://localhost:9001
  credit-check:
    base-url: http://localhost:9002
    api-key: test-token
```

## Testing

### Integration Tests
Run WireMock integration tests:
```bash
mvn test -Dtest="com.autoinsurance.wiremock.*IntegrationTest" -Djacoco.skip=true
```

### Stubbed Scenarios

#### Risk Assessment
- High risk young driver (age 22)
- Low risk experienced driver (age 35+)
- Validation errors (age < 18)
- Default medium risk scenarios

#### Credit Check  
- Excellent credit (785+ score, 15% discount)
- Good credit (700-784 score, 10% discount)
- Fair credit (620-699 score, 0% discount)
- Invalid SSN format errors
- Unauthorized access errors

## Development Workflow

1. **Start WireMock:** `./start-wiremock.sh`
2. **Configure Application:** Use `application-wiremock.yml` profile
3. **Develop:** All external API calls go to WireMock
4. **Test:** Run integration tests against WireMock
5. **Stop:** Ctrl+C to stop WireMock servers

## Benefits

- ✅ **No External Dependencies:** Develop without internet/VPN
- ✅ **Predictable Responses:** Consistent test data
- ✅ **Error Testing:** Simulate service failures
- ✅ **Fast Development:** No network latency
- ✅ **Contract Testing:** Maintains API compatibility

## Files Structure

```
src/test/java/com/autoinsurance/wiremock/
├── WireMockTestConfig.java         # Configuration
├── WireMockBaseTest.java           # Base test class
├── stubs/
│   ├── RiskAssessmentStubs.java    # Risk assessment scenarios
│   └── CreditCheckStubs.java       # Credit check scenarios
├── standalone/
│   └── WireMockStandaloneServer.java # Development server
└── *IntegrationTest.java           # Integration tests
```

## TDD Red-Green-Blue Integration

All WireMock stubs follow TDD principles:
- **RED:** Tests fail without proper stubs
- **GREEN:** Stubs provide expected responses  
- **BLUE:** Refactor while maintaining contracts