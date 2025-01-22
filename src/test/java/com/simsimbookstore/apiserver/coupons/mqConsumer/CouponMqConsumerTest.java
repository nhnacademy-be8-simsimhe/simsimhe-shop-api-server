package com.simsimbookstore.apiserver.coupons.mqConsumer;

import com.simsimbookstore.apiserver.coupons.coupon.dto.IssueCouponsRequestDto;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


class CouponMqConsumerTest {
    @ExtendWith(MockitoExtension.class)

        @Mock
        private CouponService couponService;

        @InjectMocks
        private CouponMqConsumer couponMqConsumer;

        @Test
        @DisplayName("issueCoupon : 유저에게 쿠폰 발급 메시지 처리")
        void testIssueCoupon() {
            // Given
            IssueCouponsRequestDto requestDto = IssueCouponsRequestDto.builder()
                    .userIds(List.of(1L, 2L, 3L))
                    .couponTypeId(100L)
                    .build();

            // When
            couponMqConsumer.issueCoupon(requestDto);

            // Then
            verify(couponService, times(1)).issueCoupons(requestDto.getUserIds(), requestDto.getCouponTypeId());
        }

        @Test
        @DisplayName("expireCoupon : 쿠폰 만료 메시지 처리")
        void testExpireCoupon() {
            // Given
            Map<String, Long> requestDto = Map.of(
                    "userId", 1L,
                    "couponId", 200L
            );

            // When
            couponMqConsumer.expireCoupon(requestDto);

            // Then
            verify(couponService, times(1)).expireCoupon(1L, 200L);
        }

        @Test
        @DisplayName("deleteCoupon : 쿠폰 삭제 메시지 처리")
        void testDeleteCoupon() {
            // Given
            Map<String, Long> requestDto = Map.of(
                    "userId", 1L,
                    "couponId", 200L
            );

            // When
            couponMqConsumer.deleteCoupon(requestDto);

            // Then
            verify(couponService, times(1)).deleteCoupon(1L, 200L);
        }
    }

