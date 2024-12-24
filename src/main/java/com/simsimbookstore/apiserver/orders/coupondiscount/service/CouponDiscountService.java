package com.simsimbookstore.apiserver.orders.coupondiscount.service;

import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountRequestDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import java.util.List;

public interface CouponDiscountService {

    CouponDiscountResponseDto createCouponDiscount(CouponDiscountRequestDto requestDto);

    CouponDiscountResponseDto findById(Long id);

    void deleteById(Long id);
}
