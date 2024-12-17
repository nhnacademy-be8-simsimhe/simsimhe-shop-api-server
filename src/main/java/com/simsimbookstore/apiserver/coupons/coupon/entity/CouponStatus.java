package com.simsimbookstore.apiserver.coupons.coupon.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum CouponStatus {
    USED("사용"),
    EXPIRED("만료"),
    UNUSED("미사용");

    private final String name;

    CouponStatus(String name) {
        this.name = name;
    }
}
