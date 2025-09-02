#!/bin/bash

# Master Test Script for GlobalBooks SOA System
# This script runs all three test scenarios in sequence

echo "================================================"
echo "GlobalBooks SOA - Complete System Test Suite"
echo "================================================"
echo

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "📁 Test Suite Location: $SCRIPT_DIR"
echo "⏰ Test Started: $(date)"
echo

# Check if all scripts exist
if [ ! -f "$SCRIPT_DIR/test-1-user-creation.sh" ]; then
    echo "❌ Error: test-1-user-creation.sh not found"
    exit 1
fi

if [ ! -f "$SCRIPT_DIR/test-2-rest-order.sh" ]; then
    echo "❌ Error: test-2-rest-order.sh not found"
    exit 1
fi

if [ ! -f "$SCRIPT_DIR/test-3-soap-order.sh" ]; then
    echo "❌ Error: test-3-soap-order.sh not found"
    exit 1
fi

if [ ! -f "$SCRIPT_DIR/test-4-catalog-service.sh" ]; then
    echo "❌ Error: test-4-catalog-service.sh not found"
    exit 1
fi

echo "✅ All test scripts found"
echo

# Test 1: User Creation
echo "🔵 Starting Test 1: User Creation and Authentication"
echo "================================================"
if "$SCRIPT_DIR/test-1-user-creation.sh"; then
    echo "✅ Test 1 Passed: User Creation"
    TEST1_RESULT="PASSED"
else
    echo "❌ Test 1 Failed: User Creation"
    TEST1_RESULT="FAILED"
fi
echo

# Wait between tests
echo "⏳ Waiting 3 seconds before next test..."
sleep 3
echo

# Test 2: REST Order Processing
echo "🔵 Starting Test 2: REST Order Processing"
echo "================================================"
if "$SCRIPT_DIR/test-2-rest-order.sh"; then
    echo "✅ Test 2 Passed: REST Order Processing"
    TEST2_RESULT="PASSED"
else
    echo "❌ Test 2 Failed: REST Order Processing"
    TEST2_RESULT="FAILED"
fi
echo

# Wait between tests
echo "⏳ Waiting 3 seconds before next test..."
sleep 3
echo

# Test 3: SOAP Order Processing
echo "🔵 Starting Test 3: SOAP Order Processing"
echo "================================================"
if "$SCRIPT_DIR/test-3-soap-order.sh"; then
    echo "✅ Test 3 Passed: SOAP Order Processing"
    TEST3_RESULT="PASSED"
else
    echo "❌ Test 3 Failed: SOAP Order Processing"
    TEST3_RESULT="FAILED"
fi
echo

# Wait between tests
echo "⏳ Waiting 3 seconds before next test..."
sleep 3
echo

# Test 4: Catalog Service Direct Testing
echo "🔵 Starting Test 4: Catalog Service Direct Testing"
echo "================================================"
if "$SCRIPT_DIR/test-4-catalog-service.sh"; then
    echo "✅ Test 4 Passed: Catalog Service Direct Testing"
    TEST4_RESULT="PASSED"
else
    echo "❌ Test 4 Failed: Catalog Service Direct Testing"
    TEST4_RESULT="FAILED"
fi
echo

# Final Results Summary
echo "================================================"
echo "📊 FINAL TEST RESULTS SUMMARY"
echo "================================================"
echo "⏰ Test Completed: $(date)"
echo
echo "Test Results:"
echo "   1. User Creation & Auth:    $TEST1_RESULT"
echo "   2. REST Order Processing:   $TEST2_RESULT"
echo "   3. SOAP Order Processing:   $TEST3_RESULT"
echo "   4. Catalog Service Direct:  $TEST4_RESULT"
echo

# Overall result
if [ "$TEST1_RESULT" = "PASSED" ] && [ "$TEST2_RESULT" = "PASSED" ] && [ "$TEST3_RESULT" = "PASSED" ] && [ "$TEST4_RESULT" = "PASSED" ]; then
    echo "🎉 ALL TESTS PASSED - SOA SYSTEM FULLY FUNCTIONAL!"
    echo
    echo "✅ System Capabilities Validated:"
    echo "   • User registration and authentication"
    echo "   • JWT token generation and validation"
    echo "   • REST API order processing"
    echo "   • SOAP web service order processing"
    echo "   • Direct catalog service REST API"
    echo "   • Direct catalog service SOAP web service"
    echo "   • Microservices orchestration"
    echo "   • Queue-based asynchronous processing"
    echo
    echo "🏆 GlobalBooks SOA implementation is complete and working!"
    exit 0
else
    echo "❌ SOME TESTS FAILED - SYSTEM NEEDS ATTENTION"
    echo
    echo "🔧 Troubleshooting Tips:"
    echo "   • Ensure all services are running (auth, orchestration, etc.)"
    echo "   • Check RabbitMQ is running: docker-compose up -d rabbitmq"
    echo "   • Verify ports are not in use by other applications"
    echo "   • Check service logs for detailed error information"
    exit 1
fi
