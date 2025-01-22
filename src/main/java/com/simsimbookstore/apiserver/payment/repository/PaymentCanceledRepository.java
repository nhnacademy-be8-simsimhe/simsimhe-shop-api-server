package com.simsimbookstore.apiserver.payment.repository;

import com.simsimbookstore.apiserver.payment.entity.Payment;
import com.simsimbookstore.apiserver.payment.entity.PaymentCanceled;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentCanceledRepository extends JpaRepository<PaymentCanceled, Long> {
    Optional<PaymentCanceled> findByPayment(Payment payment);
}
