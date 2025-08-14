# 🔴🟢🔵 TDD Git Hooks - Complete Guide

> **Comprehensive TDD compliance monitoring and alerting system**

## 🚀 Overview

Your Auto Insurance API now has **comprehensive Git hooks** that monitor and enforce Test Driven Development (TDD) compliance. These hooks will **alert you immediately** when there are deviations from TDD best practices.

## 🔧 Installed Hooks

### 🔴 Pre-commit Hook
**Runs:** Before every `git commit`
**Purpose:** Prevents commits that violate TDD principles

**What it checks:**
- ✅ Tests exist for all new/modified code
- ✅ All tests are currently passing
- ✅ Test coverage meets minimum threshold (80%)
- ✅ Identifies current TDD phase (RED/GREEN/BLUE)

### 🟢 Commit Message Hook
**Runs:** During `git commit` (validates message)
**Purpose:** Enforces TDD-friendly commit message patterns

**Valid commit message formats:**
```bash
# RED Phase (failing tests first)
test: add failing test for premium calculation
red: failing validation for driver age
failing: test case for invalid VIN format

# GREEN Phase (implementation)
feat: implement premium calculation logic
implement: add driver age validation
green: make VIN validation tests pass

# BLUE Phase (refactoring)
refactor: extract quote validation service
blue: optimize premium calculation algorithm
cleanup: simplify quote builder pattern

# Other allowed formats
fix: resolve premium calculation bug
docs: update API documentation
chore: update Maven dependencies
```

### 🔵 Pre-push Hook
**Runs:** Before every `git push`
**Purpose:** Comprehensive validation before sharing code

**What it checks:**
- ✅ Full test suite execution
- ✅ Complete coverage analysis
- ✅ TDD commit history validation
- ✅ Build artifact verification
- ✅ Code quality checks

## 📊 TDD Monitoring System

### Manual Monitoring
```bash
# Check TDD compliance for last 7 days
./.githooks/tdd-monitor

# Check specific time period
./.githooks/tdd-monitor 14  # Last 14 days
```

### Automated Alerts
The system automatically logs violations to `.git/tdd-violations.log` and provides:
- 📈 Commit pattern analysis
- 📊 Test coverage trends
- 🚨 TDD anti-pattern detection
- 📋 Compliance summary reports

## 🚨 Alert Examples

### Pre-commit Alerts
```bash
❌ TDD VIOLATION: Code committed without corresponding tests!
Missing test files:
  - src/main/java/NewService.java -> src/test/java/NewServiceTest.java

❌ TDD VIOLATION: Tests are failing!
Please fix failing tests before committing.
```

### Commit Message Alerts
```bash
❌ Invalid commit message format!

TDD-friendly commit message formats:
🔴 RED Phase: test: add failing test for feature
🟢 GREEN Phase: feat: implement feature logic  
🔵 BLUE Phase: refactor: improve code structure
```

### Pre-push Alerts
```bash
❌ TDD compliance violations detected!
Found 3 violation(s) that must be fixed

Before pushing, please:
1. Fix all failing tests
2. Ensure test coverage >= 80%
3. Verify build success
4. Follow TDD commit message patterns
```

## 📈 Monitoring Reports

The TDD monitoring system provides detailed reports:

```bash
🔴🟢🔵 TDD Compliance Monitoring Report
==========================================
Analyzing TDD compliance for the last 7 days

📊 TDD Commit Pattern Analysis
Commit Distribution:
🔴 RED (tests): 5 (25%)
🟢 GREEN (implementation): 8 (40%) 
🔵 BLUE (refactoring): 4 (20%)
⚪ Other: 3 (15%)

📈 Current Test Coverage Analysis
Coverage Details:
📊 Line Coverage: 85% (340/400 lines)
🌳 Branch Coverage: 78% (156/200 branches)
✅ Coverage meets requirement (>= 80%)

📉 Test Trend Analysis
Current Test Metrics:
🧪 Test Files: 23
📄 Implementation Files: 27
🎯 Test-to-Implementation Ratio: 85%
✅ Good test coverage ratio
```

## ⚙️ Configuration

Customize hook behavior in `.githooks/tdd-config.conf`:

```bash
# Test Coverage Requirements
MIN_TEST_COVERAGE=80
COVERAGE_FAIL_BUILD=true

# Commit Message Patterns
ENABLE_TDD_COMMIT_PATTERNS=true
REQUIRE_TDD_PREFIXES=true

# Code Quality Checks
MAX_FILE_LINES=150
CHECK_TODO_COMMENTS=true
```

## 🛠️ Usage Examples

### Typical TDD Workflow
```bash
# 1. RED Phase - Write failing test
git add src/test/java/NewFeatureTest.java
git commit -m "test: add failing test for new feature"

# 2. GREEN Phase - Implement minimal code
git add src/main/java/NewFeature.java src/test/java/NewFeatureTest.java
git commit -m "feat: implement new feature to pass tests"

# 3. BLUE Phase - Refactor
git add src/main/java/NewFeature.java
git commit -m "refactor: extract common logic to utility class"

# 4. Push when ready
git push origin feature-branch
```

### Hook Bypass (Not Recommended)
```bash
# Skip pre-commit hook (DANGEROUS)
git commit --no-verify -m "emergency fix"

# Skip pre-push hook (DANGEROUS)  
git push --no-verify
```

## 🔧 Troubleshooting

### Hook Not Running
```bash
# Reinstall hooks
./.githooks/install-hooks.sh

# Check hook permissions
ls -la .git/hooks/
```

### Coverage Issues
```bash
# Generate coverage report manually
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Failures
```bash
# Run tests with detailed output
mvn test

# Run specific test class
mvn test -Dtest=QuoteServiceTest
```

## 📊 TDD Compliance Metrics

The system tracks and reports on:

### Coverage Metrics
- **Line Coverage**: % of code lines executed by tests
- **Branch Coverage**: % of decision branches tested
- **Method Coverage**: % of methods with test coverage

### TDD Flow Metrics  
- **RED Commits**: Tests written first
- **GREEN Commits**: Implementation commits
- **BLUE Commits**: Refactoring commits
- **Cycle Balance**: Healthy RED→GREEN→BLUE flow

### Quality Metrics
- **Test-to-Code Ratio**: Number of test files vs implementation files
- **Assertion Density**: Average assertions per test method
- **Anti-pattern Detection**: Large methods, missing tests, etc.

## 🎯 Best Practices

### Commit Message Best Practices
1. **Use TDD prefixes**: `test:`, `feat:`, `refactor:`
2. **Be descriptive**: Explain what the change does
3. **Keep it concise**: One line summary is best
4. **Follow the cycle**: RED → GREEN → BLUE pattern

### TDD Workflow Best Practices
1. **RED First**: Always write failing tests before implementation
2. **Minimal GREEN**: Write just enough code to pass tests
3. **Regular BLUE**: Refactor frequently while tests are green
4. **Commit Often**: Small, focused commits with clear TDD phases

### Monitoring Best Practices
1. **Check Reports**: Run `./githooks/tdd-monitor` regularly
2. **Address Violations**: Fix TDD violations immediately
3. **Team Reviews**: Share monitoring reports in team meetings
4. **Continuous Improvement**: Adjust thresholds as team matures

## 🚨 Emergency Procedures

### Bypassing Hooks (Emergency Only)
```bash
# If absolutely necessary (with good reason)
git commit --no-verify -m "urgent: fix production issue"

# Document the bypass reason
echo "Emergency bypass: Production hotfix" >> .git/tdd-violations.log
```

### Restoring TDD Compliance
```bash
# After emergency bypass, restore compliance:
1. Add tests for the emergency fix
2. Ensure all tests pass
3. Run full monitoring report
4. Address any violations found
```

## 📞 Support

### Common Issues
- **Hooks not running**: Check `.git/hooks/` permissions
- **Coverage too low**: Add more test cases
- **Build failures**: Fix compilation errors first
- **Message format**: Follow TDD commit patterns

### Getting Help
```bash
# View hook help
./.githooks/pre-commit --help
./.githooks/commit-msg --help
./.githooks/pre-push --help

# Generate monitoring report
./.githooks/tdd-monitor
```

## 🎉 Success Indicators

When the TDD hooks are working properly, you'll see:
- ✅ All commits follow TDD patterns
- ✅ Test coverage consistently above 80%
- ✅ Balanced RED/GREEN/BLUE commit distribution
- ✅ No anti-patterns detected
- ✅ Clean monitoring reports

**Your Auto Insurance API now has comprehensive TDD compliance monitoring that will alert you immediately when deviations occur!** 🚀

---

*For more information about TDD best practices, see the main [README.md](README.md)*