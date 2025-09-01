# Queue-Based Order Processing Walkthrough

## Overview
This document provides a complete step-by-step walkthrough of the queue-based order processing workflow in the GlobalBooks SOA system. The architecture uses RabbitMQ for asynchronous message-driven communication between microservices.

## Architecture Summary

### Services
- **Auth Server** (Port 8081) - JWT-based authentication
- **Orders Service** (Port 8082) - Order management and status tracking
- **Payments Service** (Port 8083) - Payment processing
- **Shipping Service** (Port 8084) - Shipment handling
- **Catalog Service** (Port 8085) - Book catalog and details
- **Orchestration Service** (Port 8086) - Order workflow orchestration
- **RabbitMQ** (Ports 5672, 15672) - Message broker

### Queue Architecture
```
order.queue → payment.queue → shipping.queue
                    ↓              ↓
            paymentconfirm.queue   shippingconfirm.queue
                    ↓              ↓
                Orders Service (Status Updates)
```

## Prerequisites

### 1. Start RabbitMQ
```bash
cd /home/randy99/things/globalbooks/globalbooks-soa
docker-compose up -d rabbitmq
```

### 2. Start All Services
```bash
# Auth Server
cd auth-server && nohup mvn spring-boot:run > ../logs/auth-server.log 2>&1 &

# Orders Service (with RabbitMQ enabled)
cd orders-service && nohup mvn spring-boot:run > ../logs/orders-service.log 2>&1 &

# Payments Service
cd payments-service && nohup mvn spring-boot:run > ../logs/payments-service.log 2>&1 &

# Shipping Service
cd shipping-service && nohup mvn spring-boot:run > ../logs/shipping-service.log 2>&1 &

# Catalog Service
cd catalog-service && nohup mvn spring-boot:run > ../logs/catalog-service.log 2>&1 &

# Orchestration Service
cd order-orchestration-service && nohup mvn spring-boot:run > ../logs/orchestration-service.log 2>&1 &
```

### 3. Verify Services
```bash
# Check running services
ps aux | grep "spring-boot:run" | grep -v grep | wc -l  # Should return 6

# Check RabbitMQ Management UI
curl -s http://localhost:15672 | head -5
```

## Complete Workflow Walkthrough

### Step 1: Send Order Request 🌐

**Command:**
```bash
curl -X POST http://localhost:8086/api/orders/process \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": "3",
    "customerId": "demo-customer",
    "quantity": 1
  }'
```

**Expected Response:**
```json
{
  "quantity": 1,
  "customerId": "demo-customer",
  "message": "Order submitted for processing",
  "status": "success",
  "bookId": "3"
}
```

**What Happens:**
- REST request received at `OrderOrchestrationController`
- Request immediately returns success (asynchronous processing)
- Message enters Spring Integration pipeline

---

### Step 2: Check Orchestration Logs 📚

**Command:**
```bash
tail -5 logs/orchestration-service.log
```

**Expected Output:**
```
payload={bookId=3, customerId=demo-customer, quantity=1, bookTitle=1984, bookAuthor=George Orwell, booksValidated=true}
```

**What Happens:**
- **Catalog Service Called**: BookId "3" → "1984" by "George Orwell"
- **Message Enriched**: Original order + book details + validation flag
- **Sent to Queue**: Enriched message → `order.queue`

---

### Step 3: Monitor Queue Status 📊

**Command:**
```bash
curl -u guest:guest http://localhost:15672/api/queues | \
  jq '.[] | {name: .name, messages: .messages, consumers: .consumers}'
```

**Expected Output:**
```json
{
  "name": "order.queue",
  "messages": 0,
  "consumers": 1
}
{
  "name": "payment.queue", 
  "messages": 0,
  "consumers": 1
}
// ... other queues
```

**What Happens:**
- All messages processed quickly (0 messages remaining)
- Each queue has active consumers listening

---

### Step 4: Orders Service Processing 📥

**Command:**
```bash
tail -10 logs/orders-service.log
```

**Expected Output:**
```
INFO [ntContainer#0-1] OrderQueueProcessor : Processing order from order.queue: {bookId=3, customerId=demo-customer, quantity=1, bookTitle=1984, bookAuthor=George Orwell, booksValidated=true}
INFO [ntContainer#0-1] OrderQueueProcessor : Order saved with ID: 4
INFO [ntContainer#0-1] OrderQueueProcessor : Sent order to payment.queue: {amount=29.99, orderId=4, ...}
```

**What Happens:**
- **`@RabbitListener`** consumes from `order.queue`
- **Order Created**: Saved to database with auto-generated ID
- **Payment Request**: Sent to `payment.queue` with calculated amount

---

### Step 5: Payments Service Processing 💳

**Command:**
```bash
tail -5 logs/payments-service.log
```

**Expected Output:**
```
INFO [ntContainer#0-1] PaymentProcessor : Processing payment from payment.queue: {amount=29.99, orderId=4, ...}
INFO [ntContainer#0-1] PaymentProcessor : Payment created with ID: 4
INFO [ntContainer#0-1] PaymentProcessor : Payment confirmation sent to paymentconfirm.queue: {amount=29.99, orderId=4, paymentId=4, status=COMPLETED}
INFO [ntContainer#0-1] PaymentProcessor : Shipping message sent to shipping.queue: {orderId=4, ...}
```

**What Happens:**
- **Payment Processing**: Creates payment record with ID
- **Dual Message Sending**:
  - Payment confirmation → `paymentconfirm.queue`
  - Shipping initiation → `shipping.queue`

---

### Step 6: Parallel Processing ⚡

#### 6A: Payment Confirmation Processing

**Command:**
```bash
grep "payment confirmation" logs/orders-service.log | tail -1
```

**Expected Output:**
```
INFO [ntContainer#1-1] OrderStatusProcessor : Received payment confirmation: {amount=29.99, orderId=4, paymentId=4, status=COMPLETED}
INFO [ntContainer#1-1] OrderStatusProcessor : Order 4 status updated to PAID
```

#### 6B: Shipping Service Processing

**Command:**
```bash
tail -5 logs/shipping-service.log
```

**Expected Output:**
```
INFO [ntContainer#0-1] ShippingProcessor : Processing shipping from shipping.queue: {orderId=4, ...}
INFO [ntContainer#0-1] ShippingProcessor : Shipment created with ID: 4 for order 4
INFO [ntContainer#0-1] ShippingProcessor : Shipping confirmation sent to shippingconfirm.queue: {orderId=4, shipmentId=4, trackingNumber=TRK4, status=SHIPPED}
```

**What Happens Simultaneously:**
- **Orders Service**: Updates order status to `PAID`
- **Shipping Service**: Creates shipment and sends confirmation

---

### Step 7: Final Status Update ✅

**Command:**
```bash
grep "shipping confirmation" logs/orders-service.log | tail -1
```

**Expected Output:**
```
INFO [ntContainer#2-1] OrderStatusProcessor : Received shipping confirmation: {orderId=4, shipmentId=4, trackingNumber=TRK4, status=SHIPPED}
INFO [ntContainer#2-1] OrderStatusProcessor : Order 4 status updated to SHIPPED
```

**What Happens:**
- **Final Update**: Order status changed to `SHIPPED`
- **Workflow Complete**: Order fully processed

---

### Step 8: Verify Final State 🔍

**Command:**
```bash
curl -s http://localhost:8082/orders/4
```

**Expected Output:**
```json
{
  "id": 4,
  "bookIsbns": null,
  "customerId": null,
  "bookDetails": {
    "shippingStatus": "SHIPPED",
    "paymentStatus": "PAID"
  }
}
```

**Verification:**
- ✅ Order exists with correct ID
- ✅ Payment status: `PAID`
- ✅ Shipping status: `SHIPPED`

---

## Timing Analysis

| **Step** | **Service** | **Action** | **Queue** | **Timing** |
|----------|-------------|------------|-----------|------------|
| 1 | **Orchestration** | REST request received | - | `T+0ms` |
| 2 | **Orchestration** | Catalog enrichment | - | `T+0ms` |
| 3 | **Orchestration** | Send to queue | → `order.queue` | `T+14ms` |
| 4 | **Orders** | Create order | ← `order.queue` | `T+18ms` |
| 5 | **Orders** | Send payment | → `payment.queue` | `T+23ms` |
| 6 | **Payments** | Process payment | ← `payment.queue` | `T+27ms` |
| 7 | **Payments** | Send confirmations | → `paymentconfirm.queue` + `shipping.queue` | `T+2030ms` |
| 8 | **Orders** | Update to PAID | ← `paymentconfirm.queue` | `T+2031ms` |
| 9 | **Shipping** | Create shipment | ← `shipping.queue` | `T+2032ms` |
| 10 | **Shipping** | Send confirmation | → `shippingconfirm.queue` | `T+5034ms` |
| 11 | **Orders** | Update to SHIPPED | ← `shippingconfirm.queue` | `T+5036ms` |

**Total Processing Time**: ~5 seconds (including deliberate processing delays)

## Key Queue Definitions

### Queue Configuration
```java
// In RabbitConfig.java files across services
@Bean
public Queue orderQueue() { return new Queue("order.queue", true); }

@Bean  
public Queue paymentQueue() { return new Queue("payment.queue", true); }

@Bean
public Queue paymentConfirmQueue() { return new Queue("paymentconfirm.queue", true); }

@Bean
public Queue shippingQueue() { return new Queue("shipping.queue", true); }

@Bean
public Queue shippingConfirmQueue() { return new Queue("shippingconfirm.queue", true); }
```

### Message Listeners
```java
// Orders Service
@RabbitListener(queues = "order.queue")
public void processOrder(Map<String, Object> orderData) { ... }

@RabbitListener(queues = "paymentconfirm.queue")  
public void processPaymentConfirmation(Map<String, Object> confirmation) { ... }

@RabbitListener(queues = "shippingconfirm.queue")
public void processShippingConfirmation(Map<String, Object> confirmation) { ... }

// Payments Service
@RabbitListener(queues = "payment.queue")
public void processPayment(Map<String, Object> paymentData) { ... }

// Shipping Service
@RabbitListener(queues = "shipping.queue") 
public void processShipping(Map<String, Object> shippingData) { ... }
```

## Monitoring Commands

### Check All Orders
```bash
curl -s http://localhost:8082/orders | jq '.[0:3]'
```

### Monitor Queue Status
```bash
curl -u guest:guest http://localhost:15672/api/queues | \
  grep -E '"name"|"messages"|"consumers"' | \
  paste - - - | head -7
```

### Check Service Health
```bash
# Check service ports
lsof -i :8081-8086

# Check logs for errors
grep -i "error\|exception" logs/*.log
```

### RabbitMQ Management UI
Access the RabbitMQ dashboard at: **http://localhost:15672**
- Username: `guest`
- Password: `guest`

## Architecture Benefits

### ✅ **Asynchronous Processing**
- Immediate REST response
- No blocking operations
- Scalable message processing

### ✅ **Service Isolation** 
- No direct service-to-service calls
- Each service focuses on single responsibility
- Independent deployment and scaling

### ✅ **Message-Driven Architecture**
- Event sourcing patterns
- Audit trail through message logs
- Replay capability for failed messages

### ✅ **Status Tracking**
- Real-time order status updates
- Multiple confirmation points
- Complete order lifecycle management

### ✅ **Error Handling & Resilience**
- RabbitMQ message persistence
- Dead letter queue support
- Retry mechanisms

## Testing Different Scenarios

### Test with Different Books
```bash
# Book ID 1 - The Great Gatsby
curl -X POST http://localhost:8086/api/orders/process \
  -H "Content-Type: application/json" \
  -d '{"bookId": "1", "customerId": "test1", "quantity": 2}'

# Book ID 2 - To Kill a Mockingbird  
curl -X POST http://localhost:8086/api/orders/process \
  -H "Content-Type: application/json" \
  -d '{"bookId": "2", "customerId": "test2", "quantity": 1}'
```

### Verify Processing
```bash
# Check all orders
curl -s http://localhost:8082/orders

# Check specific order
curl -s http://localhost:8082/orders/{ORDER_ID}
```

## Troubleshooting

### Services Not Starting
```bash
# Check port conflicts
lsof -i :8081-8086

# Check logs for startup errors
tail -20 logs/{service-name}.log
```

### RabbitMQ Connection Issues
```bash
# Check RabbitMQ container
docker ps | grep rabbitmq

# Check RabbitMQ logs
docker logs globalbooks-soa-rabbitmq-1 --tail 20

# Test connection
curl -u guest:guest http://localhost:15672/api/overview
```

### Queue Processing Issues
```bash
# Check queue consumers
curl -u guest:guest http://localhost:15672/api/queues | \
  jq '.[] | {name: .name, consumers: .consumers}'

# Check for stuck messages
curl -u guest:guest http://localhost:15672/api/queues | \
  jq '.[] | select(.messages > 0) | {name: .name, messages: .messages}'
```

---

## Conclusion

This queue-based order processing system demonstrates a complete **event-driven microservices architecture** using:

- **Spring Boot** for microservices
- **RabbitMQ** for message-driven communication  
- **Spring Integration** for orchestration
- **Asynchronous processing** for scalability
- **Status tracking** for order lifecycle management

The workflow successfully processes orders through: **CREATED** → **PAID** → **SHIPPED** states using pure message-driven communication with no direct service dependencies.

**Result**: A truly scalable, resilient, and maintainable order processing system! 🚀
