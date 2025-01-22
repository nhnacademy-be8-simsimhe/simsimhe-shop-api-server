package com.simsimbookstore.apiserver.orders.returns.repository;

import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.returns.entity.Returns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnsRepository extends JpaRepository<Returns, Long> {
    Optional<Returns> findByOrderBook(OrderBook orderBook);

    boolean existsByOrderBook(OrderBook orderBook);
}
