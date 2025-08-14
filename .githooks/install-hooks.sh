#!/bin/bash

# TDD Git Hooks Installation Script
# Installs comprehensive TDD compliance monitoring hooks

set -e

echo "🔴🟢🔵 Installing TDD Compliance Git Hooks"
echo "==========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check if we're in a Git repository
if [ ! -d ".git" ]; then
    echo -e "${RED}❌ Error: Not in a Git repository${NC}"
    echo "Please run this script from the root of your Git repository"
    exit 1
fi

echo -e "${BLUE}📦 Installing TDD compliance hooks...${NC}"

# Create hooks directory if it doesn't exist
mkdir -p .git/hooks

# Install pre-commit hook
echo "  • Installing pre-commit hook..."
cp .githooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

# Install commit-msg hook
echo "  • Installing commit-msg hook..."
cp .githooks/commit-msg .git/hooks/commit-msg
chmod +x .git/hooks/commit-msg

# Install pre-push hook
echo "  • Installing pre-push hook..."
cp .githooks/pre-push .git/hooks/pre-push
chmod +x .git/hooks/pre-push

# Make monitoring script executable
echo "  • Setting up TDD monitoring script..."
chmod +x .githooks/tdd-monitor

echo ""
echo -e "${GREEN}✅ TDD Git Hooks installed successfully!${NC}"
echo ""

echo -e "${BLUE}🔧 Hooks installed:${NC}"
echo "  🔴 pre-commit: Prevents TDD violations before commit"
echo "  🟢 commit-msg: Enforces TDD-friendly commit messages"
echo "  🔵 pre-push: Comprehensive validation before push"
echo "  📊 tdd-monitor: Manual TDD compliance monitoring"
echo ""

echo -e "${BLUE}📋 What these hooks will check:${NC}"
echo ""
echo -e "${RED}🔴 Pre-commit Hook:${NC}"
echo "  • Tests exist for all new code"
echo "  • All tests are passing"
echo "  • Test coverage meets minimum threshold (80%)"
echo "  • Identifies TDD phase (RED/GREEN/BLUE)"
echo ""
echo -e "${GREEN}🟢 Commit Message Hook:${NC}"
echo "  • Enforces TDD-friendly commit message format"
echo "  • Examples: 'test:', 'feat:', 'refactor:', 'fix:'"
echo "  • Provides TDD cycle guidance"
echo ""
echo -e "${BLUE}🔵 Pre-push Hook:${NC}"
echo "  • Full test suite execution"
echo "  • Comprehensive coverage analysis"
echo "  • TDD commit history validation"
echo "  • Build artifact verification"
echo "  • Code quality checks"
echo ""

echo -e "${YELLOW}💡 TDD Commit Message Examples:${NC}"
echo ""
echo "🔴 RED Phase (failing tests):"
echo "  git commit -m \"test: add failing test for premium calculation\""
echo "  git commit -m \"red: failing validation for driver age\""
echo ""
echo "🟢 GREEN Phase (implementation):"
echo "  git commit -m \"feat: implement premium calculation logic\""
echo "  git commit -m \"implement: add driver age validation\""
echo ""
echo "🔵 BLUE Phase (refactoring):"
echo "  git commit -m \"refactor: extract validation service\""
echo "  git commit -m \"blue: optimize calculation algorithm\""
echo ""

echo -e "${BLUE}🚀 Usage:${NC}"
echo "  • Hooks run automatically on git commit/push"
echo "  • Manual monitoring: ./githooks/tdd-monitor"
echo "  • Check last 7 days: ./githooks/tdd-monitor 7"
echo "  • Bypass hook (not recommended): git commit --no-verify"
echo ""

echo -e "${GREEN}🎉 Your project now has comprehensive TDD compliance monitoring!${NC}"
echo -e "${YELLOW}Next: Try making a commit to see the hooks in action${NC}"

# Test hook installation
echo ""
echo -e "${BLUE}🧪 Testing hook installation...${NC}"
if [ -x ".git/hooks/pre-commit" ] && [ -x ".git/hooks/commit-msg" ] && [ -x ".git/hooks/pre-push" ]; then
    echo -e "${GREEN}✅ All hooks installed and executable${NC}"
else
    echo -e "${RED}❌ Hook installation verification failed${NC}"
    exit 1
fi

# Create initial monitoring report
echo ""
echo -e "${BLUE}📊 Generating initial TDD compliance report...${NC}"
if ./.githooks/tdd-monitor 7 > /dev/null 2>&1; then
    echo -e "${GREEN}✅ TDD monitoring system ready${NC}"
else
    echo -e "${YELLOW}⚠️  TDD monitoring report generated with warnings${NC}"
    echo "Check .git/tdd-violations.log for details"
fi

echo ""
echo -e "${BOLD}🎯 TDD Compliance System Ready!${NC}"