package com.simsimbookstore.apiserver.orders.order.dto;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookListResponseDto {
    Long bookId;
    String title;
    BigDecimal price;
    Integer quantity;
    List<OrderCouponResponseDto> coupons = new ArrayList<>();
}
