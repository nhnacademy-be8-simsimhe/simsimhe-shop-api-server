package com.simsimbookstore.apiserver.orders.facade;

import org.springframework.transaction.annotation.Transactional;

public interface OrderFacade {

    @Transactional
    OrderFacadeResponseDto createPrepareOrder(OrderFacadeRequestDto facadeRequestDto);
}
