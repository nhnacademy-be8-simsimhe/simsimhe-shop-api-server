package com.simsimbookstore.apiserver.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.payment.entity.Payment;
import com.simsimbookstore.apiserver.payment.entity.PaymentMethod;
import com.simsimbookstore.apiserver.payment.entity.PaymentStatus;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConfirmSuccessResponseDto {
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("paymentKey")
    private String paymentKey;
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
    @JsonProperty("paymentMethod")
    private String method; // paymentMethod
    @JsonProperty("approvedAt")
    private String approvedAt;

//    public static void toEntity(ConfirmSuccessResponseDto confirmSuccessResponseDto,  //원래는 Payment Type
//                                   PaymentStatus paymentStatus, Order order) {

//        return new Payment(
//                null,
//                confirmSuccessResponseDto.getPaymentKey(),
//                confirmSuccessResponseDto.getApprovedAt(),
//                paymentStatus,
//                confirmSuccessResponseDto.getPaymentMethod(),
//                order
//        );

//        return Payment.builder()
//                .order(order)
//                .paymentKey(confirmSuccessResponseDto.paymentKey)
//                .paymentMethodId(confirmSuccessResponseDto.getMethod())
//                .paymentDate(this.approvedAt)
//                .paymentStatusId(paymentStatus)
//                .build();

//    public Payment toEntity(PaymentStatus paymentStatus, Order order, PaymentMethod paymentMethod) {
//        return Payment.builder()
//                .paymentKey(this.paymentKey)
//                .paymentDate(this.approvedAt)
//                .paymentMethodToss(this.method)
//                .paymentStatus(paymentStatus)
//                .order(order)
//                .paymentMethod(paymentMethod)
//                .build();
//
//    }
}
