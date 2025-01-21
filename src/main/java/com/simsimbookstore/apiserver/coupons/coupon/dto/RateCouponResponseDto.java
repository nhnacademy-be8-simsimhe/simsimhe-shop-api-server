package com.simsimbookstore.apiserver.coupons.coupon.dto;

import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
public class RateCouponResponseDto extends CouponResponseDto {

    // 할인 형태
    private static final DisCountType disCountType = DisCountType.RATE;
    // 할인율
    private BigDecimal discountRate;
    // 최대 할인 금액
    private BigDecimal maxDiscountAmount;
    // 최소 주문 금액
    private BigDecimal minOrderAmount;


}
