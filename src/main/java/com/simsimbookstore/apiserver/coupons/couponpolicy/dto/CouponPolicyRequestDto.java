package com.simsimbookstore.apiserver.coupons.couponpolicy.dto;

import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Builder
public class CouponPolicyRequestDto {
    @NotBlank(message = "쿠폰 정책 이름을 입력해주세요")
    @Length(max = 100, message = "쿠폰 정책 이름은 최대 100자까지 입력 가능합니다.")
    private String couponPolicyName;

    @NotNull(message = "할인 형태는 필수 입력값입니다.")
    private DisCountType discountType;

    private BigDecimal discountPrice;

    private BigDecimal discountRate;

    private BigDecimal maxDiscountAmount;

    private BigDecimal minOrderAmount;
    @NotBlank(message = "쿠폰 정책에 대한 설명을 입력해주세요")
    @Length(max = 1000, message = "쿠폰 정책 설명은 최대 1000자까지 입력 가능합니다.")
    private String policyDescription;

    /**
     * 1) discountType == RATE
     * -> discountRate, maxDiscountAmount가 Notnull이고 discountPrice는 Null 이어야함
     * 2) discountType == FIX
     * -> discountPrice가 Notnull이고 discountRate, maxDiscountAmount은 null
     */
    @AssertTrue
    public boolean isDiscountValueValid() {
        if (discountType == DisCountType.RATE) {
            // null체크
            if (Objects.isNull(discountRate) || Objects.isNull(maxDiscountAmount)) {
                return false;
            }

            if (Objects.nonNull(discountPrice)) {
                return false;
            }
            // 0과 음수 체크
            if (discountRate.signum() <= 0 || maxDiscountAmount.signum() <= 0) {
                return false;
            }
        } else if (discountType == DisCountType.FIX) {
            if (Objects.isNull(discountPrice)) {
                return false;
            }
            if (Objects.nonNull(discountRate) || Objects.nonNull(maxDiscountAmount)) {
                return false;
            }
            if (discountPrice.signum() <= 0) {
                return false;
            }
        }
        return true;
    }

}
