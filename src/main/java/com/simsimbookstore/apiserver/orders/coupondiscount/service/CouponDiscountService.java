package com.simsimbookstore.apiserver.orders.coupondiscount.service;

import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountRequestDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.UserCouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponDiscountService {

    CouponDiscountResponseDto createCouponDiscount(CouponDiscountRequestDto requestDto, OrderBook orderBook);

    CouponDiscountResponseDto findById(Long id);

    void deleteById(Long id);

    Page<UserCouponDiscountResponseDto> getUserCouponDiscount(Long userId, Pageable pageable);
}
