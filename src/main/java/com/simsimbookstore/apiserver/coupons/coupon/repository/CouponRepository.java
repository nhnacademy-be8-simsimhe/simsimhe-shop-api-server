package com.simsimbookstore.apiserver.coupons.coupon.repository;

import com.simsimbookstore.apiserver.coupons.coupon.entity.Coupon;
import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon,Long>, CustomCouponRepository {
    Page<Coupon> findByUserUserId(Pageable pageable,Long userId);

    @Query("SELECT c FROM Coupon c WHERE c.user.userId = :userId AND c.couponStatus = :couponStatus AND c.deadline < CURRENT_TIMESTAMP")
    Page<Coupon> findByUserUserIdAndCouponStatusAndDeadlineBeforeNow(
            @Param("userId") Long userId,
            @Param("couponStatus") CouponStatus couponStatus,
            Pageable pageable
    );

    Optional<Coupon> findByUserUserIdAndCouponId(Long userId, Long couponId);

    List<Coupon> findByCouponTypeCouponTypeId(Long couponTypeId);

    List<Coupon> findByCouponStatus(CouponStatus couponStatus);

    @Query("SELECT c FROM Coupon c WHERE c.couponStatus = 'UNUSED' AND c.deadline < CURRENT_TIMESTAMP")
    List<Coupon> findUnusedAndExpiredCoupons();
}

