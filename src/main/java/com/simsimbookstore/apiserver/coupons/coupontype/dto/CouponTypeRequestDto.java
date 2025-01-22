package com.simsimbookstore.apiserver.coupons.coupontype.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.simsimbookstore.apiserver.coupons.coupontype.entity.CouponTargetType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Data
public class CouponTypeRequestDto {
    @NotBlank(message = "쿠폰 종류 이름을 입력해주세요")
    @Length(max = 40,message = "쿠폰 종류 이름은 최대 40자까지 입력 가능합니다.")
    private String couponTypeName;
    private Long targetId; //BookId , CategoryID

    private Integer period;

    private LocalDateTime deadline;

    private Boolean stacking;

    @NotNull(message = "쿠폰 정책을 선택해주세요")
    @Min(value = 1,message = "ID는 1이상이어야 합니다.")
    private Long couponPolicyId;

    @NotNull(message = "쿠폰 타입을 입력해주세요")
    private CouponTargetType couponTargetType;


    /**
     * period와 deadline 중 하나는 null이어야 함.
     */
    @AssertTrue(message = "period와 deadline은 서로 XOR 관계입니다.")
    public boolean isValidPeriodAndDeadline() {
        boolean isPeriodNull = (this.period == null);
        boolean isDeadlineNull = (this.deadline == null);
        return isPeriodNull ^ isDeadlineNull;
    }

    /**
     * AllCoupon이면 targetId는 null이어야함
     */
    @AssertTrue(message = "ALL 쿠폰은 TargetId를 가질 수 없습니다.")
    public boolean isValidAllCouponTargetId() {

        if (couponTargetType == CouponTargetType.ALL) {
            return Objects.isNull(targetId);
        }
        return true;
    }
}
