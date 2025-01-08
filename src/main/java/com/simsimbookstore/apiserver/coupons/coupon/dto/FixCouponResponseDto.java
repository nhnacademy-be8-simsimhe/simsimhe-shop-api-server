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
public class FixCouponResponseDto extends CouponResponseDto{

    // 할인 형태
    private final DisCountType disCountType = DisCountType.FIX;
    // 할인액
    private BigDecimal discountPrice;
    // 최소 주문 금액
    private BigDecimal minOrderAmount;

}
