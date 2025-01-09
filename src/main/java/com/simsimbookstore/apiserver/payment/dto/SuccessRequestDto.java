package com.simsimbookstore.apiserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessRequestDto {
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
    private PaymentMethodResponse paymentMethod;  // 사용자가 선택한 결제 방법 추가
}
