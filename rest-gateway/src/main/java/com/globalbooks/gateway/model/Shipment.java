package com.globalbooks.gateway.model;

public class Shipment {

    private Long id;
    private Long orderId;
    private String address;
    private String status;

    public Shipment() {
    }

    public Shipment(Long id, Long orderId, String address, String status) {
        this.id = id;
        this.orderId = orderId;
        this.address = address;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
