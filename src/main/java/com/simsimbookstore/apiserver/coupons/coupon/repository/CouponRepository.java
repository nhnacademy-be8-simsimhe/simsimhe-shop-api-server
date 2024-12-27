package com.simsimbookstore.apiserver.coupons.coupon.repository;

import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon,Long>, CustomCouponRepository {
    Page<Coupon> findByUserUserId(Pageable pageable,Long userId);

    Page<Coupon> findByUserUserIdAndCouponStatusOrderByIssueDate(Pageable pageable, Long userId, CouponStatus couponStatus);

    Optional<Coupon> findByUserUserIdAndCouponId(Long userId, Long couponId);
}