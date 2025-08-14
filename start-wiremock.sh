#!/bin/bash

# WireMock Standalone Server Launcher
# Starts virtualized external services for Auto Insurance API development

echo "🚀 Starting WireMock Standalone Server for Auto Insurance API..."
echo ""

# Run WireMock server with proper classpath
mvn exec:java \
  -Dexec.mainClass="com.autoinsurance.wiremock.standalone.WireMockStandaloneServer" \
  -Dexec.classpathScope="test" \
  -q

echo ""
echo "✅ WireMock servers stopped."