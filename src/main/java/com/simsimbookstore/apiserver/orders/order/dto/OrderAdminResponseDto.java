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
public class OrderAdminResponseDto {
        private Long orderId;
        private String orderNumber;
        private BigDecimal totalPrice;
        private Order.OrderState orderState;

        public static OrderAdminResponseDto fromEntity(Order order) {
            return  OrderAdminResponseDto.builder()
                    .orderId(order.getOrderId())
                    .orderNumber(order.getOrderNumber())
                    .totalPrice(order.getTotalPrice())
                    .orderState(order.getOrderState())
                    .build();
        }
    }

