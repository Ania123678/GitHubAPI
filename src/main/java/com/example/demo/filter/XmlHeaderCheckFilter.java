package com.example.demo.filter;

import com.example.demo.exception.MediaTypeNotAcceptableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class XmlHeaderCheckFilter implements ExchangeFilterFunction {

    private static final String ACCEPT_HEADER_NAME = "Accept";
    private static final MediaType XML_MEDIA_TYPE = MediaType.APPLICATION_XML;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        HttpHeaders requestHeaders = request.headers();

        if (requestHeaders.containsKey(ACCEPT_HEADER_NAME)) {
            MediaType acceptMediaType = requestHeaders.getAccept().stream().findFirst().orElse(null);

            if (acceptMediaType != null && acceptMediaType.isCompatibleWith(XML_MEDIA_TYPE)) {
                throw new MediaTypeNotAcceptableException(HttpStatus.NOT_ACCEPTABLE.value(), "Not Acceptable");
            }
        }

        return next.exchange(request);
    }
}