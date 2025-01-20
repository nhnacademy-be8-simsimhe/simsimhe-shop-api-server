package com.simsimbookstore.apiserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cancel {
    private String orderNumber;
    private String paymentKey;
    private String canceledReason;
}
