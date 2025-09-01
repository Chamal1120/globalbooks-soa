#!/bin/bash

echo "Stopping existing services..."
PORTS=(8080 8081 8082 8083 8084 8085)
for PORT in ${PORTS[@]}; do
    PID=$(lsof -t -i:$PORT)
    if [ -n "$PID" ]; then
        kill -9 $PID
    fi
done
# Wait a moment for ports to be released
sleep 5

echo "Building all services..."
cd /home/randy99/things/globalbooks/globalbooks-soa && mvn clean install

# Check if build was successful
if [ $? -ne 0 ]; then
  echo "Maven build failed. Aborting service start."
  exit 1
fi

echo "Starting all services..."

mvn spring-boot:run -pl auth-server > /home/randy99/things/globalbooks/globalbooks-soa/logs/auth-server.log 2>&1 &
mvn spring-boot:run -pl orders-service > /home/randy99/things/globalbooks/globalbooks-soa/logs/orders-service.log 2>&1 &
mvn spring-boot:run -pl payments-service > /home/randy99/things/globalbooks/globalbooks-soa/logs/payments-service.log 2>&1 &
mvn spring-boot:run -pl shipping-service > /home/randy99/things/globalbooks/globalbooks-soa/logs/shipping-service.log 2>&1 &
mvn spring-boot:run -pl rest-gateway > /home/randy99/things/globalbooks/globalbooks-soa/logs/rest-gateway.log 2>&1 &
mvn spring-boot:run -pl catalog-service > /home/randy99/things/globalbooks/globalbooks-soa/logs/catalog-service.log 2>&1 &

echo "All services restarted."
sleep 10
