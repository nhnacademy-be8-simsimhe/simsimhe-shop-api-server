package com.simsimbookstore.apiserver.orders.coupondiscount.repository;

import com.simsimbookstore.apiserver.orders.coupondiscount.entity.CouponDiscount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCouponDiscountRepository {
    Page<CouponDiscount> getUserCouponDiscount(Long userId, Pageable pageable);
}
