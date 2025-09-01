package com.globalbooks.catalog;

import com.globalbooks.catalog.generated.GetBookDetailsRequest;
import com.globalbooks.catalog.generated.GetBookDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class BookEndpoint {

    private static final String NAMESPACE_URI = "http://globalbooks.com/catalog";

    private final BookRepository bookRepository;

    @Autowired
    public BookEndpoint(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBookDetailsRequest")
    @ResponsePayload
    public GetBookDetailsResponse getBookDetails(@RequestPayload GetBookDetailsRequest request) {
        GetBookDetailsResponse response = new GetBookDetailsResponse();
        response.setBook(bookRepository.findBookById(request.getId()));
        return response;
    }
}
