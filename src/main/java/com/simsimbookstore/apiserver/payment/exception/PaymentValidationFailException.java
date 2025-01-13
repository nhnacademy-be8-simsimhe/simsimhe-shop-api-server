package com.simsimbookstore.apiserver.payment.exception;

public class PaymentValidationFailException extends RuntimeException {
    public PaymentValidationFailException(String message) {
        super(message);
    }
}
