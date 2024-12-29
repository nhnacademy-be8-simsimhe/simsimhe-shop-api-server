package com.simsimbookstore.apiserver.coupons.coupon.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IssueCouponsRequestDto {
    @NotEmpty(message = "회원 ID 목록은 필수이며 최소 하나 이상의 값이 필요합니다.")
    private List<@NotNull(message = "회원 ID는 null일 수 없습니다.") @Positive(message = "회원 ID는 0보다 커야합니다") Long> userIds;
    @NotNull(message = "쿠폰 타입 ID는 필수입니다.")
    private Long couponTypeId;
}
