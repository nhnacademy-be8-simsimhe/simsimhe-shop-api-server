package com.simsimbookstore.apiserver.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ConfirmResponseDto {
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("paymentKey")
    private String paymentKey;
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
    @JsonProperty("method")
    private String tossReturnMethod;
    @JsonProperty("approvedAt")
    private String approvedAt;
    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;
}
