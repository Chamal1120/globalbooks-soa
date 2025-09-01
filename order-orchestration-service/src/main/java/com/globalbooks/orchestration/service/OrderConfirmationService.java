package com.globalbooks.orchestration.service;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderConfirmationService {

    @ServiceActivator
    public Map<String, Object> generateConfirmation(Message<?> message) {
        Map<String, Object> payload = (Map<String, Object>) message.getPayload();
        
        Map<String, Object> confirmation = new HashMap<>();
        confirmation.put("confirmationId", UUID.randomUUID().toString());
        confirmation.put("orderId", payload.get("orderId"));
        confirmation.put("customerId", payload.get("customerId"));
        confirmation.put("bookIsbns", payload.get("bookIsbns"));
        confirmation.put("status", "PROCESSING");
        confirmation.put("timestamp", LocalDateTime.now().toString());
        confirmation.put("message", "Order has been received and is being processed. Payment and shipping will be handled asynchronously.");
        
        return confirmation;
    }
}
