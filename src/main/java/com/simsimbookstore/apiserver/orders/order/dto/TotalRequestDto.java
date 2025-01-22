package com.simsimbookstore.apiserver.orders.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalRequestDto {
    private Long userId;
    private List<BookListRequestDto> bookList; // 책 주문 리스트
    private Map<Long, PackagingRequestDto> packagingOptions; // 책 ID별 포장 정보
    private Map<Long, Long> couponOptions; // 책 ID별 쿠폰 ID
    private BigDecimal usePoint;

    // 내부 클래스 포장 정보
    @Getter
    @Setter
    public static class PackagingRequestDto {
        private Long packageTypeId; // 포장지 타입 ID
        private int quantity; // 포장지 개수
    }
}


