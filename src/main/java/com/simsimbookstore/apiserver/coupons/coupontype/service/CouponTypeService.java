package com.simsimbookstore.apiserver.coupons.coupontype.service;

import com.simsimbookstore.apiserver.coupons.coupon.dto.CouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeRequestDto;
import com.simsimbookstore.apiserver.coupons.coupontype.dto.CouponTypeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponTypeService {
    //모든 쿠폰 타입을 가지고 온다.
    Page<CouponTypeResponseDto> getAllCouponType(Pageable pageable);
    //쿠폰 타입을 하나 가져온다.
    CouponTypeResponseDto getCouponType(Long couponTypeId);
    //특정 쿠폰 정책에 해당하는 쿠폰 타입을 가져온다.
    Page<CouponTypeResponseDto> getCouponByCouponPolicy(Pageable pageable,Long couponPolicyId);

    CouponTypeResponseDto createCouponType(CouponTypeRequestDto requestDto);
    //쿠폰 타입을 삭제한다.
    void deleteCouponType(Long CouponTypeId);
}
