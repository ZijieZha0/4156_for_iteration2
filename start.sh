#!/bin/bash

# NutriFlow Service Startup Script

echo "=========================================="
echo "  Starting NutriFlow Service"
echo "=========================================="
echo ""

# Check if Java 17 is available
echo "Checking Java version..."
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)

if [ "$JAVA_VERSION" != "17" ]; then
    echo "⚠️  Warning: Java 17 is required, but Java $JAVA_VERSION is currently active."
    echo ""
    echo "To switch to Java 17, run:"
    echo "  export JAVA_HOME=\$(/usr/libexec/java_home -v 17)"
    echo "  export PATH=\"\$JAVA_HOME/bin:\$PATH\""
    echo ""
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
else
    echo "✓ Java 17 detected"
fi

echo ""
echo "Navigating to nutriflow-service directory..."
cd "$(dirname "$0")/nutriflow-service" || {
    echo "❌ Error: nutriflow-service directory not found"
    exit 1
}

echo "✓ Directory: $(pwd)"
echo ""
echo "Starting application with local profile..."
echo "Server will be available at: http://localhost:8080"
echo ""
echo "Press Ctrl+C to stop the server"
echo "=========================================="
echo ""

# Start the application with local profile
mvn spring-boot:run -Dspring-boot.run.profiles=local

