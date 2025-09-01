package com.globalbooks.orders.service;

import com.globalbooks.orders.model.Order;
import com.globalbooks.orders.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderQueueProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OrderQueueProcessor.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @SuppressWarnings("unchecked")
    @RabbitListener(queues = "order.queue")
    public void processOrderFromQueue(Map<String, Object> orderData) {
        try {
            logger.info("Processing order from order.queue: {}", orderData);

            // Create new order with book details
            Order order = new Order();
            order.setCustomerId((String) orderData.get("userId"));
            
            // Extract book details from enriched catalog data
            Map<String, Object> bookDetails = (Map<String, Object>) orderData.get("bookDetails");
            if (bookDetails != null) {
                order.setBookDetails(bookDetails);
            }

            // Save order to database
            Order savedOrder = orderRepository.save(order);
            logger.info("Order saved with ID: {}", savedOrder.getId());

            // Create payment message with order and book details
            Map<String, Object> paymentMessage = new HashMap<>();
            paymentMessage.put("orderId", savedOrder.getId());
            paymentMessage.put("customerId", savedOrder.getCustomerId());
            paymentMessage.put("bookDetails", savedOrder.getBookDetails());
            paymentMessage.put("amount", calculateAmount(savedOrder.getBookDetails()));
            paymentMessage.put("shippingAddress", orderData.get("shippingAddress"));
            paymentMessage.put("paymentMethod", orderData.get("paymentMethod"));

            // Send to payment.queue
            amqpTemplate.convertAndSend("payment.queue", paymentMessage);
            logger.info("Sent order to payment.queue: {}", paymentMessage);

        } catch (Exception e) {
            logger.error("Error processing order from queue: {}", e.getMessage(), e);
        }
    }

    private double calculateAmount(Map<String, Object> bookDetails) {
        if (bookDetails != null && bookDetails.containsKey("quantity")) {
            int quantity = (Integer) bookDetails.get("quantity");
            return quantity * 29.99; // Base price per book
        }
        return 29.99;
    }
}
