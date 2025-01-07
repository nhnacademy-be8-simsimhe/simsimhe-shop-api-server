package com.simsimbookstore.apiserver.orders.order.history;

import com.simsimbookstore.apiserver.orders.order.dto.OrderHistoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderHistoryService {
    Page<OrderHistoryResponseDto> getOrderHistory(Long userId, Pageable pageable);
}
