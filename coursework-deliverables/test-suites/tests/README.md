# GlobalBooks SOA - Test Suite

This directory contains comprehensive integration tests for the GlobalBooks SOA system, covering user authentication, REST API order processing, and SOAP web service functionality.

## Test Files

| Test Script | Purpose | Dependencies |
|-------------|---------|-------------|
| `test-1-user-creation.sh` | User registration and JWT authentication | Auth Server (8081) |
| `test-2-rest-order.sh` | REST API order processing workflow | Auth Server (8081), Orchestration Service (8086) |
| `test-3-soap-order.sh` | SOAP web service order processing | Orchestration Service (8086) |
| `run-all-tests.sh` | Master script to run all tests sequentially | All above dependencies |

## Quick Start

### Prerequisites
1. Ensure all services are running:
   ```bash
   # From the root directory
   docker-compose up -d rabbitmq
   
   # Start all microservices (in separate terminals)
   mvn spring-boot:run -pl auth-server -Dspring-boot.run.arguments=--server.port=8081
   mvn spring-boot:run -pl orders-service -Dspring-boot.run.arguments=--server.port=8082
   mvn spring-boot:run -pl payments-service -Dspring-boot.run.arguments=--server.port=8083
   mvn spring-boot:run -pl shipping-service -Dspring-boot.run.arguments=--server.port=8084
   mvn spring-boot:run -pl catalog-service -Dspring-boot.run.arguments=--server.port=8085
   mvn spring-boot:run -pl order-orchestration-service -Dspring-boot.run.arguments=--server.port=8086
   ```

2. Make test scripts executable:
   ```bash
   chmod +x tests/*.sh
   ```

### Running Tests

#### Individual Tests
```bash
# Test 1: User Creation and Authentication
./tests/test-1-user-creation.sh

# Test 2: REST Order Processing  
./tests/test-2-rest-order.sh

# Test 3: SOAP Order Processing
./tests/test-3-soap-order.sh
```

#### All Tests
```bash
# Run complete test suite
./tests/run-all-tests.sh
```

## Test Details

### Test 1: User Creation and Authentication
- **File**: `test-1-user-creation.sh`
- **Purpose**: Validates user registration and JWT token generation
- **Tested Endpoints**:
  - `POST /register` - User registration
  - `POST /authenticate` - User authentication
- **Success Criteria**:
  - User successfully registered
  - JWT token generated and validated
  - Token format verification
- **Output**: JWT token saved to `/tmp/globalbooks_jwt_token.txt`

### Test 2: REST Order Processing  
- **File**: `test-2-rest-order.sh`
- **Purpose**: Tests REST API order processing workflow
- **Tested Endpoints**:
  - `POST /api/orders/process` - Order processing via REST
- **Test Data**:
  - Book ID: `1` (The Great Gatsby)
  - Customer: `REST-TEST-USER-001`
  - Quantity: 2 copies
- **Dependencies**: Requires JWT token from Test 1
- **Success Criteria**:
  - Order accepted and processed
  - Valid order ID returned
  - Proper error handling for unauthorized requests

### Test 3: SOAP Order Processing
- **File**: `test-3-soap-order.sh`  
- **Purpose**: Validates SOAP web service functionality
- **Tested Endpoints**:
  - `POST /ws` - SOAP order processing
  - `GET /ws/orders.wsdl` - WSDL accessibility
- **Test Data**:
  - Book ID: `2` (To Kill a Mockingbird)
  - Customer: `SOAP-CUSTOMER-001`
  - Quantity: 3 copies
- **Success Criteria**:
  - SOAP envelope properly processed
  - Valid XML response returned
  - Order processed through microservices workflow

## Test Data

### Available Books (Catalog Service)
```json
[
  {"id": "1", "title": "The Great Gatsby", "author": "F. Scott Fitzgerald"},
  {"id": "2", "title": "To Kill a Mockingbird", "author": "Harper Lee"},
  {"id": "3", "title": "1984", "author": "George Orwell"},
  {"id": "978-0134685991", "title": "Clean Code", "author": "Robert C. Martin"}
]
```

### Test Users
- **Username**: `chamal1120`
- **Password**: `password`

## Test Configuration

### Service URLs
```bash
AUTH_SERVER_URL="http://localhost:8081"
ORCHESTRATION_URL="http://localhost:8086"
SOAP_ENDPOINT_URL="http://localhost:8086/ws"
WSDL_URL="http://localhost:8086/ws/orders.wsdl"
```

### Test Parameters
```bash
USERNAME="chamal1120"
PASSWORD="password"
```

## Expected Output

### Successful Test Run
```
================================================
Test 1: GlobalBooks SOA - User Creation
================================================
✅ Auth Server is running
✅ User registered successfully
✅ JWT Token: eyJhbGciOiJIUzI1NiJ9...
✅ JWT token validation: PASSED
🎉 User Creation test PASSED!

================================================
Test 2: GlobalBooks SOA - REST Order Processing  
================================================
✅ Auth Server is running
✅ Orchestration Service is running
✅ JWT Token obtained: eyJhbGciOiJIUzI1NiJ9...
✅ REST Order processed successfully!
🎉 REST Order Processing test PASSED!

================================================
Test 3: GlobalBooks SOA - SOAP Order Processing
================================================
✅ SOAP Service is running and accessible
✅ WSDL is accessible and properly formatted
✅ SOAP Order processed successfully!
🎉 SOAP Order Processing test PASSED!

🎉 ALL TESTS PASSED - SOA SYSTEM FULLY FUNCTIONAL!
```

## Troubleshooting

### Common Issues

#### Services Not Running
```bash
# Check if services are running
ps aux | grep java | grep -E "(auth|catalog|order|payment|shipping)"

# Check specific ports
lsof -i :8081-8086
```

#### JWT Token Issues
- **Problem**: "JWT token not found" or expired tokens
- **Solution**: Run Test 1 first or check token expiration (default: 10 hours)

#### Book Not Found Errors  
- **Problem**: "Book with ID X not found"
- **Solution**: Use valid book IDs from the test data (1, 2, 3, or 978-0134685991)

#### Connection Refused
- **Problem**: curl: (7) Failed to connect to localhost
- **Solution**: Ensure all required services are running on correct ports

#### RabbitMQ Issues
- **Problem**: Queue processing failures
- **Solution**: Start RabbitMQ: `docker-compose up -d rabbitmq`

### Debug Commands

#### Service Health Checks
```bash
curl http://localhost:8081/actuator/health  # Auth Server
curl http://localhost:8082/health           # Orders Service
curl http://localhost:8083/health           # Payments Service  
curl http://localhost:8084/health           # Shipping Service
curl http://localhost:8085/health           # Catalog Service
curl http://localhost:8086/actuator/health  # Orchestration Service
```

#### Manual API Testing
```bash
# Test catalog service
curl http://localhost:8085/api/books/1

# Test authentication
curl -X POST http://localhost:8081/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username": "chamal1120", "password": "password"}'
```

### Log Files
Service logs are available in the `logs/` directory:
- `auth-server.log`
- `catalog-service.log`
- `orders-service.log`
- `payments-service.log`
- `shipping-service.log`
- `orchestration-service.log`

## Test Artifacts

During test execution, the following temporary files are created:
- `/tmp/globalbooks_jwt_token.txt` - JWT token from successful authentication
- `/tmp/globalbooks_rest_order_id.txt` - Order ID from REST test
- `/tmp/globalbooks_soap_order_id.txt` - Order ID from SOAP test

## Contributing

When adding new tests:
1. Follow the existing naming convention: `test-{number}-{description}.sh`
2. Include comprehensive error checking and status validation
3. Use the existing test data (books, users) for consistency
4. Update this README with new test documentation
5. Add the new test to `run-all-tests.sh`

## API Reference

For complete API documentation, see: [API-ENDPOINTS.md](../API-ENDPOINTS.md)

---

**Last Updated**: September 2, 2025  
**Test Suite Version**: 1.0
