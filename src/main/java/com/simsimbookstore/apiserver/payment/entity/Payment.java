package com.simsimbookstore.apiserver.payment.entity;

import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.payment.dto.ConfirmResponseDto;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table (name = "payments")
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

    // 토스에게서 반환되는 결제 방법, 결제에 실패했으면 반환되지 않기 때문에 null 가능
    private String tossReturnMethod;

    private String errorCode;

    private String errorMessage;

    // 사용자가 선택하는 결제 수단
    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "payment_status_id", nullable = false)
    private PaymentStatus paymentStatus;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}