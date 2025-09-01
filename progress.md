# Project Progress: GlobalBooks SOA

This document summarizes the initial setup and configuration of the GlobalBooks SOA project.

## 1. Project Scaffolding

A complete multi-module Maven project structure has been created for the Service-Oriented Architecture.

- **Parent Project:** `globalbooks-soa`
  - Configured with Java 8 and Spring Boot 2.7.17.
- **Service Modules Created:**
  - `auth-server`
  - `orders-service`
  - `payments-service`
  - `shipping-service`
  - `rest-gateway`
  - `catalog-service` (as a locally runnable JAX-WS SOAP service)
- **Build Configuration:**
  - A parent `pom.xml` manages shared dependencies.
  - Each service module has its own `pom.xml` with appropriate starters (`spring-boot-starter-web` for REST services, `spring-boot-starter-web-services` for the SOAP service).
- **Application Skeletons:**
  - Each service has a main `Application.java` class, making it a runnable Spring Boot application.

## 2. Service Configuration

- **Server Ports:** Each microservice has been assigned a unique port in its `src/main/resources/application.properties` file as per the implementation plan:
  - `rest-gateway`: 8080
  - `auth-server`: 8081
  - `orders-service`: 8082
  - `payments-service`: 8083
  - `shipping-service`: 8084
  - `catalog-service`: 8085

## 3. Environment and Execution

- **Dockerized Infrastructure:**
  - A `docker-compose.yml` file has been created to manage heavy infrastructure components:
    - **RabbitMQ:** Uses the official `rabbitmq:3.9-management` image.
    - **Apache ODE:** A custom Docker image is configured. It is built using a `Dockerfile` that deploys the user-provided `ode.war` file onto a `tomcat:8.5-jdk8-openjdk` base image.
- **Local Development Script:**
  - An executable shell script, `scripts/start-dev-services.sh`, was created to simplify local development. It builds all Maven modules and starts each of the six microservices.
- **Version Control:**
  - A `.gitignore` file has been added to the project root to exclude common unnecessary files and directories.

## 4. Catalog Service Implementation

The `catalog-service` has been implemented as a contract-first SOAP web service.

- **Contract (XSD):** A schema file `books.xsd` was created to define the `Book` data structure and the `getBookDetails` request/response messages.
- **Code Generation:** The `pom.xml` was updated with the `jaxb2-maven-plugin` to automatically generate Java classes from the XSD.
- **Endpoint Logic:**
    - A `BookRepository` was created to serve as an in-memory database for books.
    - A `BookEndpoint` was implemented to handle SOAP requests, retrieve data from the repository, and send responses.
- **Configuration:** A `WebServiceConfig` class was added to configure the Spring-WS servlet and expose the WSDL at `http://localhost:8085/ws/books.wsdl`.
- **Dependency Fix:** The `jaxb-runtime` dependency was added to the `pom.xml` to resolve a SOAP fault at runtime.
- **Verification:** The service was successfully tested by sending a SOAP request and receiving a valid response.

## 5. Development Workflow Enhancement

- A `restart-dev-services.sh` script was created to improve the development lifecycle. This script stops any running services, rebuilds the project, and restarts all services.

## 6. Auth Server Implementation

The `auth-server` has been implemented to provide authentication and authorization services.

- **User Persistence:** The in-memory user repository was replaced with a JPA-based repository using an H2 in-memory database.
- **User Model:** The `User` model was updated to be a JPA entity.
- **JWT Implementation:** The deprecated JWT generation and parsing logic was updated to use the latest `jjwt` library version.
- **Registration Endpoint:** A `/register` endpoint was added to allow new users to be created.
- **Security Configuration:** Spring Security was configured to handle authentication and authorization.
- **Verification:** The `/register` and `/authenticate` endpoints were tested to ensure they are working correctly.

## 7. Orders Service Implementation

The `orders-service` has been implemented to manage customer orders.

- **Order Model:** A simple `Order` model was created to represent an order.
- **In-Memory Repository:** An in-memory repository was implemented to store and retrieve orders.
- **REST Controller:** A REST controller was created with endpoints for creating and retrieving orders.
- **Verification:** The service was successfully tested by creating and retrieving orders.

## 8. Payments Service Implementation

The `payments-service` has been implemented to manage customer payments.

- **Payment Model:** A simple `Payment` model was created to represent a payment.
- **In-Memory Repository:** An in-memory repository was implemented to store and retrieve payments.
- **REST Controller:** A REST controller was created with endpoints for creating and retrieving payments.
- **Verification:** The service was successfully tested by creating and retrieving payments.

## 9. Shipping Service Implementation

The `shipping-service` has been implemented to manage customer shipments.

- **Shipment Model:** A simple `Shipment` model was created to represent a shipment.
- **In-Memory Repository:** An in-memory repository was implemented to store and retrieve shipments.
- **REST Controller:** A REST controller was created with endpoints for creating and retrieving shipments.
- **Verification:** The service was successfully tested by creating and retrieving shipments.

## 10. REST Gateway Implementation

The `rest-gateway` has been implemented to act as a single entry point for all the microservices.

- **SOAP to REST:** Exposed a REST endpoint for the SOAP-based `catalog-service`.
- **REST Proxy:** Proxied the REST endpoints for the `auth-server`, `orders-service`, `payments-service`, and `shipping-service`.
- **Models:** Created models in the `rest-gateway` to match the models in the other services.
- **Verification:** The gateway was successfully tested by making requests to all the proxied endpoints.

## Next Steps

orchestration.