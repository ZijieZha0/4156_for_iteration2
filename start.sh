#!/bin/bash

# NutriFlow Service Startup Script

echo "=========================================="
echo "  Starting NutriFlow Service"
echo "=========================================="
echo ""

# Kill any process using port 8080
echo "Checking for processes on port 8080..."
PORT_PID=$(lsof -ti:8080)
if [ ! -z "$PORT_PID" ]; then
    echo "Found process on port 8080 (PID: $PORT_PID)"
    echo "Killing process..."
    kill -9 $PORT_PID 2>/dev/null
    sleep 2
    echo "[OK] Port 8080 freed"
    echo ""
else
    echo "[OK] Port 8080 is available"
    echo ""
fi

# Check if server is already running
if pgrep -f "spring-boot:run.*nutriflow" > /dev/null; then
    echo "Warning: Spring Boot server is already running!"
    echo ""
    read -p "Stop existing server and restart? (Y/n): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Nn]$ ]]; then
        echo "Stopping existing server..."
        pkill -f "spring-boot:run"
        sleep 3
        echo "[OK] Server stopped"
        echo ""
    else
        echo "Keeping existing server running"
        echo "Server is available at: http://localhost:8080"
        exit 0
    fi
fi

# Check if Java 17 is available
echo "Checking Java version..."
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)

if [ "$JAVA_VERSION" != "17" ]; then
    echo "Warning: Java 17 is required, but Java $JAVA_VERSION is currently active."
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
    echo "[OK] Java 17 detected"
fi

# Check PostgreSQL connection
echo ""
echo "Checking PostgreSQL connection..."
if psql -h localhost -U postgres -d nutriflow -c "SELECT 1;" > /dev/null 2>&1; then
    echo "[OK] PostgreSQL is accessible"
else
    echo "Error: Cannot connect to PostgreSQL database 'nutriflow'"
    echo ""
    echo "Make sure PostgreSQL is running and the database exists."
    echo "You can create it with: psql -U postgres -c 'CREATE DATABASE nutriflow;'"
    exit 1
fi

echo ""
echo "Navigating to nutriflow-service directory..."
cd "$(dirname "$0")/nutriflow-service" || {
    echo "Error: nutriflow-service directory not found"
    exit 1
}

echo "[OK] Directory: $(pwd)"
echo ""
echo "Starting application with local profile..."
echo "Server will be available at: http://localhost:8080"
echo ""
echo "Press Ctrl+C to stop the server"
echo "=========================================="
echo ""

# Start the application with local profile
mvn spring-boot:run -Dspring-boot.run.profiles=local

