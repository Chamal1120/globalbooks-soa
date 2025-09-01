package com.globalbooks.gateway.config;

import com.globalbooks.gateway.client.CatalogClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class GatewayConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.globalbooks.gateway.generated");
        return marshaller;
    }

    @Bean
    public CatalogClient catalogClient(Jaxb2Marshaller marshaller) {
        CatalogClient client = new CatalogClient();
        client.setDefaultUri("http://localhost:8085/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
