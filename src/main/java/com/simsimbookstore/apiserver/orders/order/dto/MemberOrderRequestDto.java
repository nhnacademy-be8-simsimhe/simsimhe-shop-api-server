package com.simsimbookstore.apiserver.orders.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    private String phoneNumber;
    private BigDecimal deliveryPrice;
    private String senderName;
}
