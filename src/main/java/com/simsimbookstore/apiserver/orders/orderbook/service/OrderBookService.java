package com.simsimbookstore.apiserver.orders.orderbook.service;

import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import java.util.List;

public interface OrderBookService {
    List<OrderBook> createOrderBooks(List<OrderBookRequestDto> orderBookRequestDtos);

    OrderBook getOrderBook(Long orderBookId);

    OrderBook updateOrderBook(Long orderBookId, OrderBook.OrderBookState newOrderBookState);

    void deleteOrderBook(Long orderBookId);

    List<Packages> getPackagesByOrderBookId(Long orderBookId);
}
