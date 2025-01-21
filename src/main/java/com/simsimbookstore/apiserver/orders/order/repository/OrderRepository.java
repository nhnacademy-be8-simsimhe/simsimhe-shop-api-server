package com.simsimbookstore.apiserver.orders.order.repository;

import com.simsimbookstore.apiserver.orders.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrderRepository extends JpaRepository<Order, Long>,  OrderCustomRepository{
    Optional<Order> findByOrderNumber(String orderNumber);
    Page<Order> findByUserUserIdOrderByOrderDateDesc(Long userId, Pageable pageable);
    List<Order> findAllByUserUserId(Long userId);


    @Query("SELECT o FROM Order o " +
            "WHERE o.orderState = 'PENDING' " +
            "  AND o.orderDate <= :thresholdTime")
    List<Order> findAllPendingOlderThan(@Param("thresholdTime") LocalDateTime thresholdTime);

}
