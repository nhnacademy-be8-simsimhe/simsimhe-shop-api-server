package com.simsimbookstore.apiserver.coupons.coupon.dto;

import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@SuperBuilder
public class EmptyCouponResponseDto extends CouponResponseDto{

}
