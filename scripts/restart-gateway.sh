#!/bin/bash

echo "Stopping existing rest-gateway service..."
PORT=8080
PID=$(lsof -t -i:$PORT)
if [ -n "$PID" ]; then
    kill -9 $PID
fi
sleep 2

echo "Building rest-gateway service..."
cd /home/randy99/things/globalbooks/globalbooks-soa && mvn -pl rest-gateway clean install

if [ $? -ne 0 ]; then
  echo "Maven build failed. Aborting service start."
  exit 1
fi

echo "Starting rest-gateway service..."
mvn spring-boot:run -pl rest-gateway > /home/randy99/things/globalbooks/globalbooks-soa/logs/rest-gateway.log 2>&1 &

echo "rest-gateway service restarted."
sleep 5
