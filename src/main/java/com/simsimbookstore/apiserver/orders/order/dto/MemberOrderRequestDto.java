package com.simsimbookstore.apiserver.orders.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberOrderRequestDto {
    @Setter
    private Long userId;
    @Setter
    private Long deliveryId;
    private BigDecimal originalPrice;
    private BigDecimal pointUse;
    @Setter
    private BigDecimal totalPrice;
    private LocalDate deliveryDate;
    private String orderEmail;
    private Integer pointEarn;
    private String phoneNumber;
    private BigDecimal deliveryPrice;
}
