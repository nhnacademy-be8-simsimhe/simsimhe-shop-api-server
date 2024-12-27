package com.simsimbookstore.apiserver.payment.entity;

import jakarta.persistence.*;

@Entity
public class PaymentStatus {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentStatusId;

    @Column(nullable = false)
    private String paymentStatusName;
}
