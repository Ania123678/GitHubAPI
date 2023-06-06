package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@Setter
@Getter
public class MediaTypeNotAcceptableException extends RuntimeException {

    private int status;

    private String message;

}
