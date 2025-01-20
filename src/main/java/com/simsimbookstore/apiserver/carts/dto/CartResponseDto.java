package com.simsimbookstore.apiserver.carts.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CartResponseDto {
    private Long bookId;
    private String userId;
    private int quantity;
    private String title;
    private String imagePath;
    private BigDecimal price;
    private int bookQuantity;

}
