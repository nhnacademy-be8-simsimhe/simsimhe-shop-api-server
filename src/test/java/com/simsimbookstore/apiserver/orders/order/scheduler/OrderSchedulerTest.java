package com.simsimbookstore.apiserver.orders.order.scheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderSchedulerTest {

    @Mock
    private MemberOrderService orderService;

    @InjectMocks
    private OrderScheduler orderScheduler;

    @Test
    void testCompleteOldDeliveryOrders() {
        // given
        int expectedUpdatedCount = 5;
        when(orderService.updateOrdersToCompleted()).thenReturn(expectedUpdatedCount);

        // when
        int result = orderScheduler.completeOldDeliveryOrders();

        // then
        assertEquals(expectedUpdatedCount, result);
        verify(orderService, times(1)).updateOrdersToCompleted();
    }



        @Test
        void testRemoveExpiredPendingOrders() {
            // given
            int expectedRemovedCount = 5;
            when(orderService.removeExpiredPendingOrders(10))
                    .thenReturn(expectedRemovedCount);
            // 스케줄러 메서드를 직접 호출
            orderScheduler.removeExpiredPendingOrders();
            // then
            verify(orderService, times(1)).removeExpiredPendingOrders(10);
        }
    }
