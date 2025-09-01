package com.globalbooks.gateway.client;

import com.globalbooks.gateway.generated.GetBookDetailsRequest;
import com.globalbooks.gateway.generated.GetBookDetailsResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

public class CatalogClient extends WebServiceGatewaySupport {

    public GetBookDetailsResponse getBookDetails(String id) {
        GetBookDetailsRequest request = new GetBookDetailsRequest();
        request.setId(id);

        return (GetBookDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request,
                        new SoapActionCallback("http://globalbooks.com/catalog/getBookDetailsRequest"));
    }
}
