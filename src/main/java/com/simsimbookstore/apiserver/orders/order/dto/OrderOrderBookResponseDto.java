package com.simsimbookstore.apiserver.orders.order.dto;

import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookAdminResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOrderBookResponseDto {

    OrderAdminResponseDto orderResponseDto;
    List<OrderBookAdminResponseDto> orderBookAdminResponseDtos;
}
