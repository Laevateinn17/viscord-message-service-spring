package com.viscord.message_service.exception;

public class NotFoundException  extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
