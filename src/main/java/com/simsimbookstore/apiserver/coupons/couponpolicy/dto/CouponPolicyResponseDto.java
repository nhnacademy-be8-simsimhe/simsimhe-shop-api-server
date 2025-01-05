package com.simsimbookstore.apiserver.coupons.couponpolicy.dto;

import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CouponPolicyResponseDto {
    private Long couponPolicyId;

    private String couponPolicyName;

    private DisCountType disCountType; // RATE, FIX

    private BigDecimal discountRate;

    private BigDecimal discountPrice;

    private BigDecimal maxDiscountAmount;

    private BigDecimal minOrderAMount;

    private String policyDescription;
}
