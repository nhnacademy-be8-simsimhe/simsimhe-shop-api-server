package com.simsimbookstore.apiserver.coupons.exception;

public class AlreadyCouponTypeIssue extends RuntimeException {
    public AlreadyCouponTypeIssue(String message) {
        super(message);
    }
}
