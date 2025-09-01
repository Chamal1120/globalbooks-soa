package com.globalbooks.gateway.controller;

import com.globalbooks.gateway.client.CatalogClient;
import com.globalbooks.gateway.generated.Book;
import com.globalbooks.gateway.generated.GetBookDetailsResponse;
import com.globalbooks.gateway.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class GatewayController {

    @Autowired
    private CatalogClient catalogClient;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/books/{id}")
    public Book getBookDetails(@PathVariable("id") String id) {
        GetBookDetailsResponse response = catalogClient.getBookDetails(id);
        return response.getBook();
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return restTemplate.postForEntity("http://localhost:8081/register", user, String.class);
    }

    @PostMapping("/api/auth/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return restTemplate.postForEntity("http://localhost:8081/authenticate", request, AuthenticationResponse.class);
    }

    @PostMapping("/api/orders")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return restTemplate.postForEntity("http://localhost:8082/orders", order, Order.class);
    }

    @GetMapping("/api/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return restTemplate.getForEntity("http://localhost:8082/orders/{id}", Order.class, id);
    }

    @GetMapping("/api/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return restTemplate.exchange("http://localhost:8082/orders", org.springframework.http.HttpMethod.GET, null, new org.springframework.core.ParameterizedTypeReference<List<Order>>() {});
    }

    @PostMapping("/api/payments")
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        return restTemplate.postForEntity("http://localhost:8083/payments", payment, Payment.class);
    }

    @GetMapping("/api/payments/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return restTemplate.getForEntity("http://localhost:8083/payments/{id}", Payment.class, id);
    }

    @GetMapping("/api/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return restTemplate.exchange("http://localhost:8083/payments", org.springframework.http.HttpMethod.GET, null, new org.springframework.core.ParameterizedTypeReference<List<Payment>>() {});
    }

    @PostMapping("/api/shipments")
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment) {
        return restTemplate.postForEntity("http://localhost:8084/shipments", shipment, Shipment.class);
    }

    @GetMapping("/api/shipments/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        return restTemplate.getForEntity("http://localhost:8084/shipments/{id}", Shipment.class, id);
    }

    @GetMapping("/api/shipments")
    public ResponseEntity<List<Shipment>> getAllShipments() {
        return restTemplate.exchange("http://localhost:8084/shipments", org.springframework.http.HttpMethod.GET, null, new org.springframework.core.ParameterizedTypeReference<List<Shipment>>() {});
    }
}