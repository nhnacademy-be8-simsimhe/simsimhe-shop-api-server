package com.simsimbookstore.apiserver.orders.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUsageDto {
    private Long bookId;            // 책 ID
    private Long couponId;          // 사용한 쿠폰 ID
    private String couponName;      // 쿠폰 이름
    private BigDecimal discount;    // 할인 금액
}
