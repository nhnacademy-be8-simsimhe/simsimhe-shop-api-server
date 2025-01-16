package com.simsimbookstore.apiserver.coupons.mqConsumer;

import com.simsimbookstore.apiserver.coupons.coupon.dto.IssueCouponsRequestDto;
import com.simsimbookstore.apiserver.coupons.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CouponMqConsumer {
    public static final String COUPON_ISSUE_QUEUE_NAME = "simsimbooks.coupon.issue.queue";
    public static final String COUPON_EXPIRE_QUEUE_NAME = "simsimbooks.coupon.expire.queue";
    public static final String COUPON_DELETE_QUEUE_NAME = "simsimbooks.coupon.delete.queue";

    private final CouponService couponService;

    @RabbitListener(queues = COUPON_ISSUE_QUEUE_NAME)
    public void issueCoupon(IssueCouponsRequestDto requestDto) {
        couponService.issueCoupons(requestDto.getUserIds(), requestDto.getCouponTypeId());
        // 실패했을 때 어떻게 MQ 보상처리 할 것인지
    }

    @RabbitListener(queues = COUPON_EXPIRE_QUEUE_NAME)
    public void expireCoupon(Map<String, Long> requestDto) {
        couponService.expireCoupon(requestDto.get("userId"), requestDto.get("couponId"));
    }

    @RabbitListener(queues = COUPON_DELETE_QUEUE_NAME)
    public void deleteCoupon(Map<String, Long> requestDto) {
        couponService.deleteCoupon(requestDto.get("userId"), requestDto.get("couponId"));
    }
}
