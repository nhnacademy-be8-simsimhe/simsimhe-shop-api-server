package com.simsimbookstore.apiserver.orders.order.repository;

import com.simsimbookstore.apiserver.orders.order.entity.Order;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
}
