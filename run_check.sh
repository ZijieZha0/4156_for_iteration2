#!/bin/bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"

# Ensure Java 17 is active
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"

echo "Using Java version:"
java -version

cd "$PROJECT_ROOT/nutriflow-service"

echo "Running mvn clean compile..."
mvn -q clean compile

echo "Running Checkstyle..."
mvn -q checkstyle:check

echo "Running PMD..."
mvn -q pmd:pmd

echo "All static checks completed successfully."

