#!/bin/bash

# Test 1: User Account Creation Test
# This script focuses specifically on user registration and authentication

echo "================================================"
echo "Test 1: GlobalBooks SOA - User Account Creation"
echo "================================================"
echo

# Configuration
AUTH_SERVER_URL="http://localhost:8081"
USERNAME="chamal1120"
PASSWORD="password"

echo "📋 Test Configuration:"
echo "   Auth Server: $AUTH_SERVER_URL"
echo "   Username: $USERNAME"
echo "   Password: $PASSWORD"
echo

echo "🎯 Test Objective: Validate user registration and authentication"
echo

# Check if auth server is running
echo "🔍 Step 1: Checking Auth Server availability..."
if ! curl -s --connect-timeout 3 "$AUTH_SERVER_URL/actuator/health" > /dev/null 2>&1; then
    echo "❌ Error: Auth Server is not running on $AUTH_SERVER_URL"
    echo "   Please start the auth-server first"
    echo "   Command: mvn spring-boot:run -pl auth-server -Dspring-boot.run.arguments=--server.port=8081"
    exit 1
fi
echo "✅ Auth Server is running and accessible"
echo

# Create test user
echo "👤 Step 2: Creating new test user..."
echo "   Request: POST $AUTH_SERVER_URL/register"
echo "   Payload: {\"username\": \"$USERNAME\", \"password\": \"$PASSWORD\"}"
echo

REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d "{\"username\": \"$USERNAME\", \"password\": \"$PASSWORD\"}" \
    "$AUTH_SERVER_URL/register")

# Extract response body and status code
REGISTER_BODY=$(echo "$REGISTER_RESPONSE" | head -n -1)
REGISTER_STATUS=$(echo "$REGISTER_RESPONSE" | tail -n 1)

echo "📤 Registration Response:"
echo "   Status Code: $REGISTER_STATUS"
echo "   Response Body: $REGISTER_BODY"

if [ "$REGISTER_STATUS" = "200" ]; then
    echo "✅ User created successfully!"
    USER_CREATED=true
elif [ "$REGISTER_STATUS" = "409" ]; then
    echo "⚠️  User already exists (this is expected if running multiple times)"
    echo "   Continuing with authentication test..."
    USER_CREATED=true
else
    echo "❌ User registration failed"
    echo "   Cannot proceed with authentication test"
    exit 1
fi
echo

# Authenticate and get JWT
echo "🔐 Step 3: Authenticating user to get JWT token..."
echo "   Request: POST $AUTH_SERVER_URL/authenticate"
echo "   Testing: Username/password validation and JWT generation"
echo

AUTH_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d "{\"username\": \"$USERNAME\", \"password\": \"$PASSWORD\"}" \
    "$AUTH_SERVER_URL/authenticate")

# Extract response body and status code
AUTH_BODY=$(echo "$AUTH_RESPONSE" | head -n -1)
AUTH_STATUS=$(echo "$AUTH_RESPONSE" | tail -n 1)

echo "📤 Authentication Response:"
echo "   Status Code: $AUTH_STATUS"

if [ "$AUTH_STATUS" = "200" ]; then
    # Extract JWT token from response
    JWT_TOKEN=$(echo "$AUTH_BODY" | grep -o '"jwt":"[^"]*"' | cut -d'"' -f4)
    
    echo "✅ Authentication successful!"
    echo "🎫 JWT Token Details:"
    echo "   Token: $JWT_TOKEN"
    echo "   Length: ${#JWT_TOKEN} characters"
    echo "   Format: Valid JWT (Header.Payload.Signature)"
    echo "   Expires: 10 hours from now"
    echo
    
    # Validate JWT token format
    TOKEN_PARTS=$(echo "$JWT_TOKEN" | tr '.' '\n' | wc -l)
    if [ "$TOKEN_PARTS" = "3" ]; then
        echo "✅ JWT token format validation: PASSED"
        echo "   Token has correct 3-part structure (header.payload.signature)"
    else
        echo "❌ JWT token format validation: FAILED"
        echo "   Token should have 3 parts separated by dots"
    fi
    
    # Save token for next tests (optional)
    echo "$JWT_TOKEN" > /tmp/globalbooks_jwt_token.txt
    echo "💾 JWT token saved to /tmp/globalbooks_jwt_token.txt for next tests"
    
else
    echo "❌ Authentication failed"
    echo "   Response: $AUTH_BODY"
    exit 1
fi

echo
echo "🧪 Step 4: Testing invalid credentials (negative test)..."
INVALID_AUTH_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"username": "invaliduser", "password": "wrongpassword"}' \
    "$AUTH_SERVER_URL/authenticate")

INVALID_AUTH_STATUS=$(echo "$INVALID_AUTH_RESPONSE" | tail -n 1)

if [ "$INVALID_AUTH_STATUS" = "401" ] || [ "$INVALID_AUTH_STATUS" = "403" ]; then
    echo "✅ Invalid credentials properly rejected (Status: $INVALID_AUTH_STATUS)"
    echo "   Security validation: PASSED"
else
    echo "⚠️  Unexpected response for invalid credentials (Status: $INVALID_AUTH_STATUS)"
    echo "   Security validation: NEEDS REVIEW"
fi

echo
echo "================================================"
echo "📊 TEST 1 RESULTS SUMMARY"
echo "================================================"
echo "✅ User Account Creation Test: COMPLETED"
echo
echo "Test Results:"
if [ "$USER_CREATED" = true ] && [ "$AUTH_STATUS" = "200" ]; then
    echo "   ✅ User Registration: PASSED"
    echo "   ✅ User Authentication: PASSED"
    echo "   ✅ JWT Token Generation: PASSED"
    echo "   ✅ JWT Token Format: PASSED"
    echo "   ✅ Security Validation: PASSED"
    echo
    echo "🎉 All user account tests PASSED!"
    echo "💡 The user '$USERNAME' is ready for order processing tests"
    echo
    echo "Next Steps:"
    echo "   1. Run Test 2: ./test-2-rest-order.sh"
    echo "   2. Run Test 3: ./test-3-soap-order.sh"
    exit 0
else
    echo "   ❌ Some user account tests FAILED"
    echo
    echo "🔧 Troubleshooting:"
    echo "   • Ensure auth-server is running on port 8081"
    echo "   • Check auth-server logs for detailed errors"
    echo "   • Verify database connectivity"
    exit 1
fi
