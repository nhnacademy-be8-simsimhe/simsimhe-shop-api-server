package com.simsimbookstore.apiserver.coupons.mqConsumer;

import com.simsimbookstore.apiserver.coupons.coupon.dto.IssueCouponsRequestDto;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponMqConsumer {
    public static final String COUPON_QUEUE_NAME = "simsimbooks.coupon.issue.queue";
    private final CouponService couponService;

    @RabbitListener(queues = COUPON_QUEUE_NAME)
    public void issueBirthDayCoupon(IssueCouponsRequestDto requestDto) {
        couponService.issueCoupons(requestDto.getUserIds(), requestDto.getCouponTypeId());
    }
}
