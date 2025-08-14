#!/bin/bash

# Contract Testing Script for Auto Insurance API
# This script follows TDD principles for contract testing:
# RED: Run tests that define contracts and initially fail
# GREEN: Generate contracts and verify provider compliance  
# BLUE: Generate reports and documentation for contract evolution

set -e

echo "🔴 RED: Running Consumer Contract Tests (Define Contracts)"
echo "============================================================"

# Clean previous test artifacts
mvn clean -q

# Run consumer tests to generate Pact contracts
mvn test -Dtest="**/*ConsumerTest" -DfailIfNoTests=false -q

echo ""
echo "📋 Generated Pact Contracts:"
echo "----------------------------"
if [ -d "target/pacts" ]; then
    ls -la target/pacts/
else
    echo "No pact files generated yet"
fi

echo ""
echo "🟢 GREEN: Verifying Provider Compliance"
echo "======================================="

# Check if we have pact files to verify
if [ -d "target/pacts" ] && [ "$(ls -A target/pacts)" ]; then
    echo "Found Pact contracts, preparing provider verification..."
    
    # Note: In a real scenario, you would start the provider application
    # For this demo, we'll simulate the verification step
    echo "Provider verification would run against: http://localhost:8080"
    echo "Contracts to verify:"
    for pact in target/pacts/*.json; do
        if [ -f "$pact" ]; then
            echo "  - $(basename "$pact")"
        fi
    done
else
    echo "No Pact contracts found to verify"
fi

echo ""
echo "🔵 BLUE: Generating Contract Documentation"
echo "========================================="

# Create contract documentation directory
mkdir -p target/contract-docs

# Generate contract summary
cat > target/contract-docs/CONTRACT-SUMMARY.md << 'EOF'
# Contract Testing Summary

## Overview
This document summarizes the contract testing setup for the Auto Insurance API using Pact.io.

## Consumer Contracts Generated

### Risk Assessment Service Contract
- **Consumer**: auto-insurance-api
- **Provider**: risk-assessment-service
- **Interactions**: Risk score calculation for different driver profiles
- **Scenarios**: Young driver (high risk), experienced driver (low risk), validation errors

### Credit Check Service Contract  
- **Consumer**: auto-insurance-api
- **Provider**: credit-check-service
- **Interactions**: Credit score retrieval for premium discounts
- **Scenarios**: Excellent credit, fair credit, unauthorized access, invalid SSN

## TDD Workflow for Contracts

### RED Phase (Define Contracts)
1. Write consumer tests that define expected provider behavior
2. Tests initially fail as provider doesn't exist or doesn't match expectations
3. Generate Pact JSON files that describe the contracts

### GREEN Phase (Implement Providers)
1. Implement provider APIs to satisfy contract requirements
2. Run provider tests to verify contract compliance
3. Ensure all contract interactions pass verification

### BLUE Phase (Refactor and Evolve)
1. Refactor provider implementations while maintaining contract compliance
2. Evolve contracts by adding new interactions
3. Use contract versioning to manage breaking changes

## Running Contract Tests

```bash
# Run only consumer tests to generate contracts
mvn test -Dtest="**/*ConsumerTest"

# Run full contract test suite
mvn test -Pcontract-tests

# Verify provider compliance (requires running provider)
mvn pact:verify
```

## Contract Evolution

- **Adding new interactions**: Add to consumer tests, regenerate contracts
- **Modifying existing contracts**: Use contract versioning and provider branches
- **Breaking changes**: Coordinate with provider team using Pact Broker

## Best Practices

1. Keep contracts focused on actual usage scenarios
2. Use realistic test data that matches production patterns
3. Version contracts appropriately for breaking changes
4. Integrate contract tests into CI/CD pipeline
5. Use Pact Broker for team collaboration and contract sharing

EOF

echo "Contract documentation generated in: target/contract-docs/"
echo ""

echo "✅ Contract Testing Summary:"
echo "============================"
echo "Consumer Tests: $(ls target/pacts/*.json 2>/dev/null | wc -l) contracts generated"
echo "Documentation: target/contract-docs/CONTRACT-SUMMARY.md"
echo ""
echo "Next Steps:"
echo "- Start provider applications for contract verification"
echo "- Set up Pact Broker for contract sharing"
echo "- Integrate contract tests into CI/CD pipeline"
echo "- Review generated contracts in target/pacts/"