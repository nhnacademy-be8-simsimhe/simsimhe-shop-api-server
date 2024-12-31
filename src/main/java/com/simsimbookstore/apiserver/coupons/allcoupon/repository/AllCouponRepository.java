package com.simsimbookstore.apiserver.coupons.allcoupon.repository;

import com.simsimbookstore.apiserver.coupons.allcoupon.entity.AllCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllCouponRepository extends JpaRepository<AllCoupon,Long> {
}
