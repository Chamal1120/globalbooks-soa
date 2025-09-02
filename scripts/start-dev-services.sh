#!/bin/bash

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Get the parent directory (project root)
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "Building all services..."
cd "$PROJECT_ROOT" && mvn clean compile

echo "Starting all services..."

mvn spring-boot:run -pl auth-server &
mvn spring-boot:run -pl orders-service &
mvn spring-boot:run -pl payments-service &
mvn spring-boot:run -pl shipping-service &
mvn spring-boot:run -pl catalog-service &
mvn spring-boot:run -pl order-orchestration-service &

echo "All services started."
