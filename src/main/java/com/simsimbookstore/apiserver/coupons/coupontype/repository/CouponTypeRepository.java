package com.simsimbookstore.apiserver.coupons.coupontype.repository;

import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponTypeRepository extends JpaRepository<CouponType,Long> {
    Page<CouponType> findByCouponPolicyCouponPolicyId(Pageable pageable, Long couponPolicyId);

    List<CouponType> findByCouponPolicyCouponPolicyId(Long couponPolicyId);
}
