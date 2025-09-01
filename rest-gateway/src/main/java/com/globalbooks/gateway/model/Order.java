package com.globalbooks.gateway.model;

import java.util.List;

public class Order {

    private Long id;
    private List<String> bookIsbns;
    private String customerId;

    public Order() {
    }

    public Order(Long id, List<String> bookIsbns, String customerId) {
        this.id = id;
        this.bookIsbns = bookIsbns;
        this.customerId = customerId;
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
}
