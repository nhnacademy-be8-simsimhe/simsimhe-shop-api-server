package com.simsimbookstore.apiserver.coupons.dto;

import com.simsimbookstore.apiserver.coupons.coupon.dto.FixCouponResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class CouponResponseDtoTest {
    @Test
    @DisplayName("FixResponseDtoSetter 테스트")
    void FixResponseDtoSetterTest() {
        FixCouponResponseDto fixCouponResponseDto = FixCouponResponseDto.builder().build();

        BigDecimal discountPrice = BigDecimal.valueOf(5000);
        BigDecimal minOrderAmount = BigDecimal.valueOf(10000);

        fixCouponResponseDto.setDiscountPrice(discountPrice);
        fixCouponResponseDto.setMinOrderAmount(minOrderAmount);

        Assertions.assertEquals(discountPrice, fixCouponResponseDto.getDiscountPrice());
        Assertions.assertEquals(minOrderAmount,fixCouponResponseDto.getMinOrderAmount());
    }

//    @Test
//    @DisplayName()
}
