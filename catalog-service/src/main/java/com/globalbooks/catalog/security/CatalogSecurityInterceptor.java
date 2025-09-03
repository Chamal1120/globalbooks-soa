package com.globalbooks.catalog.security;

import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.callback.SimplePasswordValidationCallbackHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * WS-Security interceptor for SOAP endpoints only.
 * Validates Username Token authentication for SOAP requests.
 * 
 * IMPORTANT: This interceptor ONLY affects SOAP endpoints (/ws/*).
 * REST endpoints (/api/*) are completely unaffected.
 */
public class CatalogSecurityInterceptor extends Wss4jSecurityInterceptor {
    
    private static final String SOAP_USERNAME = "catalog-client";
    private static final String SOAP_PASSWORD = "catalog-secure-2024";
    
    public CatalogSecurityInterceptor() {
        super();
        configureValidation();
    }
    
    private void configureValidation() {
        // Set validation actions - require Username Token
        setValidationActions("UsernameToken");
        
        // Configure password callback handler for validation
        SimplePasswordValidationCallbackHandler callbackHandler = 
            new SimplePasswordValidationCallbackHandler();
        callbackHandler.setUsersMap(createUsersMap());
        setValidationCallbackHandler(callbackHandler);
    }
    
    private Map<String, String> createUsersMap() {
        Map<String, String> users = new HashMap<>();
        users.put(SOAP_USERNAME, SOAP_PASSWORD);
        return users;
    }
}
