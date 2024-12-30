package com.simsimbookstore.apiserver.payment.entity;

import jakarta.persistence.*;

@Entity
public class PaymentMethod {
    @Id
    @Column(name = "payment_method_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentMethodId;

    @Column(nullable = false)
    private String paymentMethod;
}
