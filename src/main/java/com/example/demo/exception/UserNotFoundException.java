package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@Setter
@Getter
public class UserNotFoundException extends RuntimeException {

    private int status;

    private String message;

//    public UserNotFoundException(int status, String message) {
//        this.status = status;
//        this.message = message;
//    }

}

