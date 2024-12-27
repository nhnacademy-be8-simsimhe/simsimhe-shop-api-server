package com.simsimbookstore.apiserver.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class RequestOrderValueDto {
    @JsonProperty("orderNumber")
    private String orderId;
    private BigDecimal totalAmount;
}
