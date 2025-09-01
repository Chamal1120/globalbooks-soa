package com.globalbooks.orders.model;

import java.util.List;
import java.util.Map;

public class Order {

    private Long id;
    private List<String> bookIsbns;
    private String customerId;
    private Map<String, Object> bookDetails; // Maps ISBN to book details

    public Order() {
    }

    public Order(Long id, List<String> bookIsbns, String customerId) {
        this.id = id;
        this.bookIsbns = bookIsbns;
        this.customerId = customerId;
    }

    public Order(Long id, List<String> bookIsbns, String customerId, Map<String, Object> bookDetails) {
        this.id = id;
        this.bookIsbns = bookIsbns;
        this.customerId = customerId;
        this.bookDetails = bookDetails;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getBookIsbns() {
        return bookIsbns;
    }

    public void setBookIsbns(List<String> bookIsbns) {
        this.bookIsbns = bookIsbns;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Map<String, Object> getBookDetails() {
        return bookDetails;
    }

    public void setBookDetails(Map<String, Object> bookDetails) {
        this.bookDetails = bookDetails;
    }
}
