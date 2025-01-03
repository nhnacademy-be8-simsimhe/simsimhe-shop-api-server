package com.simsimbookstore.apiserver.carts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartResponseDto {
    private Long bookId;
    private String userId;
    private int quantity;
    private String title;
    private String imagePath;
    private BigDecimal price;
    private int bookQuantity;

}
