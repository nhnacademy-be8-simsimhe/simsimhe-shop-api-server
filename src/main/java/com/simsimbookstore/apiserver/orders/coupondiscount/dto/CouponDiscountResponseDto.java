package com.simsimbookstore.apiserver.orders.coupondiscount.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

