package com.globalbooks.orchestration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final RestTemplate restTemplate = new RestTemplate();

    @ServiceActivator
    public Message<?> checkAvailability(Message<?> message) {
        Map<String, Object> payload = (Map<String, Object>) message.getPayload();
        
        try {
            // Check if it's a single book order (direct format)
            String bookId = (String) payload.get("bookId");
            if (bookId != null) {
                // Call catalog service REST endpoint
                String url = "http://localhost:8085/api/books/" + bookId;
                Map response = restTemplate.getForObject(url, Map.class);
                
                if (response == null) {
                    throw new RuntimeException("Book with ID " + bookId + " not found");
                }
                
                // Add book details to the payload for downstream processing
                payload.put("bookTitle", response.get("title"));
                payload.put("bookAuthor", response.get("author"));
                payload.put("booksValidated", true);
                
                return message;
            }
            
            // Handle legacy items format if needed
            List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
            if (items != null && !items.isEmpty()) {
                // Extract book IDs and check availability for each book
                for (Map<String, Object> item : items) {
                    String itemBookId = (String) item.get("bookId");
                    if (itemBookId == null) {
                        throw new RuntimeException("Book ID is required for each item");
                    }
                    
                    // Call catalog service REST endpoint
                    String url = "http://localhost:8085/api/books/" + itemBookId;
                    Map response = restTemplate.getForObject(url, Map.class);
                    
                    if (response == null) {
                        throw new RuntimeException("Book with ID " + itemBookId + " not found");
                    }
                    
                    // Add book details to the item for downstream processing
                    item.put("bookTitle", response.get("title"));
                    item.put("bookAuthor", response.get("author"));
                }
                
                payload.put("booksValidated", true);
                return message;
            }
            
            throw new RuntimeException("No book ID or items provided in order");
            
        } catch (Exception e) {
            throw new RuntimeException("Catalog check failed: " + e.getMessage());
        }
    }
}
