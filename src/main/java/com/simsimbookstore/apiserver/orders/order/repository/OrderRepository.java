package com.simsimbookstore.apiserver.orders.order.repository;

import com.simsimbookstore.apiserver.orders.order.entity.Order;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    Page<Order> findByUserUserIdOrderByOrderDateDesc(Long userId, Pageable pageable);
    List<Order> findAllByUserUserId(Long userId);
}
