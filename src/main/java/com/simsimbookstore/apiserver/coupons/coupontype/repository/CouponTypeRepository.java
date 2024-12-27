package com.simsimbookstore.apiserver.coupons.coupontype.repository;

import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponTypeRepository extends JpaRepository<CouponType,Long> {
}
