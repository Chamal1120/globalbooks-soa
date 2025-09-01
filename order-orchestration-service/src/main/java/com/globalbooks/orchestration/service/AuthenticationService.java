package com.globalbooks.orchestration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AuthenticationService {

    @Value("${services.auth.url}")
    private String authServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @ServiceActivator
    public Message<?> validateToken(Message<?> message) {
        Map<String, Object> payload = (Map<String, Object>) message.getPayload();
        String token = (String) message.getHeaders().get("Authorization");
        
        // For now, just check if token exists and add dummy user info
        // In production, this would validate with the auth service
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Authorization token is required");
        }
        
        // Simulate successful authentication
        payload.put("userId", "user123");
        payload.put("username", "testuser");
        
        return message;
    }
}
