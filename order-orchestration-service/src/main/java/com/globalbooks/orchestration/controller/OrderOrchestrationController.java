package com.globalbooks.orchestration.controller;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.web.bind.annotation.*;
import org.springframework.integration.channel.QueueChannel;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderOrchestrationController {

    private final MessageChannel orderInputChannel;

    public OrderOrchestrationController(MessageChannel orderInputChannel) {
        this.orderInputChannel = orderInputChannel;
    }

    @PostMapping("/process")
    public Map<String, Object> processOrder(
            @RequestBody Map<String, Object> orderRequest,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        try {
            // Send the order to the integration flow (async processing)
            orderInputChannel.send(MessageBuilder
                .withPayload(orderRequest)
                .setHeader("Authorization", token)
                .build());
            
            // Return immediate success response
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("status", "success");
            response.put("message", "Order submitted for processing");
            response.put("bookId", orderRequest.get("bookId"));
            response.put("customerId", orderRequest.get("customerId"));
            response.put("quantity", orderRequest.get("quantity"));
            
            return response;
            
        } catch (Exception e) {
            throw new RuntimeException("Order processing failed: " + e.getMessage(), e);
        }
    }
}
