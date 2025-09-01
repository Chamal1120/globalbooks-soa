package com.globalbooks.payments.service;

import com.globalbooks.payments.model.Payment;
import com.globalbooks.payments.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessor.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RabbitListener(queues = "payment.queue")
    public void processPayment(Map<String, Object> paymentData) {
        try {
            logger.info("Processing payment from payment.queue: {}", paymentData);

            // Simulate payment processing delay
            Thread.sleep(2000);

            Long orderId = ((Number) paymentData.get("orderId")).longValue();
            double amount = ((Number) paymentData.get("amount")).doubleValue();

            // Create payment record
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(new java.math.BigDecimal(amount));
            payment.setStatus("PROCESSING");

            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment created with ID: {}", savedPayment.getId());

            // Process payment (simulate success)
            savedPayment.setStatus("COMPLETED");
            paymentRepository.save(savedPayment);
            logger.info("Payment {} completed for order {}", savedPayment.getId(), orderId);

            // Send confirmation back to orders service
            Map<String, Object> paymentConfirmation = new HashMap<>();
            paymentConfirmation.put("orderId", orderId);
            paymentConfirmation.put("paymentId", savedPayment.getId());
            paymentConfirmation.put("status", "COMPLETED");
            paymentConfirmation.put("amount", amount);

            amqpTemplate.convertAndSend("paymentconfirm.queue", paymentConfirmation);
            logger.info("Payment confirmation sent to paymentconfirm.queue: {}", paymentConfirmation);

            // Send to shipping queue for shipping processing
            Map<String, Object> shippingMessage = new HashMap<>();
            shippingMessage.put("orderId", orderId);
            shippingMessage.put("customerId", paymentData.get("customerId"));
            shippingMessage.put("bookDetails", paymentData.get("bookDetails"));
            shippingMessage.put("shippingAddress", paymentData.get("shippingAddress"));

            amqpTemplate.convertAndSend("shipping.queue", shippingMessage);
            logger.info("Shipping message sent to shipping.queue: {}", shippingMessage);

        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage(), e);
        }
    }
}
