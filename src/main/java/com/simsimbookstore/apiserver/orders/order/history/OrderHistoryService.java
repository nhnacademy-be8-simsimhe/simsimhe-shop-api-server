package com.simsimbookstore.apiserver.orders.order.history;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.orders.order.dto.OrderHistoryResponseDto;
import org.springframework.data.domain.Pageable;

public interface OrderHistoryService {
    PageResponse<OrderHistoryResponseDto> getOrderHistory(Long userId, Pageable pageable);
}
