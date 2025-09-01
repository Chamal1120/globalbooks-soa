package com.globalbooks.orders.service;

import com.globalbooks.orders.model.Order;
import com.globalbooks.orders.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderStatusProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OrderStatusProcessor.class);

    @Autowired
    private OrderRepository orderRepository;

    @RabbitListener(queues = "paymentconfirm.queue")
    public void processPaymentConfirmation(Map<String, Object> paymentConfirmation) {
        try {
            logger.info("Received payment confirmation: {}", paymentConfirmation);
            
            Long orderId = ((Number) paymentConfirmation.get("orderId")).longValue();
            String status = (String) paymentConfirmation.get("status");
            
            if ("COMPLETED".equals(status)) {
                // Update order status to PAID
                Order order = orderRepository.findById(orderId);
                if (order != null) {
                    // Add a status field or use bookDetails to store status
                    if (order.getBookDetails() == null) {
                        order.setBookDetails(new java.util.HashMap<>());
                    }
                    order.getBookDetails().put("paymentStatus", "PAID");
                    orderRepository.save(order);
                    logger.info("Order {} status updated to PAID", orderId);
                } else {
                    logger.warn("Order {} not found for payment confirmation", orderId);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing payment confirmation: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "shippingconfirm.queue")
    public void processShippingConfirmation(Map<String, Object> shippingConfirmation) {
        try {
            logger.info("Received shipping confirmation: {}", shippingConfirmation);
            
            Long orderId = ((Number) shippingConfirmation.get("orderId")).longValue();
            String status = (String) shippingConfirmation.get("status");
            
            if ("SHIPPED".equals(status)) {
                // Update order status to SHIPPED
                Order order = orderRepository.findById(orderId);
                if (order != null) {
                    if (order.getBookDetails() == null) {
                        order.setBookDetails(new java.util.HashMap<>());
                    }
                    order.getBookDetails().put("shippingStatus", "SHIPPED");
                    orderRepository.save(order);
                    logger.info("Order {} status updated to SHIPPED", orderId);
                } else {
                    logger.warn("Order {} not found for shipping confirmation", orderId);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing shipping confirmation: {}", e.getMessage(), e);
        }
    }
}
