package com.simsimbookstore.apiserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessRequestDto {
    private String paymentKey;
    private String orderId;
    private Double amount;
}
