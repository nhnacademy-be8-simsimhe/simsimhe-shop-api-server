package com.simsimbookstore.apiserver.coupons.coupontype.dto;

import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CouponTypeResponseDto {
    private Long couponTypeId;

    private String couponTypeName;

    private Integer period;

    private LocalDateTime deadline;

    private boolean stacking;

    private Long couponPolicyId;

    private CouponTargetType couponTypes; //ALL, CATEGORY, BOOK

    private Long couponTargetId;// 타겟의 Id

    private String couponTargetName; // 카테고리 이름, 책 이름

}
