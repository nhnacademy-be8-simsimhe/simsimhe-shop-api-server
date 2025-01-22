package com.simsimbookstore.apiserver.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class CancelRequestDto {
    private String cancelReason;
    private List<Long> orderBookId;
    private String orderNumber;
    private Long userId;
    private LocalDateTime refundedAt;

    public CancelRequestDto() {
        refundedAt = LocalDateTime.now();
    }
}
