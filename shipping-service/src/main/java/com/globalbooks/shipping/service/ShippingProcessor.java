package com.globalbooks.shipping.service;

import com.globalbooks.shipping.model.Shipment;
import com.globalbooks.shipping.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ShippingProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ShippingProcessor.class);

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @SuppressWarnings("unchecked")
    @RabbitListener(queues = "shipping.queue")
    public void processShipping(Map<String, Object> shippingData) {
        try {
            logger.info("Processing shipping from shipping.queue: {}", shippingData);

            // Simulate shipping processing delay
            Thread.sleep(3000);

            Long orderId = ((Number) shippingData.get("orderId")).longValue();
            Map<String, Object> shippingAddress = (Map<String, Object>) shippingData.get("shippingAddress");

            // Create shipment record
            Shipment shipment = new Shipment();
            shipment.setOrderId(orderId);
            
            // Extract address details
            if (shippingAddress != null) {
                shipment.setAddress(
                    shippingAddress.get("street") + ", " + 
                    shippingAddress.get("city") + ", " + 
                    shippingAddress.get("state") + " " + 
                    shippingAddress.get("zipCode")
                );
            }
            
            shipment.setStatus("PREPARING");

            Shipment savedShipment = shipmentRepository.save(shipment);
            logger.info("Shipment created with ID: {} for order {}", savedShipment.getId(), orderId);

            // Process shipment (simulate shipping)
            savedShipment.setStatus("SHIPPED");
            shipmentRepository.save(savedShipment);
            logger.info("Shipment {} shipped for order {}", savedShipment.getId(), orderId);

            // Send confirmation back to orders service
            Map<String, Object> shippingConfirmation = new HashMap<>();
            shippingConfirmation.put("orderId", orderId);
            shippingConfirmation.put("shipmentId", savedShipment.getId());
            shippingConfirmation.put("status", "SHIPPED");
            shippingConfirmation.put("trackingNumber", "TRK" + savedShipment.getId());

            amqpTemplate.convertAndSend("shippingconfirm.queue", shippingConfirmation);
            logger.info("Shipping confirmation sent to shippingconfirm.queue: {}", shippingConfirmation);

        } catch (Exception e) {
            logger.error("Error processing shipping: {}", e.getMessage(), e);
        }
    }
}
