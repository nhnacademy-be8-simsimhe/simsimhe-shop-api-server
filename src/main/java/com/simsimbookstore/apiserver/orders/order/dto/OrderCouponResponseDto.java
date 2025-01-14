package com.simsimbookstore.apiserver.orders.order.dto;

import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCouponResponseDto {
    private Long couponId;
    private String couponTypeName;
    private DisCountType discountType;

//    @Override
//    public String toString() {
//        return "OrderCouponResponseDto{" +
//                "couponId=" + couponId +
//                ", couponTypeName='" + couponTypeName + '\'' +
//                '}';
//    }
}

