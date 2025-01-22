package com.simsimbookstore.apiserver.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_canceled")
public class PaymentCanceled {
    @Id
    @Column(nullable = false, name = "payment_canceled_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentCanceledId;

    @Lob
    @Column(nullable = false, name = "payment_canceled_reason")
    private String paymentCanceledReason;

    @Column(nullable = false, name = "payment_user_canceled_at")
    private LocalDateTime userCanceledAt;

    @Column(name = "payment_canceled_amount")
    private BigDecimal paymentCanceledAmount;

    @Column(name = "payment_canceled_transaction_key")
    private String paymentCanceledTransactionKey;

    @Column(name = "payment_toss_canceled_at")
    private LocalDateTime tossCanceledAt;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    public void confirmPaymentCancel(String paymentCanceledTransactionKey, LocalDateTime tossCanceledAt){
        this.paymentCanceledTransactionKey = paymentCanceledTransactionKey;
        this.tossCanceledAt = tossCanceledAt;
    }
}
