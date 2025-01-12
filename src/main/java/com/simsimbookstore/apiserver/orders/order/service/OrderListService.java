package com.simsimbookstore.apiserver.orders.order.service;

import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderCouponResponseDto;
import java.util.List;

public interface OrderListService {
    List<BookListResponseDto> toBookOrderList(List<BookListRequestDto> dtos);

    List<BookListResponseDto> createBookOrderWithCoupons(List<BookListResponseDto> bookOrderList, Long userId);
}
