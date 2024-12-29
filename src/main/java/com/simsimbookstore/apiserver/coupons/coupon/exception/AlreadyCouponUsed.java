package com.simsimbookstore.apiserver.coupons.coupon.exception;

public class AlreadyCouponUsed extends RuntimeException {
    public AlreadyCouponUsed(String message) {
        super(message);
    }
}
