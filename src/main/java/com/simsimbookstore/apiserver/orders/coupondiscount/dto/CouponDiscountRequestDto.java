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
public class CouponDiscountRequestDto {

    private Long couponId;

    private String couponName; //쿠폰의 이름

    private String couponType; //쿠폰할인의 종류

    private BigDecimal discountPrice; //실제로 할인된 금액

}
