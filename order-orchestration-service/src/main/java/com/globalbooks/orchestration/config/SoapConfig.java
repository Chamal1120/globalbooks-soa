package com.globalbooks.orchestration.config;

import com.globalbooks.orchestration.security.OrderSecurityInterceptor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;

@EnableWs
@Configuration
public class SoapConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        ServletRegistrationBean<MessageDispatcherServlet> registration = new ServletRegistrationBean<>(servlet, "/ws/*");
        registration.setLoadOnStartup(1);
        return registration;
    }

    @Bean(name = "orders")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema ordersSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("OrdersPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://globalbooks.com/orders");
        wsdl11Definition.setSchema(ordersSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema ordersSchema() {
        return new SimpleXsdSchema(new ClassPathResource("orders.xsd"));
    }
    
    /**
     * WS-Security interceptor for SOAP endpoints ONLY.
     * 
     * CRITICAL: This interceptor only affects SOAP requests to /ws/* endpoints.
     * REST endpoints at /api/* with JWT authentication are completely unaffected.
     */
    @Bean
    public OrderSecurityInterceptor orderSecurityInterceptor() {
        return new OrderSecurityInterceptor();
    }
    
    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        // Add WS-Security interceptor ONLY for SOAP endpoints
        interceptors.add(orderSecurityInterceptor());
        super.addInterceptors(interceptors);
    }
}
