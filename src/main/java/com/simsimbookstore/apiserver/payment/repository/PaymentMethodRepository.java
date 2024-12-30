package com.simsimbookstore.apiserver.payment.repository;

import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<Void> findByPaymentMethod(String method);
}
