#!/bin/bash

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Get the parent directory (project root)
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "Stopping existing services..."
PORTS=(8081 8082 8083 8084 8085 8086)
for PORT in ${PORTS[@]}; do
    PID=$(lsof -t -i:$PORT)
    if [ -n "$PID" ]; then
        kill -9 $PID
    fi
done
# Wait a moment for ports to be released
sleep 5

echo "Building all services..."
cd "$PROJECT_ROOT" && mvn clean compile

# Check if build was successful
if [ $? -ne 0 ]; then
  echo "Maven build failed. Aborting service start."
  exit 1
fi

echo "Starting all services..."

mvn spring-boot:run -pl auth-server > "$PROJECT_ROOT/logs/auth-server.log" 2>&1 &
mvn spring-boot:run -pl orders-service > "$PROJECT_ROOT/logs/orders-service.log" 2>&1 &
mvn spring-boot:run -pl payments-service > "$PROJECT_ROOT/logs/payments-service.log" 2>&1 &
mvn spring-boot:run -pl shipping-service > "$PROJECT_ROOT/logs/shipping-service.log" 2>&1 &
mvn spring-boot:run -pl catalog-service > "$PROJECT_ROOT/logs/catalog-service.log" 2>&1 &
mvn spring-boot:run -pl order-orchestration-service > "$PROJECT_ROOT/logs/orchestration-service.log" 2>&1 &

echo "All services restarted."
sleep 10
