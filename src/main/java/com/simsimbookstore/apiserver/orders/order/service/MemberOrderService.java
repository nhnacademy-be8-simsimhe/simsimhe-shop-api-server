package com.simsimbookstore.apiserver.orders.order.service;

import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;

public interface MemberOrderService {
    OrderResponseDto createOrder(MemberOrderRequestDto requestDto);
}
