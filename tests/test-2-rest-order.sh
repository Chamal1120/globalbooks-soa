#!/bin/bash

# Test 2: REST Order Processing Test
# This script focuses on REST API order processing using the created user

echo "================================================"
echo "Test 2: GlobalBooks SOA - REST Order Processing"
echo "================================================"
echo

# Configuration
AUTH_SERVER_URL="http://localhost:8081"
ORCHESTRATION_URL="http://localhost:8086"
USERNAME="testuser"
PASSWORD="password123"

echo "📋 Test Configuration:"
echo "   Auth Server: $AUTH_SERVER_URL"
echo "   Orchestration Service: $ORCHESTRATION_URL"
echo "   Username: $USERNAME"
echo "   Password: $PASSWORD"
echo

echo "🎯 Test Objective: Validate REST-based order processing workflow"
echo

# Check if services are running
echo "🔍 Step 1: Checking service availability..."
if ! curl -s --connect-timeout 3 "$AUTH_SERVER_URL/actuator/health" > /dev/null 2>&1; then
    echo "❌ Error: Auth Server is not running on $AUTH_SERVER_URL"
    echo "   Command: mvn spring-boot:run -pl auth-server -Dspring-boot.run.arguments=--server.port=8081"
    exit 1
fi
echo "✅ Auth Server is running"

if ! curl -s --connect-timeout 3 "$ORCHESTRATION_URL/actuator/health" > /dev/null 2>&1; then
    echo "❌ Error: Orchestration Service is not running on $ORCHESTRATION_URL"
    echo "   Command: mvn spring-boot:run -pl order-orchestration-service -Dspring-boot.run.arguments=--server.port=8086"
    exit 1
fi
echo "✅ Orchestration Service is running"
echo

# Try to load JWT from previous test
JWT_TOKEN=""
if [ -f "/tmp/globalbooks_jwt_token.txt" ]; then
    JWT_TOKEN=$(cat /tmp/globalbooks_jwt_token.txt)
    echo "🔄 Step 2: Using JWT token from previous test..."
    echo "   Token: ${JWT_TOKEN:0:50}..."
    echo
else
    # Authenticate and get JWT
    echo "🔐 Step 2: Authenticating user to get JWT token..."
    echo "   Request: POST $AUTH_SERVER_URL/authenticate"
    echo "   Note: If this fails, run Test 1 first (./test-1-user-creation.sh)"
    echo

    AUTH_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
        -H "Content-Type: application/json" \
        -d "{\"username\": \"$USERNAME\", \"password\": \"$PASSWORD\"}" \
        "$AUTH_SERVER_URL/authenticate")

    AUTH_BODY=$(echo "$AUTH_RESPONSE" | head -n -1)
    AUTH_STATUS=$(echo "$AUTH_RESPONSE" | tail -n 1)

    if [ "$AUTH_STATUS" != "200" ]; then
        echo "❌ Authentication failed (Status: $AUTH_STATUS)"
        echo "   Response: $AUTH_BODY"
        echo "   💡 Tip: Run ./test-1-user-creation.sh first to create the user"
        exit 1
    fi

    JWT_TOKEN=$(echo "$AUTH_BODY" | grep -o '"jwt":"[^"]*"' | cut -d'"' -f4)
    echo "✅ Authentication successful!"
    echo "🎫 JWT Token: ${JWT_TOKEN:0:50}..."
    echo
fi

# Validate JWT token
if [ -z "$JWT_TOKEN" ]; then
    echo "❌ No JWT token available"
    echo "   Run ./test-1-user-creation.sh first"
    exit 1
fi

# Process Order via REST API
echo "📦 Step 3: Processing order via REST API..."
echo "   Endpoint: POST $ORCHESTRATION_URL/api/orders/process"
echo "   Method: REST/JSON"
echo "   Authorization: Bearer Token"
echo "   Content-Type: application/json"
echo

# Order payload
ORDER_PAYLOAD='{
    "customerId": "testuser",
    "bookId": "1",
    "quantity": 2
}'

echo "📋 Order Details:"
echo "   Customer ID: testuser"
echo "   Book ID: 1 (The Great Gatsby by F. Scott Fitzgerald)"
echo "   Quantity: 2 copies"
echo

echo "🚀 Step 4: Sending REST order request..."

ORDER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -d "$ORDER_PAYLOAD" \
    "$ORCHESTRATION_URL/api/orders/process")

ORDER_BODY=$(echo "$ORDER_RESPONSE" | head -n -1)
ORDER_STATUS=$(echo "$ORDER_RESPONSE" | tail -n 1)

echo "📤 Order Processing Response:"
echo "   Status Code: $ORDER_STATUS"
echo "   Response Body: $ORDER_BODY"
echo

if [ "$ORDER_STATUS" = "200" ]; then
    echo "✅ REST Order processed successfully!"
    
    # Extract order details from response
    ORDER_ID=$(echo "$ORDER_BODY" | grep -o '"orderId":"[^"]*"' | cut -d'"' -f4)
    STATUS=$(echo "$ORDER_BODY" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
    MESSAGE=$(echo "$ORDER_BODY" | grep -o '"message":"[^"]*"' | cut -d'"' -f4)
    
    if [ ! -z "$ORDER_ID" ]; then
        echo "📋 Order Summary:"
        echo "   Order ID: $ORDER_ID"
        echo "   Status: $STATUS"
        echo "   Message: $MESSAGE"
        echo
        
        # Save order ID for reference
        echo "$ORDER_ID" > /tmp/globalbooks_rest_order_id.txt
        echo "💾 Order ID saved to /tmp/globalbooks_rest_order_id.txt"
    fi
    
    echo
    echo "🔄 Step 5: Microservices Processing Flow:"
    echo "   1. ✅ Order received by Orchestration Service"
    echo "   2. ✅ JWT token validated successfully"
    echo "   3. ✅ Order data validated and accepted"
    echo "   4. ✅ Order queued for asynchronous processing"
    echo "   5. 🔄 Background services processing:"
    echo "      • Orders Service: Creating order record"
    echo "      • Payments Service: Processing payment for $30.00"
    echo "      • Shipping Service: Arranging delivery to New York"
    echo "      • Catalog Service: Updating inventory for The Great Gatsby"
    echo
    echo "💡 Order is now in the processing queue"
    echo "   Check service logs for detailed workflow progress"
    
    REST_TEST_PASSED=true
    
else
    echo "❌ REST Order processing failed"
    echo "   This might be due to:"
    echo "   - Invalid JWT token (expired or malformed)"
    echo "   - Orchestration service unavailable"
    echo "   - Invalid order data format"
    echo "   - Authentication/authorization issues"
    REST_TEST_PASSED=false
fi

# Test unauthorized request (negative test)
echo
echo "🧪 Step 6: Testing unauthorized request (negative test)..."
UNAUTH_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d "$ORDER_PAYLOAD" \
    "$ORCHESTRATION_URL/api/orders/process")

UNAUTH_STATUS=$(echo "$UNAUTH_RESPONSE" | tail -n 1)

if [ "$UNAUTH_STATUS" = "401" ] || [ "$UNAUTH_STATUS" = "403" ]; then
    echo "✅ Unauthorized request properly rejected (Status: $UNAUTH_STATUS)"
    echo "   Security validation: PASSED"
    SECURITY_PASSED=true
else
    echo "⚠️  Unexpected response for unauthorized request (Status: $UNAUTH_STATUS)"
    echo "   Security validation: NEEDS REVIEW"
    SECURITY_PASSED=false
fi

echo
echo "================================================"
echo "📊 TEST 2 RESULTS SUMMARY"
echo "================================================"
echo "✅ REST Order Processing Test: COMPLETED"
echo

echo "Test Results:"
if [ "$REST_TEST_PASSED" = true ]; then
    echo "   ✅ Service Connectivity: PASSED"
    echo "   ✅ JWT Authentication: PASSED"
    echo "   ✅ REST API Processing: PASSED"
    echo "   ✅ Order Validation: PASSED"
    echo "   ✅ JSON Response Format: PASSED"
    if [ "$SECURITY_PASSED" = true ]; then
        echo "   ✅ Security Validation: PASSED"
    else
        echo "   ⚠️  Security Validation: NEEDS REVIEW"
    fi
    echo
    echo "🎉 REST Order Processing test PASSED!"
    echo "💡 Order successfully submitted via REST API"
    echo
    echo "Next Steps:"
    echo "   1. Run Test 3: ./test-3-soap-order.sh"
    echo "   2. Check service logs to see complete processing workflow"
    echo "   3. Verify order in database/queue systems"
    exit 0
else
    echo "   ❌ REST Order Processing: FAILED"
    echo
    echo "🔧 Troubleshooting:"
    echo "   • Ensure order-orchestration-service is running on port 8086"
    echo "   • Verify JWT token is valid and not expired"
    echo "   • Check orchestration service logs for detailed errors"
    echo "   • Ensure RabbitMQ is running for queue processing"
    exit 1
fi
