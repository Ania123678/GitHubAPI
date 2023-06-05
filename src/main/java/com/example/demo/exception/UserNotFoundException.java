package com.example.demo.exception;

public class UserNotFoundException extends RuntimeException {

    private int status;
    private String message;

    public UserNotFoundException(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

