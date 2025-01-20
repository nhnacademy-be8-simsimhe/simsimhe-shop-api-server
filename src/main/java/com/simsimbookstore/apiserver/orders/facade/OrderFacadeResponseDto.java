package com.simsimbookstore.apiserver.orders.facade;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
