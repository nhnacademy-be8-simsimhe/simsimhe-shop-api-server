package com.simsimbookstore.apiserver.orders.order.service;

import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalRequestDto;
import java.math.BigDecimal;
import java.util.List;

public interface OrderTotalService {

    BigDecimal calculateTotal(TotalRequestDto requestDto);
}
