package com.simsimbookstore.apiserver.orders.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTrackingNumberRequestDto {
    private Integer trackingNumber;
}