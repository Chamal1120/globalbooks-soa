# Catalog Service SOAP Clients with WS-Security

## 🔐 **WS-Security Credentials**
- **Username**: `catalog-client`
- **Password**: `catalog-secure-2024`
- **Password Type**: `PasswordText`

## 📞 **SOAP Request Examples**

### **✅ Secure SOAP Request (With WS-Security)**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" 
               xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
    <soap:Header>
        <wsse:Security>
            <wsse:UsernameToken>
                <wsse:Username>catalog-client</wsse:Username>
                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">catalog-secure-2024</wsse:Password>
            </wsse:UsernameToken>
        </wsse:Security>
    </soap:Header>
    <soap:Body>
        <getBookDetailsRequest xmlns="http://globalbooks.com/catalog">
            <id>1</id>
        </getBookDetailsRequest>
    </soap:Body>
</soap:Envelope>
```

### **❌ Insecure SOAP Request (Will be Rejected)**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <getBookDetailsRequest xmlns="http://globalbooks.com/catalog">
            <id>1</id>
        </getBookDetailsRequest>
    </soap:Body>
</soap:Envelope>
```

## 🌐 **cURL Examples**

### **Secure SOAP Call**
```bash
curl -X POST http://localhost:8085/ws \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction:" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" 
               xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
    <soap:Header>
        <wsse:Security>
            <wsse:UsernameToken>
                <wsse:Username>catalog-client</wsse:Username>
                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">catalog-secure-2024</wsse:Password>
            </wsse:UsernameToken>
        </wsse:Security>
    </soap:Header>
    <soap:Body>
        <getBookDetailsRequest xmlns="http://globalbooks.com/catalog">
            <id>1</id>
        </getBookDetailsRequest>
    </soap:Body>
</soap:Envelope>'
```

### **REST Call (Unchanged - No Security)**
```bash
curl -X GET http://localhost:8085/api/books/1 \
  -H "Accept: application/json"
```

## 🔧 **SoapUI Configuration**

### **Project Setup**
1. **Create New SOAP Project**
2. **WSDL URL**: `http://localhost:8085/ws/books.wsdl`
3. **Service Name**: `BooksPortService`
4. **Operation**: `getBookDetails`

### **WS-Security Configuration**
1. **Right-click** on Request → **Add WS-Security**
2. **Select**: `Username Token`
3. **Username**: `catalog-client`
4. **Password**: `catalog-secure-2024`
5. **Password Type**: `Text`
6. **Add Nonce**: `false` (optional)
7. **Add Created**: `false` (optional)

## 🔍 **Testing Checklist**

### **✅ Expected Behaviors**
- [ ] REST API works without authentication
- [ ] SOAP without WS-Security is rejected (401/500)
- [ ] SOAP with correct credentials succeeds (200)
- [ ] SOAP with wrong credentials is rejected
- [ ] WSDL remains accessible
- [ ] Order Orchestration continues working
- [ ] Both endpoints return same data

### **❌ Failure Scenarios**
- [ ] REST API requires authentication (BAD)
- [ ] SOAP accepts requests without security (BAD)
- [ ] Order Orchestration breaks (CRITICAL)
- [ ] Data inconsistency between endpoints (BAD)

## 🚀 **Client Implementation Examples**

### **Java Spring WebServiceTemplate**
```java
@Component
public class SecureCatalogClient {
    
    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    public Book getBookDetails(String bookId) {
        // Configure WS-Security
        Wss4jSecurityInterceptor interceptor = new Wss4jSecurityInterceptor();
        interceptor.setSecurementActions("UsernameToken");
        interceptor.setSecurementUsername("catalog-client");
        interceptor.setSecurementPassword("catalog-secure-2024");
        
        webServiceTemplate.setInterceptors(new ClientInterceptor[]{interceptor});
        
        GetBookDetailsRequest request = new GetBookDetailsRequest();
        request.setId(bookId);
        
        GetBookDetailsResponse response = (GetBookDetailsResponse) 
            webServiceTemplate.marshalSendAndReceive(
                "http://localhost:8085/ws", request);
        
        return response.getBook();
    }
}
```

### **.NET WCF Client**
```csharp
public class SecureCatalogClient
{
    public Book GetBookDetails(string bookId)
    {
        var binding = new BasicHttpBinding(BasicHttpSecurityMode.Message);
        binding.Security.Message.ClientCredentialType = BasicHttpMessageCredentialType.UserName;
        
        var endpoint = new EndpointAddress("http://localhost:8085/ws");
        var client = new BooksPortClient(binding, endpoint);
        
        client.ClientCredentials.UserName.UserName = "catalog-client";
        client.ClientCredentials.UserName.Password = "catalog-secure-2024";
        
        var request = new getBookDetailsRequest { id = bookId };
        var response = client.getBookDetails(request);
        
        return response.book;
    }
}
```

## 🔒 **Security Notes**

### **Credentials Management**
- **Development**: Hardcoded credentials (as shown)
- **Production**: Use environment variables or secure configuration
- **Enterprise**: Integrate with LDAP/Active Directory

### **Password Types Supported**
- **PasswordText**: Plain text (current implementation)
- **PasswordDigest**: Hashed password (more secure)
- **X.509 Certificates**: Certificate-based authentication

### **Additional Security Options**
- **Timestamp validation**
- **Digital signatures**
- **Encryption**
- **SAML tokens**

## 📋 **Migration Checklist**

- [ ] Update catalog-service dependencies
- [ ] Configure WS-Security interceptor
- [ ] Test REST endpoints (should be unchanged)
- [ ] Test SOAP without security (should fail)
- [ ] Test SOAP with security (should succeed)
- [ ] Verify Order Orchestration still works
- [ ] Update client applications using SOAP
- [ ] Update documentation
- [ ] Deploy to staging environment
- [ ] Run full integration tests
