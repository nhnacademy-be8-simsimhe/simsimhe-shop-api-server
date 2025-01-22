package com.simsimbookstore.apiserver.orders.coupondiscount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponDiscountResponseDto {
    private Long couponDiscountId;
    private Long orderBookId;
    private String couponName;
    private String couponType;
    private BigDecimal discountPrice;
}

