package com.simsimbookstore.apiserver.coupons.exception;

public class AlreadyCouponPolicyUsed extends RuntimeException {
    public AlreadyCouponPolicyUsed(String message) {
        super(message);
    }
}
