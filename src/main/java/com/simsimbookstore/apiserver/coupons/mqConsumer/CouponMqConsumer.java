package com.simsimbookstore.apiserver.coupons.mqConsumer;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CouponMqConsumer {
    public static final String COUPON_QUEUE_NAME = "simsimbooks.coupon.queue";

    @RabbitListener(queues = COUPON_QUEUE_NAME)
    public void receiver(String msg) {
        System.out.println(msg);
    }
}
