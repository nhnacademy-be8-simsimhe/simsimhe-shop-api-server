package com.simsimbookstore.apiserver.orders.orderbook.dto;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderBookRequestDto {

    private Long orderId;
    private Long bookId;
    private Integer quantity;
    private BigDecimal salePrice;
    private BigDecimal discountPrice;
    private String orderBookState;


    public OrderBook toEntity(Book book, Order order) {
        return OrderBook.builder()
                .book(book)
                .order(order)
                .quantity(quantity)
                .salePrice(salePrice)
                .discountPrice(discountPrice)
                .orderBookState(OrderBook.OrderBookState.valueOf(orderBookState))
                .build();
    }
}