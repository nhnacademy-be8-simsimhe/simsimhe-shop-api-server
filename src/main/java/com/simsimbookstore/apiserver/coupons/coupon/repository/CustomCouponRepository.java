package com.simsimbookstore.apiserver.coupons.coupon.repository;

import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomCouponRepository {
    List<Coupon> findEligibleCouponToBook(Long userId, Long bookId);

    Optional<Coupon> findUnusedCouponByUserAndType(Long userId, Long couponTypeId);
}
