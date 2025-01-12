package com.simsimbookstore.apiserver.orders.order.service;

import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import java.util.List;

public interface OrderListService {
    List<BookListResponseDto> toBookOrderList(List<BookListRequestDto> dtos);
}
