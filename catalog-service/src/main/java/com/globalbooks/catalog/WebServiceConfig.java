package com.globalbooks.catalog;

import com.globalbooks.catalog.security.CatalogSecurityInterceptor;
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
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "books")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema booksSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("BooksPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://globalbooks.com/catalog");
        wsdl11Definition.setSchema(booksSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema booksSchema() {
        return new SimpleXsdSchema(new ClassPathResource("books.xsd"));
    }
    
    /**
     * WS-Security interceptor for SOAP endpoints ONLY.
     * 
     * CRITICAL: This interceptor only affects SOAP requests to /ws/* endpoints.
     * REST endpoints at /api/* are completely unaffected and continue to work
     * without any authentication changes.
     */
    @Bean
    public CatalogSecurityInterceptor catalogSecurityInterceptor() {
        return new CatalogSecurityInterceptor();
    }
    
    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        // Add WS-Security interceptor ONLY for SOAP endpoints
        interceptors.add(catalogSecurityInterceptor());
        super.addInterceptors(interceptors);
    }
}
