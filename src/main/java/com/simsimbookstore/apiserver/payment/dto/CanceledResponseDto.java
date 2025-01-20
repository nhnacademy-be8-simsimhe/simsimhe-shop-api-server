package com.simsimbookstore.apiserver.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CanceledResponseDto {
    @JsonProperty("canceledAt")
    private String canceledAt;
    @JsonProperty("cancelReason")
    private String paymentCanceledReason;
    @JsonProperty("cancelAmount")
    private BigDecimal paymentCanceledAmount;
    @JsonProperty("transactionKey")
    private String paymentCanceledTransactionKey;  // 각 취소 거래마다 거래를 구분하는 키

    @JsonProperty("cancels")
    private void cancels(List<CanceledResponseDto> cancelData) {
        for (CanceledResponseDto canceledResponseDto : cancelData) {
            this.canceledAt = canceledResponseDto.canceledAt;
            this.paymentCanceledReason = canceledResponseDto.paymentCanceledReason;
            this.paymentCanceledAmount = canceledResponseDto.paymentCanceledAmount;
            this.paymentCanceledTransactionKey = canceledResponseDto.paymentCanceledTransactionKey;
        }
    }
}
