package com.globalbooks.orchestration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Value("${services.orders.url}")
    private String ordersServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @ServiceActivator
    public Message<?> createOrder(Message<?> message) {
        Map<String, Object> payload = (Map<String, Object>) message.getPayload();
        String token = (String) message.getHeaders().get("Authorization");
        
        try {
            // Create order via orders service
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            if (token != null) {
                headers.set("Authorization", token);
            }

            // Extract book IDs from items for the orders service
            List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
            List<String> bookIsbns = items.stream()
                .map(item -> (String) item.get("bookId"))
                .collect(Collectors.toList());

            // Create book details map from enriched item data
            Map<String, Object> bookDetails = new HashMap<>();
            items.forEach(item -> {
                String bookId = (String) item.get("bookId");
                Map<String, Object> details = new HashMap<>();
                details.put("title", item.get("bookTitle"));
                details.put("author", item.get("bookAuthor"));
                details.put("quantity", item.get("quantity"));
                bookDetails.put(bookId, details);
            });

            // Prepare order data matching the Order model
            Map<String, Object> orderData = Map.of(
                "customerId", payload.get("customerId"),
                "bookIsbns", bookIsbns,
                "bookDetails", bookDetails
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                ordersServiceUrl + "/orders", 
                request, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> orderResponse = response.getBody();
                
                // Create a new payload with order information
                Map<String, Object> newPayload = new HashMap<>(payload);
                newPayload.put("orderId", orderResponse.get("id"));
                newPayload.put("orderDetails", orderResponse);
                newPayload.put("orderCreated", true);
                
                return MessageBuilder.withPayload(newPayload)
                    .copyHeaders(message.getHeaders())
                    .build();
            } else {
                throw new RuntimeException("Order creation failed - HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Order creation failed: " + e.getMessage());
        }
    }
}
