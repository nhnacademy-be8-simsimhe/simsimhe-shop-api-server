package com.simsimbookstore.apiserver.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import com.simsimbookstore.apiserver.payment.entity.PaymentStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ConfirmSuccessResponseDto {
    private String orderId;
    private String paymentKey;
    private Double totalAmount;
    private String paymentMethod;
    private LocalDateTime approvedAt;

    public static Payment toEntity(ConfirmSuccessResponseDto confirmSuccessResponseDto,
                                   PaymentStatus paymentStatus, Order order) {

        return new Payment(
                null,
                confirmSuccessResponseDto.getPaymentKey(),
                confirmSuccessResponseDto.getApprovedAt(),
                paymentStatus,
                confirmSuccessResponseDto.getPaymentMethod(),
                order
        );

//        return Payment.builder()
//                .order()
//                .paymentKey(this.paymentKey)
//                .paymentMethodId(this.paymentMethod)
//                .paymentDate(this.approvedAt)
//                .paymentStatusId(paymentStatus)
//                .build();
    }
}
