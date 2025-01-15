package com.simsimbookstore.apiserver.orders.delivery.dto;

import com.simsimbookstore.apiserver.orders.delivery.entity.Delivery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStateUpdateRequestDto {
    private Delivery.DeliveryState newState; // 새로운 상태
}
