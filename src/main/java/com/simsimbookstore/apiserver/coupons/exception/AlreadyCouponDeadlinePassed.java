package com.simsimbookstore.apiserver.coupons.exception;

public class AlreadyCouponDeadlinePassed extends RuntimeException {
    public AlreadyCouponDeadlinePassed(String message) {
        super(message);
    }
}
