package com.simsimbookstore.apiserver.orders.order.repository;

import com.simsimbookstore.apiserver.orders.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
