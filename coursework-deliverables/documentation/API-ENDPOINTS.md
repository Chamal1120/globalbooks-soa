# GlobalBooks SOA - API Endpoints Documentation

This document provides a comprehensive reference for all REST and SOAP endpoints available in the GlobalBooks SOA system.

## Table of Contents
- [Service Overview](#service-overview)
- [REST API Endpoints](#rest-api-endpoints)
- [SOAP Web Service Endpoints](#soap-web-service-endpoints)
- [Available Test Data](#available-test-data)
- [Authentication](#authentication)
- [Testing Examples](#testing-examples)

## Service Overview

| Service | Port | Type | Description |
|---------|------|------|-------------|
| Auth Server | 8081 | REST | User authentication and JWT token management |
| Orders Service | 8082 | REST | Order management and persistence |
| Payments Service | 8083 | REST | Payment processing and tracking |
| Shipping Service | 8084 | REST | Shipment management |
| Catalog Service | 8085 | REST + SOAP | Book catalog and inventory |
| Order Orchestration | 8086 | REST + SOAP | Business process orchestration |
| REST Gateway | 8080 | REST | API Gateway for REST services |

## REST API Endpoints

### 🔐 Auth Server (Port 8081)

#### User Registration
```http
POST http://localhost:8081/register
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

#### User Authentication
```http
POST http://localhost:8081/authenticate
Content-Type: application/json

{
  "username": "string", 
  "password": "string"
}

Response:
{
  "jwt": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "string"
}
```

#### Health Check
```http
GET http://localhost:8081/actuator/health
```

---

### 📦 Orders Service (Port 8082)

#### Create Order
```http
POST http://localhost:8082/orders
Content-Type: application/json

{
  "id": null,
  "bookIsbns": ["string"],
  "customerId": "string"
}
```

#### Get Order by ID
```http
GET http://localhost:8082/orders/{id}
```

#### Get All Orders
```http
GET http://localhost:8082/orders
```

#### Health Check
```http
GET http://localhost:8082/health
```

---

### 💳 Payments Service (Port 8083)

#### Create Payment
```http
POST http://localhost:8083/payments
Content-Type: application/json

{
  "id": null,
  "orderId": "string",
  "amount": 0.0,
  "paymentMethod": "string",
  "status": "string"
}
```

#### Get Payment by ID
```http
GET http://localhost:8083/payments/{id}
```

#### Get All Payments
```http
GET http://localhost:8083/payments
```

#### Health Check
```http
GET http://localhost:8083/health
```

---

### 🚚 Shipping Service (Port 8084)

#### Create Shipment
```http
POST http://localhost:8084/shipments
Content-Type: application/json

{
  "id": null,
  "orderId": "string",
  "address": "string",
  "status": "string",
  "trackingNumber": "string"
}
```

#### Get Shipment by ID
```http
GET http://localhost:8084/shipments/{id}
```

#### Get All Shipments
```http
GET http://localhost:8084/shipments
```

#### Health Check
```http
GET http://localhost:8084/health
```

---

### 📚 Catalog Service (Port 8085)

#### Get Book by ID (REST)
```http
GET http://localhost:8085/api/books/{id}

Response:
{
  "id": "string",
  "title": "string", 
  "author": "string"
}
```

#### Health Check
```http
GET http://localhost:8085/health
```

#### WSDL Access
```http
GET http://localhost:8085/ws/books.wsdl
```

---

### 🎯 Order Orchestration Service (Port 8086)

#### Process Order (REST)
```http
POST http://localhost:8086/api/orders/process
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "customerId": "string",
  "bookId": "string",
  "quantity": 1,
  "shippingAddress": {
    "street": "string",
    "city": "string", 
    "state": "string",
    "zipCode": "string",
    "country": "string"
  },
  "paymentDetails": {
    "cardNumber": "string",
    "expiryMonth": "string",
    "expiryYear": "string", 
    "cvv": "string",
    "cardholderName": "string"
  }
}
```

#### Health Check
```http
GET http://localhost:8086/actuator/health
```

#### WSDL Access
```http
GET http://localhost:8086/ws/orders.wsdl
```

---

### 🌐 REST Gateway (Port 8080)

#### Book Details via Gateway
```http
GET http://localhost:8080/books/{id}
```

#### User Registration via Gateway
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

#### User Authentication via Gateway  
```http
POST http://localhost:8080/api/auth/authenticate
Content-Type: application/json

{
  "username": "string",
  "password": "string"  
}
```

#### Order Processing via Gateway
```http
POST http://localhost:8080/api/orders/process
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "customerId": "string",
  "bookId": "string", 
  "quantity": 1
}
```

#### Gateway Proxied Endpoints
- `GET /api/orders` → Orders Service
- `GET /api/orders/{id}` → Orders Service
- `POST /api/orders` → Orders Service
- `GET /api/payments` → Payments Service
- `GET /api/payments/{id}` → Payments Service
- `POST /api/payments` → Payments Service
- `GET /api/shipments` → Shipping Service
- `GET /api/shipments/{id}` → Shipping Service
- `POST /api/shipments` → Shipping Service

## SOAP Web Service Endpoints

### 📚 Catalog Service SOAP (Port 8085)

#### Endpoint URL
```
http://localhost:8085/ws
```

#### WSDL Location
```
http://localhost:8085/ws/books.wsdl
```

#### Get Book Details Operation
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" 
               xmlns:cat="http://globalbooks.com/catalog">
   <soap:Header/>
   <soap:Body>
      <cat:getBookDetailsRequest>
         <cat:id>1</cat:id>
      </cat:getBookDetailsRequest>
   </soap:Body>
</soap:Envelope>
```

#### Response
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <ns2:getBookDetailsResponse xmlns:ns2="http://globalbooks.com/catalog">
         <ns2:book>
            <ns2:id>1</ns2:id>
            <ns2:title>The Great Gatsby</ns2:title>
            <ns2:author>F. Scott Fitzgerald</ns2:author>
         </ns2:book>
      </ns2:getBookDetailsResponse>
   </soap:Body>
</soap:Envelope>
```

---

### 🎯 Order Orchestration SOAP (Port 8086)

#### Endpoint URL
```
http://localhost:8086/ws
```

#### WSDL Location
```
http://localhost:8086/ws/orders.wsdl
```

#### Process Order Operation
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" 
               xmlns:ord="http://globalbooks.com/orders">
   <soap:Header/>
   <soap:Body>
      <ord:ProcessOrderRequest>
         <ord:customerId>SOAP-CUSTOMER-001</ord:customerId>
         <ord:bookId>1</ord:bookId>
         <ord:quantity>2</ord:quantity>
      </ord:ProcessOrderRequest>
   </soap:Body>
</soap:Envelope>
```

#### Response
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <ns2:ProcessOrderResponse xmlns:ns2="http://globalbooks.com/orders">
         <ns2:orderId>ORD-12345</ns2:orderId>
         <ns2:status>SUCCESS</ns2:status>
         <ns2:message>Order processed successfully</ns2:message>
      </ns2:ProcessOrderResponse>
   </soap:Body>
</soap:Envelope>
```

## Available Test Data

### 📖 Books in Catalog
| ID | Title | Author |
|----|-------|--------|
| `1` | The Great Gatsby | F. Scott Fitzgerald |
| `2` | To Kill a Mockingbird | Harper Lee |
| `3` | 1984 | George Orwell |
| `978-0134685991` | Clean Code | Robert C. Martin |

### 👤 Test Users
| Username | Password | Purpose |
|----------|----------|---------|
| `chamal1120` | `password` | General testing |
| `testuser` | `password` | Authentication testing |

## Authentication

### JWT Token Format
```json
{
  "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaGFtYWwxMTIwIiwiaWF0IjoxNzU2NzU4MTA0LCJleHAiOjE3NTY3OTQxMDR9.kwJprh8HHfHT31vMtnzsXGHm9Dl5eh2ikYGdzaHpQiM",
  "username": "chamal1120"
}
```

### Authorization Header
```
Authorization: Bearer {jwt_token}
```

## Testing Examples

### Quick Health Check All Services
```bash
# Check all services are running
curl http://localhost:8081/actuator/health  # Auth Server
curl http://localhost:8082/health           # Orders Service  
curl http://localhost:8083/health           # Payments Service
curl http://localhost:8084/health           # Shipping Service
curl http://localhost:8085/health           # Catalog Service
curl http://localhost:8086/actuator/health  # Orchestration Service
```

### Complete Order Flow (REST)
```bash
# 1. Register user
curl -X POST http://localhost:8081/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password"}'

# 2. Authenticate and get JWT
JWT_RESPONSE=$(curl -s -X POST http://localhost:8081/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password"}')

JWT_TOKEN=$(echo "$JWT_RESPONSE" | grep -o '"jwt":"[^"]*"' | cut -d'"' -f4)

# 3. Process order
curl -X POST http://localhost:8086/api/orders/process \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "customerId": "testuser",
    "bookId": "1", 
    "quantity": 1,
    "shippingAddress": {
      "street": "123 Main St",
      "city": "New York",
      "state": "NY", 
      "zipCode": "10001",
      "country": "USA"
    },
    "paymentDetails": {
      "cardNumber": "4111111111111111",
      "expiryMonth": "12",
      "expiryYear": "2025",
      "cvv": "123",
      "cardholderName": "Test User"
    }
  }'
```

### SOAP Order Testing
```bash
# Process order via SOAP
curl -X POST http://localhost:8086/ws \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" 
               xmlns:ord="http://globalbooks.com/orders">
   <soap:Header/>
   <soap:Body>
      <ord:ProcessOrderRequest>
         <ord:customerId>SOAP-CUSTOMER-001</ord:customerId>
         <ord:bookId>2</ord:bookId>
         <ord:quantity>1</ord:quantity>
      </ord:ProcessOrderRequest>
   </soap:Body>
</soap:Envelope>'
```

### Catalog Service Testing
```bash
# REST - Get book details
curl http://localhost:8085/api/books/1

# SOAP - Get book details  
curl -X POST http://localhost:8085/ws \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" 
               xmlns:cat="http://globalbooks.com/catalog">
   <soap:Header/>
   <soap:Body>
      <cat:getBookDetailsRequest>
         <cat:id>1</cat:id>
      </cat:getBookDetailsRequest>
   </soap:Body>
</soap:Envelope>'
```

## Common Issues & Solutions

### 🚨 404 Not Found Errors
- **REST Endpoint**: Ensure you're using `/api/books/{id}` not `/books/{id}` for catalog service
- **SOAP Endpoint**: Use `/ws` not `/ws/` for SOAP endpoints

### 🚨 Authentication Errors
- Always include `Authorization: Bearer {token}` header for protected endpoints
- JWT tokens expire after ~10 hours, re-authenticate if needed
- Token field in response is `jwt` not `token`

### 🚨 Book Not Found Errors
- Use valid book IDs: `1`, `2`, `3`, or `978-0134685991`
- Avoid using random ISBNs like `978-0747532743`

### 🚨 Service Connection Errors
- Verify all services are running on correct ports
- Check RabbitMQ is running: `docker-compose up -d rabbitmq`
- Use health check endpoints to verify service status

---