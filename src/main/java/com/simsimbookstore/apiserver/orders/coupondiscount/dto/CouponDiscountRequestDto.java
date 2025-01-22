package com.simsimbookstore.apiserver.orders.coupondiscount.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
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
