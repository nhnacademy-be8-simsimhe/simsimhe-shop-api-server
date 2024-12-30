package com.simsimbookstore.apiserver.point.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPointRequestDto {
    Long userId;
    Long orderId;
    Integer points;
}
