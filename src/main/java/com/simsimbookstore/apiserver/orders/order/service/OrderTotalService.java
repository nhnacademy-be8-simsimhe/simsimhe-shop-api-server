package com.simsimbookstore.apiserver.orders.order.service;

import com.simsimbookstore.apiserver.orders.order.dto.TotalRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalResponseDto;
import java.math.BigDecimal;

public interface OrderTotalService {

    TotalResponseDto calculateTotal(TotalRequestDto requestDto);

    BigDecimal calculateDeliveryPrice(BigDecimal total);
}
