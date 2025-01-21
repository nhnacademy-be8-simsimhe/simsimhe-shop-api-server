package com.simsimbookstore.apiserver.orders.order.dto;

import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookStateUpdateRequestDto {
    private OrderBook.OrderBookState newOrderBookState;
}
