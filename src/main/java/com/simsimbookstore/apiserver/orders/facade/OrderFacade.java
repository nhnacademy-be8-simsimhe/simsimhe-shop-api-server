package com.simsimbookstore.apiserver.orders.facade;

import com.simsimbookstore.apiserver.orders.order.entity.Order;
import org.springframework.transaction.annotation.Transactional;

public interface OrderFacade {

    @Transactional
    OrderFacadeResponseDto createPrepareOrder(OrderFacadeRequestDto facadeRequestDto);

    @Transactional
    Order orderRefund(Long orderId);
}
