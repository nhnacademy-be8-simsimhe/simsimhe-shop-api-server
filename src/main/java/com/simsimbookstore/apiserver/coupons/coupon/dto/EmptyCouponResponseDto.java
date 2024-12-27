package com.simsimbookstore.apiserver.coupons.coupon.dto;

import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EmptyCouponResponseDto implements CouponResponseDto{
    private Long couponId;
    private LocalDateTime issueDate;
    private LocalDateTime deadline;
    private CouponStatus couponStatus;
    private String couponTypeName;
    private boolean isStacking;
    private CouponTargetType couponTargetType; // 추후 고민
    private Long couponTargetId; //추후 고민
    private final DisCountType disCountType = DisCountType.FIX;
    private BigDecimal discountPrice;
    private BigDecimal minOrderAmount;
    @Override
    public Long getCouponId() {
        return 0L;
    }

    @Override
    public LocalDateTime getIssueDate() {
        return null;
    }

    @Override
    public LocalDateTime getDeadline() {
        return null;
    }

    @Override
    public CouponStatus getCouponStatus() {
        return null;
    }

    @Override
    public String getCouponTypeName() {
        return "";
    }

    @Override
    public boolean isStacking() {
        return false;
    }

    @Override
    public CouponTargetType getCouponTargetType() {
        return null;
    }

    @Override
    public Long getCouponTargetId() {
        return 0L;
    }

    @Override
    public DisCountType getDisCountType() {
        return null;
    }

    @Override
    public Object getDiscountInfo() {
        return null;
    }
}
