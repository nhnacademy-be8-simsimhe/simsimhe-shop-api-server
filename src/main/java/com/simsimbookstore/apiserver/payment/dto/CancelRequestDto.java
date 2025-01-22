package com.simsimbookstore.apiserver.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CancelRequestDto {
    private String cancelReason;
    private Long orderBookId;
    private String orderNumber;
    private Long userId;
    private LocalDateTime refundedAt;

    public CancelRequestDto() {
        refundedAt = LocalDateTime.now();
    }
}
