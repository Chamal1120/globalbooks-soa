package com.globalbooks.orchestration.soap;

import com.globalbooks.orchestration.controller.OrderOrchestrationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

@Endpoint
@Component
public class OrderSoapEndpoint {

    private static final String NAMESPACE_URI = "http://globalbooks.com/orders";

    @Autowired
    private OrderOrchestrationController orderController;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessOrderRequest")
    @ResponsePayload
    public Element processOrder(@RequestPayload Element request) throws Exception {
        
        System.out.println("SOAP request received: " + request.getNodeName());
        
        // Extract values from SOAP request
        String customerId = getTextContent(request, "customerId");
        String bookId = getTextContent(request, "bookId");
        String quantityStr = getTextContent(request, "quantity");
        
        System.out.println("Extracted values: customerId=" + customerId + ", bookId=" + bookId + ", quantity=" + quantityStr);
        
        // Create order request map
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("customerId", customerId);
        orderRequest.put("bookId", bookId);
        orderRequest.put("quantity", quantityStr.isEmpty() ? 1 : Integer.parseInt(quantityStr));
        
        // Call existing order processing
        Map<String, Object> result;
        try {
            result = orderController.processOrder(orderRequest, "Bearer SOAP-CLIENT-TOKEN");
        } catch (Exception e) {
            result = new HashMap<>();
            result.put("status", "FAILED");
            result.put("message", "Order processing failed: " + e.getMessage());
            result.put("orderId", "ERROR");
        }
        
        // Create SOAP response
        return createResponse(result);
    }

    private String getTextContent(Element parent, String tagName) {
        // Try first with namespace
        Node node = parent.getElementsByTagNameNS(NAMESPACE_URI, tagName).item(0);
        if (node == null) {
            // Try without namespace
            node = parent.getElementsByTagName(tagName).item(0);
        }
        String content = node != null ? node.getTextContent() : "";
        System.out.println("getTextContent for " + tagName + ": '" + content + "'");
        return content;
    }

    private Element createResponse(Map<String, Object> result) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        
        Element response = doc.createElementNS(NAMESPACE_URI, "ProcessOrderResponse");
        
        Element orderIdElement = doc.createElement("orderId");
        orderIdElement.setTextContent(result.get("orderId") != null ? String.valueOf(result.get("orderId")) : "GENERATED");
        response.appendChild(orderIdElement);
        
        Element statusElement = doc.createElement("status");
        statusElement.setTextContent(String.valueOf(result.get("status")));
        response.appendChild(statusElement);
        
        Element messageElement = doc.createElement("message");
        messageElement.setTextContent(String.valueOf(result.get("message")));
        response.appendChild(messageElement);
        
        return response;
    }
}
