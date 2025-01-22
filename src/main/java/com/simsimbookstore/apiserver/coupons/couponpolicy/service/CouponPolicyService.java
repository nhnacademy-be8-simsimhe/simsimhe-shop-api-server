package com.simsimbookstore.apiserver.coupons.couponpolicy.service;

import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyRequestDto;
import com.simsimbookstore.apiserver.coupons.couponpolicy.dto.CouponPolicyResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponPolicyService {
    // 모든 쿠폰 정책을 가지고 온다.
    Page<CouponPolicyResponseDto> getAllCouponPolicy(Pageable pageable);
    // 쿠폰 정책을 하나 가지고 온다.
    CouponPolicyResponseDto getCouponPolicy(Long couponPolicyId);
    //쿠폰 정책을 생성한다.
    CouponPolicyResponseDto createCouponPolicy(CouponPolicyRequestDto requestDto);
    // 쿠폰 정책의 정보를 변경한다.
//    CouponPolicyResponseDto updateCouponPolicy(Long couponPolicyId, CouponPolicyRequestDto requestDto);
    // 쿠폰 정책을 삭제한다.
    void deleteCouponPolicy(Long couponPolicyId);

}
