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
public class ConfirmResponseDto {
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("paymentKey")
    private String paymentKey;
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
    @JsonProperty("method")
    private String tossReturnMethod;
    @JsonProperty("approvedAt")
    private String approvedAt;
    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;
}
