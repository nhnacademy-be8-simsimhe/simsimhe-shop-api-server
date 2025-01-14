package com.simsimbookstore.apiserver.orders.orderbook.repository;

import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderBookRepository extends JpaRepository<OrderBook, Long> {
    List<OrderBook> findByOrderOrderId(Long orderId);
}
