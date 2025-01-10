package com.simsimbookstore.apiserver.coupons.coupon.service;

import com.simsimbookstore.apiserver.coupons.coupon.dto.CouponResponseDto;
import com.simsimbookstore.apiserver.coupons.coupon.dto.DiscountAmountResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponService {
    // couponId로 쿠폰을 가져온다.
    CouponResponseDto getCouponById( Long couponId);
    // couponType의 사용되지 않은 쿠폰을 가져옵니다.
    CouponResponseDto getUnusedCouponByCouponType(Long userId, Long couponTypeId);

    //user의 쿠폰을 가져온다.
    Page<CouponResponseDto> getCoupons(Pageable pageable, Long userId);

    //user의 쿠폰 중 미사용된 쿠폰을 가져온다.
    Page<CouponResponseDto> getUnusedCoupons(Pageable pageable, Long userId);

    //user의 적용 가능한 쿠폰울 가져온다.
    List<CouponResponseDto> getEligibleCoupons(Long userId, Long bookId);

    // user들에게 쿠폰을 발급한다.
    List<Long> issueCoupons(List<Long> userIds, Long couponTypeId);

    // 쿠폰종류에 해당하는 모든 쿠폰을 만료시킨다.
//    void expireAllCoupons(Long couponTypeId); <- couponTypeService에서

    // user의 쿠폰을 만료시킨다.
    CouponResponseDto expireCoupon(Long userId, Long couponId);

    // user의 쿠폰을 사용한다.
    CouponResponseDto useCoupon(Long userId, Long couponId);

    //user의 쿠폰을 삭제한다.
    void deleteCoupon(Long userId, Long couponId);

    DiscountAmountResponseDto calDiscountAmount(Long bookId, Integer quantity, Long couponId);


}
