package com.simsimbookstore.apiserver.coupons.coupon.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Data
public class DiscountAmountResponseDto {
    private Long bookId;
    private Integer quantity;
    private BigDecimal discountAmount;
    //쿠폰을 적용하기 전 금액
    private BigDecimal beforeCouponDiscount;
    // 쿠폰 적용 후 금액
    private BigDecimal afterCouponDiscount;
}
