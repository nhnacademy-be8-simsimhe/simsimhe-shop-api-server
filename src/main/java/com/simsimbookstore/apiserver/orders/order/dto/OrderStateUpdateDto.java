package com.simsimbookstore.apiserver.orders.order.dto;

import com.simsimbookstore.apiserver.orders.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStateUpdateDto {
    Long orderId;
    Order.OrderState orderState;
}
