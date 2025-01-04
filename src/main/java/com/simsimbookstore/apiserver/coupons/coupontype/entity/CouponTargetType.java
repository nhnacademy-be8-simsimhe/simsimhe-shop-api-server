package com.simsimbookstore.apiserver.coupons.coupontype.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
public enum CouponTargetType {
     ALL, CATEGORY, BOOK
}
