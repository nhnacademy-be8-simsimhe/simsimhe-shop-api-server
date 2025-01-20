package com.simsimbookstore.apiserver.payment.repository;

import com.simsimbookstore.apiserver.payment.entity.PaymentCanceled;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCanceledRepository extends JpaRepository<PaymentCanceled, Long> {
}
