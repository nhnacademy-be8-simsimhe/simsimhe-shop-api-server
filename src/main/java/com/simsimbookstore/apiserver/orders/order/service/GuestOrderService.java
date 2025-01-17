package com.simsimbookstore.apiserver.orders.order.service;

import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;

public interface GuestOrderService {
    Long prepareUser(OrderFacadeRequestDto dto);
}
