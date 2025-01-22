package com.simsimbookstore.apiserver.carts.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CartRequestDto {

    private int quantity;

    private String bookId;

    private String userId;
}
