# Auto Insurance API - Project Overview

## Purpose
A comprehensive backend API for auto insurance services that provides quote generation, premium calculations, and policy management. Built with strict Test Driven Development (TDD) methodology.

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Maven 3.9+
- **Database**: H2 (development), PostgreSQL (production)
- **Testing**: JUnit 5, Mockito, REST Assured, WireMock, Pact
- **API Documentation**: OpenAPI/Swagger (springdoc-openapi)
- **Security**: Spring Security with JWT and API Key support
- **Performance Testing**: Gatling, k6
- **Mutation Testing**: Pitest

## Architecture
- **Layered Architecture**: Controller → Service → Repository pattern
- **DTO Pattern**: Separate DTOs for request/response handling
- **External Services**: Credit Check and Risk Assessment integrations
- **Caching**: Spring Cache for performance optimization

## Main Features
1. Quote Management (generate, retrieve, calculate)
2. Premium Calculations with risk assessment
3. Discount calculations (safe driver, multi-policy)
4. External service integrations (credit check, risk assessment)
5. Comprehensive validation and error handling