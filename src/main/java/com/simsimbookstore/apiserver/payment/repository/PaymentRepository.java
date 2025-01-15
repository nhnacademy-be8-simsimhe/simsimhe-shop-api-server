package com.simsimbookstore.apiserver.payment.repository;

import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
}
