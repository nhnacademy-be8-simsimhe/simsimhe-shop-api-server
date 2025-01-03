package com.simsimbookstore.apiserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SuccessRequestDto {
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
}
