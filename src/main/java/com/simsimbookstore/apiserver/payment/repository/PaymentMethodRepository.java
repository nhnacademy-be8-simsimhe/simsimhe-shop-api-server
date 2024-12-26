package com.simsimbookstore.apiserver.payment.repository;

import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
}
