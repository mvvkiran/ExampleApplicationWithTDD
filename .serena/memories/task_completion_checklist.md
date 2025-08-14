# Task Completion Checklist

## When Completing a Task

### 1. Code Quality Checks
- [ ] All tests pass: `mvn test`
- [ ] Code coverage meets requirements (80%+): `mvn clean test jacoco:report`
- [ ] No compilation warnings: `mvn clean compile`

### 2. Testing Requirements
- [ ] Unit tests written for new code
- [ ] Integration tests for API endpoints
- [ ] Contract tests for external integrations
- [ ] Test data added to TestDataFactory if needed

### 3. Documentation
- [ ] API documentation updated (OpenAPI annotations)
- [ ] JavaDoc for public methods
- [ ] README updated if adding new features

### 4. Git Workflow
- [ ] Changes committed with descriptive message
- [ ] Pre-push hooks pass: `./.githooks/pre-push`
- [ ] All tests green before pushing

### 5. Performance Validation
- [ ] Response times within benchmarks (<2s for quotes)
- [ ] No N+1 query issues
- [ ] Caching implemented where appropriate

### 6. Security Checks
- [ ] No hardcoded credentials
- [ ] Input validation in place
- [ ] Authentication/authorization properly configured

## TDD Cycle Reminder
1. 🔴 RED: Write failing test first
2. 🟢 GREEN: Write minimal code to pass
3. 🔵 BLUE: Refactor for quality