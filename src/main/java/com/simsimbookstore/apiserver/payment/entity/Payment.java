package com.simsimbookstore.apiserver.payment.entity;

import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.payment.dto.ConfirmSuccessResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethodToss;

    @ManyToOne
    @JoinColumn(name = "paymentMethodId", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "paymentStatusId", nullable = false)
    private PaymentStatus paymentStatus;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;
}
