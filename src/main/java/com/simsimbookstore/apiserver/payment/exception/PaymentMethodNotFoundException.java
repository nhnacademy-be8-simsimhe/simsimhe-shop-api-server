package com.simsimbookstore.apiserver.payment.exception;

import com.simsimbookstore.apiserver.payment.entity.Payment;

public class PaymentMethodNotFoundException extends RuntimeException {
    public PaymentMethodNotFoundException(String message) {
        super(message);
    }
}
