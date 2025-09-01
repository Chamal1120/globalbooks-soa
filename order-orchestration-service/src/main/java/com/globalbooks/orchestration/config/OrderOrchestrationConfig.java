package com.globalbooks.orchestration.config;

import com.globalbooks.orchestration.service.AuthenticationService;
import com.globalbooks.orchestration.service.CatalogService;
import com.globalbooks.orchestration.service.OrderConfirmationService;
import com.globalbooks.orchestration.service.OrderService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.client.RestTemplate;

 @Configuration @EnableIntegration
public class OrderOrchestrationConfig {

    // Message Channels
    @Bean
    public MessageChannel orderInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel paymentChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel shippingChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel errorChannel() {
        return new DirectChannel();
    }

    // RabbitMQ Integration
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpOutboundEndpoint orderOutbound(AmqpTemplate amqpTemplate) {
        return Amqp.outboundAdapter(amqpTemplate)
                .routingKey("order.queue")
                .get();
    }

    @Bean
    public AmqpOutboundEndpoint paymentOutbound(AmqpTemplate amqpTemplate) {
        return Amqp.outboundAdapter(amqpTemplate)
                .routingKey("payment.queue")
                .get();
    }

    @Bean
    public AmqpOutboundEndpoint shippingOutbound(AmqpTemplate amqpTemplate) {
        return Amqp.outboundAdapter(amqpTemplate)
                .routingKey("shipping.queue")
                .get();
    }

    // Declare all necessary queues
    @Bean
    public Queue orderQueue() {
        return new Queue("order.queue", true);
    }

    @Bean
    public Queue paymentQueue() {
        return new Queue("payment.queue", true);
    }

    @Bean
    public Queue shippingQueue() {
        return new Queue("shipping.queue", true);
    }

    @Bean
    public Queue paymentConfirmQueue() {
        return new Queue("paymentconfirm.queue", true);
    }

    @Bean
    public Queue shippingConfirmQueue() {
        return new Queue("shippingconfirm.queue", true);
    }

    // Add a channel for order queue
    @Bean
    public MessageChannel orderChannel() {
        return new DirectChannel();
    }

    // Order flow - send to order.queue
    @Bean
    public IntegrationFlow orderFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlows.from(orderChannel())
                .log(LoggingHandler.Level.INFO, "order", m -> "Sending to order.queue: " + m.getPayload())
                .handle(orderOutbound(amqpTemplate))
                .get();
    }

    // Error handling flow
    @Bean
    public IntegrationFlow errorFlow() {
        return IntegrationFlows.from(errorChannel())
                .log(LoggingHandler.Level.ERROR, "error", m -> "Error processing order: " + m.getPayload())
                .nullChannel();
    }

    // Main order processing flow - simplified to just enrich and send to order.queue
    @Bean
    public IntegrationFlow orderProcessingFlow(
            CatalogService catalogService,
            AmqpOutboundEndpoint orderOutbound) {
        
        return IntegrationFlows
                .from(orderInputChannel())
                .log(LoggingHandler.Level.INFO, "orchestration", m -> "Starting order processing: " + m.getPayload())

                // Step 1: Check Book Availability and enrich with book details (SOAP Call)
                .handle(catalogService, "checkAvailability")
                .log(LoggingHandler.Level.INFO, "catalog", m -> "Book availability checked and enriched")

                // Step 2: Send enriched order to order.queue
                .channel(orderChannel())
                .get();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}