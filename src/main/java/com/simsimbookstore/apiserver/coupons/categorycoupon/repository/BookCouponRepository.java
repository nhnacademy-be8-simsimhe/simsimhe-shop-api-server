package com.simsimbookstore.apiserver.coupons.categorycoupon.repository;

import com.simsimbookstore.apiserver.coupons.bookcoupon.entity.BookCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCouponRepository extends JpaRepository<BookCoupon,Long> {
}
