package com.simsimbookstore.apiserver.orders.orderbook.repository;

import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderBookRepository extends JpaRepository<OrderBook, Long> {
}
