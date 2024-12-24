package com.simsimbookstore.apiserver.orders.orderbook.service;

import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageResponseDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import java.util.List;

public interface OrderBookService {

    List<OrderBookResponseDto> createOrderBooks(List<OrderBookRequestDto> orderBookRequestDtos);

    OrderBookResponseDto getOrderBook(Long orderBookId);

    OrderBookResponseDto updateOrderBook(Long orderBookId, OrderBook.OrderBookState newOrderBookState);

    void deleteOrderBook(Long orderBookId);


    List<PackageResponseDto> getPackages(Long orderBookId);

    CouponDiscountResponseDto getCouponDiscount(Long orderBookId);
}
