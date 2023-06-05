package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotAccetableResponse extends ResponseStatusException {

    public NotAccetableResponse() {
        super(HttpStatus.NOT_ACCEPTABLE);
    }
}