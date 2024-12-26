package com.simsimbookstore.apiserver.coupons.couponpolicy.repository;

import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.CouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy,Long> {
}
