package com.simsimbookstore.apiserver.coupons.coupon.exception;

public class InsufficientOrderAmountException extends RuntimeException {
    public InsufficientOrderAmountException(String message) {
        super(message);
    }
}
