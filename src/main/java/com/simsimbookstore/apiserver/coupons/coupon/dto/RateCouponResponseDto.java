package com.simsimbookstore.apiserver.coupons.coupon.dto;

import com.simsimbookstore.apiserver.coupons.coupon.entity.CouponStatus;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class RateCouponResponseDto implements CouponResponseDto{
    //쿠폰 Id
    private Long couponId;
    //쿠폰 발급 날짜
    private LocalDateTime issueDate;
    // 쿠폰 마감일
    private LocalDateTime deadline;
    // 쿠폰 상태 -> USED, EXPIRED, UNUSED
    private CouponStatus couponStatus;
    // 쿠폰 정책 이름
    private String couponTypeName;
    // 중복여부
    private boolean isStacking;
    // 쿠폰 적용 대상 -> ALL, CATEGORY, BOOK
    private CouponTargetType couponTargetType;
    // 쿠폰 적용 대상의 Id
    private Long couponTargetId; //추후 고민

    // 할인 형태
    private final DisCountType disCountType = DisCountType.RATE;
    // 할인율
    private BigDecimal discountRate;
    // 최대 할인 금액
    private BigDecimal maxDiscountAmount;
    // 최소 주문 금액
    private BigDecimal minOrderAmount;


    @Override
    public Object getDiscountInfo() {
        return Map.of(
                "discountRate",discountRate,
                "minOrderAmount",minOrderAmount,
                "maxDiscountAmount",maxDiscountAmount
        );
    }
}
