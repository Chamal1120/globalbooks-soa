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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

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
    public AmqpOutboundEndpoint paymentOutbound(AmqpTemplate amqpTemplate) {
        return Amqp.outboundAdapter(amqpTemplate)
                .routingKey("payment.process")
                .get();
    }

    @Bean
    public AmqpOutboundEndpoint shippingOutbound(AmqpTemplate amqpTemplate) {
        return Amqp.outboundAdapter(amqpTemplate)
                .routingKey("shipping.initiate")
                .get();
    }

    // Declare queues
    @Bean
    public Queue paymentProcessQueue() {
        return new Queue("payment.process", true);
    }

    @Bean
    public Queue shippingInitiateQueue() {
        return new Queue("shipping.initiate", true);
    }

    // Separate flows for async processing
    @Bean
    public IntegrationFlow paymentFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlows.from(paymentChannel())
                .log(LoggingHandler.Level.INFO, "payment", m -> m.getPayload().toString())
                .handle(paymentOutbound(amqpTemplate))
                .get();
    }

    @Bean
    public IntegrationFlow shippingFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlows.from(shippingChannel())
                .log(LoggingHandler.Level.INFO, "shipping", m -> m.getPayload())
                .handle(shippingOutbound(amqpTemplate))
                .get();
    }

    // Error handling flow
    @Bean
    public IntegrationFlow errorFlow() {
        return IntegrationFlows.from(errorChannel())
                .log(LoggingHandler.Level.ERROR, "error", m -> "Error processing order: " + m.getPayload())
                .nullChannel();
    }

    // Main order processing flow
    @Bean
    public IntegrationFlow orderProcessingFlow(
            AuthenticationService authenticationService,
            CatalogService catalogService,
            OrderService orderService,
            OrderConfirmationService orderConfirmationService,
            AmqpOutboundEndpoint paymentOutbound,
            AmqpOutboundEndpoint shippingOutbound) {
        
        return IntegrationFlows
                .from(orderInputChannel())
                .log(LoggingHandler.Level.INFO, "order", m -> "Starting order processing: " + m.getPayload())

                // Step 1: Validate Authentication
                .handle(authenticationService, "validateToken")
                .log(LoggingHandler.Level.INFO, "auth", m -> "Authentication validated")

                // Step 2: Check Book Availability (SOAP Call)
                .handle(catalogService, "checkAvailability")
                .log(LoggingHandler.Level.INFO, "catalog", m -> "Book availability checked")

                // Step 3: Create Order
                .handle(orderService, "createOrder")
                .log(LoggingHandler.Level.INFO, "order", m -> "Order created: " + m.getPayload())

                // Step 4: Send to async channels
                .wireTap(flow -> flow
                    .enrichHeaders(h -> h.header("processType", "payment"))
                    .channel(paymentChannel()))
                .wireTap(flow -> flow
                    .enrichHeaders(h -> h.header("processType", "shipping"))
                    .channel(shippingChannel()))

                // Step 5: Return confirmation immediately
                .handle(orderConfirmationService, "generateConfirmation")
                .log(LoggingHandler.Level.INFO, "confirmation", m -> "Order confirmation generated: " + m.getPayload())
                // ...existing code...
                .get();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB buffer
                .build();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.globalbooks.orchestration.generated");
        return marshaller;
    }

    @Bean
    public com.globalbooks.orchestration.client.CatalogClient catalogClient(Jaxb2Marshaller marshaller) {
        com.globalbooks.orchestration.client.CatalogClient client = new com.globalbooks.orchestration.client.CatalogClient();
        client.setDefaultUri("http://localhost:8085/ws"); // URL of the catalog-service SOAP endpoint
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}