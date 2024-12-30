package com.simsimbookstore.apiserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class FailResponseDto {
    private String code;
    private String message;
    private String orderId;

    // 결제 취소의 경우, 사용자가 중간에 끈 경우라 orderId가 존재하지 않음
    public FailResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
