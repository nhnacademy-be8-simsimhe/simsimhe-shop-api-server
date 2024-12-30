package com.simsimbookstore.apiserver.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPointCalculateRequestDto {
    Long userId;
    Long orderId;
}
