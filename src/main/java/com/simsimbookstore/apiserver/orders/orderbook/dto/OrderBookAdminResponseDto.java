package com.simsimbookstore.apiserver.orders.orderbook.dto;

import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderBookAdminResponseDto {
    private Long orderBookId;
    private String bookTitle;
    private Integer quantity;
    private BigDecimal salePrice;
    private BigDecimal discountPrice;
    private OrderBook.OrderBookState orderBookState;
}
