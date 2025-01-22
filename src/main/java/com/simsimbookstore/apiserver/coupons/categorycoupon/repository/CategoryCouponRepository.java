package com.simsimbookstore.apiserver.coupons.categorycoupon.repository;

import com.simsimbookstore.apiserver.coupons.categorycoupon.entity.CategoryCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon,Long> {
}
