package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@Setter
@Getter
public class ErrorJsonResponse {

    private int status;

    private String message;


}

