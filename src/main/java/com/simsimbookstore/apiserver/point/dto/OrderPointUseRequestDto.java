package com.simsimbookstore.apiserver.point.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPointUseRequestDto {
    Long userId;
    Long orderId;
    BigDecimal usePoints;
}
