package com.simsimbookstore.apiserver.payment.dto;

import lombok.Getter;

@Getter
public class RequestOrderValueDto {
    private String orderId;
    private Double totalAmount;
}
