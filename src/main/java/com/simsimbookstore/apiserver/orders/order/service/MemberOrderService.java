package com.simsimbookstore.apiserver.orders.order.service;

import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import org.springframework.transaction.annotation.Transactional;

public interface MemberOrderService {
    OrderResponseDto createOrder(MemberOrderRequestDto requestDto);

    @Transactional
    int updateOrdersToCompleted();

    int removeExpiredPendingOrders(int minutes);
}
