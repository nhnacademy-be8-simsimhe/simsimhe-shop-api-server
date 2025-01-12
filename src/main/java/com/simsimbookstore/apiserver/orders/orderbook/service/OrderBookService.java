package com.simsimbookstore.apiserver.orders.orderbook.service;

import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageResponseDto;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface OrderBookService {

    @Transactional
    OrderBookResponseDto createOrderBook(OrderBookRequestDto orderBookRequestDto);

    List<OrderBookResponseDto> createOrderBooks(List<OrderBookRequestDto> orderBookRequestDtos);

    OrderBookResponseDto getOrderBook(Long orderBookId);

    OrderBookResponseDto updateOrderBook(Long orderBookId, OrderBook.OrderBookState newOrderBookState);

    void deleteOrderBook(Long orderBookId);


    List<PackageResponseDto> getPackages(Long orderBookId);

    CouponDiscountResponseDto getCouponDiscount(Long orderBookId);

    String getOrderName(List<OrderBookRequestDto> dtos);

    OrderBookResponseDto toOrderBookResponseDto(OrderBook orderBook);
}
