package com.simsimbookstore.apiserver.orders.facade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFacadeResponseDto {
    String orderNumber;
    String orderName;
    String email;
    String phoneNumber;
    String userName;
    BigDecimal totalPrice;
    String method;
}
