package com.simsimbookstore.apiserver.coupons.coupontype.dto;

import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Builder
@Data
public class CouponTypeRequestDto {
    @NotBlank(message = "쿠폰 종류 이름을 입력해주세요")
    @Length(max = 40,message = "쿠폰 종류 이름은 최대 40자까지 입력 가능합니다.")
    private String couponTypeName;

    private Integer period;

    private LocalDateTime deadline;

    private Boolean stacking;
    @NotBlank(message = "쿠폰 정책을 선택해주세요")
    @Min(value = 1,message = "ID는 1이상이어야 합니다.")
    private Long couponPolicyId;

    @NotBlank(message = "쿠폰 타입을 입력해주세요")
    private CouponTargetType couponTargetType;

    private Long targetId; //BookId , CategoryID
}
