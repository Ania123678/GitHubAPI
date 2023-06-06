package com.example.demo.handlers;

import com.example.demo.exception.ErrorJsonResponse;
import com.example.demo.exception.MediaTypeNotAcceptableException;
import com.example.demo.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class CustomErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorJsonResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorJsonResponse errorResponse = new ErrorJsonResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorJsonResponse> handleMediaTypeNotAcceptable() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ErrorJsonResponse response = new ErrorJsonResponse(HttpStatus.NOT_ACCEPTABLE.value(), "Not Acceptable");
        return new ResponseEntity<>(response, headers, HttpStatus.NOT_ACCEPTABLE);
    }

}

