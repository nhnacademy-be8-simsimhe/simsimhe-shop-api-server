package com.simsimbookstore.apiserver.coupons.coupon.exception;

public class InapplicableCoupon extends RuntimeException {
    public InapplicableCoupon(String message) {
        super(message);
    }
}
