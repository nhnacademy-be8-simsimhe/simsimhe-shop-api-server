package com.simsimbookstore.apiserver.coupons.coupon.repository;

import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomCouponRepository {
    Page<Coupon> findEligibleCouponToBook(Pageable pageable, Long userId, Long bookId);

    List<Coupon> findUnusedCouponByUserAndType(Long userId, Long couponTypeId);
}
