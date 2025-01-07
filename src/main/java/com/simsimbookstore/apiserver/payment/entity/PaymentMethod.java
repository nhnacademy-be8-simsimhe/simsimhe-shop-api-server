package com.simsimbookstore.apiserver.payment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod {
    @Id
    @Column(name = "payment_method_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentMethodId;

    @Column(nullable = false)
    private String paymentMethod;
}
