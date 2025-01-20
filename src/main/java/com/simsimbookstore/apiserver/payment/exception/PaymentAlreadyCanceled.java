package com.simsimbookstore.apiserver.payment.exception;

public class PaymentAlreadyCanceled extends RuntimeException {
    public PaymentAlreadyCanceled(String message) {
        super(message);
    }
}
