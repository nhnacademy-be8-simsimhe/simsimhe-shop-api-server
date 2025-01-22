package com.simsimbookstore.apiserver.coupons.couponpolicy.mapper;

import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyRequestDto;
import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyResponseDto;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;

public class CouponPolicyMapper {
    public static CouponPolicyResponseDto toResponse(CouponPolicy couponPolicy) {
        return CouponPolicyResponseDto.builder()
                .couponPolicyId(couponPolicy.getCouponPolicyId())
                .couponPolicyName(couponPolicy.getCouponPolicyName())
                .discountType(couponPolicy.getDiscountType())
                .discountRate(couponPolicy.getDiscountRate())
                .discountPrice(couponPolicy.getDiscountPrice())
                .maxDiscountAmount(couponPolicy.getMaxDiscountAmount())
                .minOrderAMount(couponPolicy.getMinOrderAmount())
                .policyDescription(couponPolicy.getPolicyDescription())
                .build();
    }

    public static CouponPolicy toCouponPolicy(CouponPolicyRequestDto requestDto) {
        return CouponPolicy.builder()
                .couponPolicyName(requestDto.getCouponPolicyName())
                .discountType(requestDto.getDiscountType())
                .discountPrice(requestDto.getDiscountPrice())
                .discountRate(requestDto.getDiscountRate())
                .maxDiscountAmount(requestDto.getMaxDiscountAmount())
                .minOrderAmount(requestDto.getMinOrderAmount())
                .policyDescription(requestDto.getPolicyDescription())
                .build();
    }
}
