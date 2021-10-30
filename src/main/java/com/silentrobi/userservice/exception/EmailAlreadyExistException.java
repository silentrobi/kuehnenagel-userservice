package com.silentrobi.userservice.exception;

public class EmailAlreadyExistException extends RuntimeException {
    private String message;
    public EmailAlreadyExistException(String message) {
        super(message);
        this.message = message;
    }
    public EmailAlreadyExistException() {
    }
}
