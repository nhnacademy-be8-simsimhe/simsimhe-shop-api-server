package com.simsimbookstore.apiserver.coupons.exception;

public class AlreadyCouponUsed extends RuntimeException {
    public AlreadyCouponUsed(String message) {
        super(message);
    }
}