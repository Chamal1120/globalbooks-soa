package com.globalbooks.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Value("${services.orchestration.url}")
    private String orchestrationServiceUrl;

    private final RestTemplate restTemplate;

    public OrderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processOrder(
            @RequestBody Map<String, Object> orderRequest,
            @RequestHeader(value = "Authorization", required = false) String token) {

        String url = orchestrationServiceUrl + "/api/orders/process";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        if (token != null) {
            headers.set("Authorization", token);
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderRequest, headers);
        
        return restTemplate.postForEntity(url, request, Object.class);
    }
}
