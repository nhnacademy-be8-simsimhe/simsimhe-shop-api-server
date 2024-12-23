package com.simsimbookstore.apiserver.orders.delivery.exception;

public class ReturnsNotFoundException extends RuntimeException{
    public ReturnsNotFoundException(String message) {
        super(message);
    }
}
