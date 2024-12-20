package com.simsimbookstore.apiserver.orders.packages.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

