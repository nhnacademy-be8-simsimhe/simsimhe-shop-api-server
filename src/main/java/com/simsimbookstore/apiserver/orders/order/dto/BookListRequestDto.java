package com.simsimbookstore.apiserver.orders.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookListRequestDto {

    Long bookId;
    Integer quantity;

}
