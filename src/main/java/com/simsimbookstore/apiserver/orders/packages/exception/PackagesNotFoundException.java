package com.simsimbookstore.apiserver.orders.packages.exception;

public class PackagesNotFoundException extends RuntimeException{
    public PackagesNotFoundException(String message) {
        super(message);
    }
}
