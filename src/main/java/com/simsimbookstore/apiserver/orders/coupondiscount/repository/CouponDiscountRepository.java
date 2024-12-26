package com.simsimbookstore.apiserver.orders.coupondiscount.repository;

import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponDiscountRepository extends JpaRepository<CouponDiscount, Long> {
}
