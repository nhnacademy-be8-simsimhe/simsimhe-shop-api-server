package com.simsimbookstore.apiserver.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelDto {
    private String orderId;
    private String cancelReason;
}
