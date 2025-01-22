package com.simsimbookstore.apiserver.orders.packages.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WrapTypeResponseDto {

    private Long packageTypeId;

    private String packageName;

    private BigDecimal packagePrice;

    private Boolean isAvailable;
}

