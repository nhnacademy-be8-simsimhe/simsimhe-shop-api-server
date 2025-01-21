package com.simsimbookstore.apiserver.orders.order.dto;

import com.simsimbookstore.apiserver.orders.order.entity.Order;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private String orderNumber;
    private BigDecimal totalPrice;


    public static OrderResponseDto fromEntity(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .totalPrice(order.getTotalPrice())
                .build();
    }
}
