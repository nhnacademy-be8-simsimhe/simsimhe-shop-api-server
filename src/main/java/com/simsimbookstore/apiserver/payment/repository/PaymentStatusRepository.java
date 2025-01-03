package com.simsimbookstore.apiserver.payment.repository;

import com.simsimbookstore.apiserver.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {
    Optional<PaymentStatus> findByPaymentStatusName(String paymentStatusName);
}
