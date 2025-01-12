package com.simsimbookstore.apiserver.orders.orderbook.dto;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.orders.coupondiscount.dto.CouponDiscountRequestDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageRequestDto;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderBookRequestDto {
    @Setter
    private Long orderId;
    private Long bookId;
    private Long couponId;
    private Integer quantity;
    private BigDecimal salePrice;
    private BigDecimal discountPrice;
    private String orderBookState;

    // (선택) 쿠폰 할인 정보 1개
    private CouponDiscountRequestDto couponDiscountRequestDto;

    // (선택) 패키지 목록
    private List<PackageRequestDto> packagesRequestDtos;


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