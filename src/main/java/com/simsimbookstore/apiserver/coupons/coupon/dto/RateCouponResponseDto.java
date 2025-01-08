package com.simsimbookstore.apiserver.coupons.coupon.dto;

import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@SuperBuilder
public class RateCouponResponseDto extends CouponResponseDto{

    // 할인 형태
    private final DisCountType disCountType = DisCountType.RATE;
    // 할인율
    private BigDecimal discountRate;
    // 최대 할인 금액
    private BigDecimal maxDiscountAmount;
    // 최소 주문 금액
    private BigDecimal minOrderAmount;


}
