package com.simsimbookstore.apiserver.coupons.coupon.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
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

    // 역직렬화 : JSON -> Enum
    @JsonCreator
    public static DisCountType from(String value) {
        return DisCountType.valueOf(value.toUpperCase());
    }
    // 직렬화 : Enum -> JSON
    @JsonValue
    public String getName() {
        return name;
    }
}
