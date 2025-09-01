#!/bin/bash

echo "Building all services..."
cd /home/randy99/things/globalbooks/globalbooks-soa && mvn clean install

echo "Starting all services..."

mvn spring-boot:run -pl auth-server &
mvn spring-boot:run -pl orders-service &
mvn spring-boot:run -pl payments-service &
mvn spring-boot:run -pl shipping-service &
mvn spring-boot:run -pl rest-gateway &
mvn spring-boot:run -pl catalog-service &

echo "All services started."
