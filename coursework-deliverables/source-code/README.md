# Source Code

This directory is intentionally left empty as per the deliverable requirements.

## Note

The complete source code for all services is available in the main project directory structure:

- `auth-server/` - JWT authentication service
- `catalog-service/` - SOAP-based catalog service  
- `orders-service/` - REST-based order management
- `payments-service/` - Payment processing service
- `shipping-service/` - Shipping coordination service
- `order-orchestration-service/` - Business process orchestration
- `rest-gateway/` - API gateway and routing

## Key Implementation Files

For coursework evaluation, the following source files demonstrate the core implementations:

**SOAP Service Implementation:**
- `catalog-service/src/main/java/com/globalbooks/catalog/endpoint/CatalogEndpoint.java`
- `catalog-service/src/main/java/com/globalbooks/catalog/config/WebServiceConfig.java`

**REST Service Implementation:**
- `orders-service/src/main/java/com/globalbooks/orders/controller/OrderController.java`
- `orders-service/src/main/java/com/globalbooks/orders/service/OrderService.java`

**Integration/Orchestration:**
- `order-orchestration-service/src/main/java/com/globalbooks/orchestration/service/OrderOrchestrationService.java`

**Security Configuration:**
- `auth-server/src/main/java/com/globalbooks/auth/config/SecurityConfig.java`
- `auth-server/src/main/java/com/globalbooks/auth/util/JwtUtil.java`
