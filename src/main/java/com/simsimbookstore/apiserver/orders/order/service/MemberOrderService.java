package com.simsimbookstore.apiserver.orders.order.service;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderAdminResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderOrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import org.springframework.transaction.annotation.Transactional;

import com.simsimbookstore.apiserver.orders.order.dto.OrderStateUpdateDto;
import org.springframework.data.domain.Pageable;


public interface MemberOrderService {
    OrderResponseDto createOrder(MemberOrderRequestDto requestDto);


    @Transactional
    int updateOrdersToCompleted();

    int removeExpiredPendingOrders(int minutes);

    OrderResponseDto getOrder(Long id);

    OrderOrderBookResponseDto getOrderOrderBook(Long orderId);

    PageResponse<OrderResponseDto> getAllOrders(Pageable pageable);

    OrderAdminResponseDto updateOrder(OrderStateUpdateDto dto);
}
