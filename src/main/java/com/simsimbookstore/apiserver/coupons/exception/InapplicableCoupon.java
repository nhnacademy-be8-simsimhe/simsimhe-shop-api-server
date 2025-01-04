package com.simsimbookstore.apiserver.coupons.exception;

public class InapplicableCoupon extends RuntimeException {
    public InapplicableCoupon(String message) {
        super(message);
    }
}
