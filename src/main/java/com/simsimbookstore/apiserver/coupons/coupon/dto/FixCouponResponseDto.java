package com.simsimbookstore.apiserver.coupons.coupon.dto;

import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
public class FixCouponResponseDto extends CouponResponseDto {

    // 할인 형태
    private final DisCountType disCountType = DisCountType.FIX;
    // 할인액
    private BigDecimal discountPrice;
    // 최소 주문 금액
    private BigDecimal minOrderAmount;

}
