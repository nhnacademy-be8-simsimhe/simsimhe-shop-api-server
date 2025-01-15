package com.simsimbookstore.apiserver.orders.orderbook.repository;

import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderBookRepository extends JpaRepository<OrderBook, Long> {
    List<OrderBook> findAllByOrder(Order order);
}
