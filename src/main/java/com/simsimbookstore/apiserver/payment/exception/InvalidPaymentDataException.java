package com.simsimbookstore.apiserver.payment.exception;

public class InvalidPaymentDataException extends RuntimeException {
    public InvalidPaymentDataException(String s) {
        super(s);
    }
}
