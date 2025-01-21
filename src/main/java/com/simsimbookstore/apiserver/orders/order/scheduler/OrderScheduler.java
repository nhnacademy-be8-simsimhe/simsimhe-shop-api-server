package com.simsimbookstore.apiserver.orders.order.scheduler;

import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final MemberOrderService orderService;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        return scheduler;
    }


    // 매일 05마다 실행
    @Scheduled(cron = "0 0 5 * * ?")
    public int completeOldDeliveryOrders() {
        // 30일 이전의 주문을 COMPLETED 상태로 업데이트
        log.info("Completing old delivery orders");
        return orderService.updateOrdersToCompleted();
    }


    @Scheduled(cron = "0 10,20,30,40,50 * * * ?")
    public void removeExpiredPendingOrders() {
        int removedCount = orderService.removeExpiredPendingOrders(10); // 10분 기준
        log.info("[Scheduler] Removed {} expired pending orders", removedCount);
    }


}