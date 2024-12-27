package com.simsimbookstore.apiserver.orders.facade;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderFacadeResponseDto {
    String orderNumber;
    BigDecimal totalPrice;
}
