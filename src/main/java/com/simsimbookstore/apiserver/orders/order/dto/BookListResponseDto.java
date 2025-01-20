package com.simsimbookstore.apiserver.orders.order.dto;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookListResponseDto {
    Long bookId;
    String title;
    BigDecimal price;
    Integer quantity;
    List<OrderCouponResponseDto> coupons = new ArrayList<>();
}
