package com.simsimbookstore.apiserver.payment.repository;

import com.simsimbookstore.apiserver.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {
}
