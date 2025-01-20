package com.simsimbookstore.apiserver.payment.exception;

public class PaymentNotExistException extends RuntimeException {
    public PaymentNotExistException(String s) {
        super(s);
    }
}
