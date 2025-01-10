package com.simsimbookstore.apiserver.coupons.exception;

public class InsufficientOrderAmountException extends RuntimeException {
    public InsufficientOrderAmountException(String message) {
        super(message);
    }
}
