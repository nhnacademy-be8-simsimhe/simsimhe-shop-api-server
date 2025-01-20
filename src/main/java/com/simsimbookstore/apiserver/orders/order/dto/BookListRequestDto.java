package com.simsimbookstore.apiserver.orders.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor  // 기본 생성자 추가
public class BookListRequestDto {
    @JsonProperty("bookId")
    private Long bookId;
    @JsonProperty("quantity")
    private Integer quantity;
}
