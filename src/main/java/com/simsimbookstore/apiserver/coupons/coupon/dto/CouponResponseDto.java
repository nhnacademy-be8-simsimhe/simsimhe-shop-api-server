package com.simsimbookstore.apiserver.coupons.coupon.dto;

import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CouponResponseDto {
    Long getCouponId();
    LocalDateTime getIssueDate();
    LocalDateTime getDeadline();
    CouponStatus getCouponStatus();
    String getCouponTypeName();
    boolean isStacking();
    CouponTargetType getCouponTargetType();
    Long getCouponTargetId();
    DisCountType getDisCountType();
    Object getDiscountInfo();
}
