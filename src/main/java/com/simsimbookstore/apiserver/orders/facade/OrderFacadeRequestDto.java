package com.simsimbookstore.apiserver.orders.facade;

import com.simsimbookstore.apiserver.orders.delivery.dto.DeliveryRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.MemberOrderRequestDto;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderFacadeRequestDto {
    DeliveryRequestDto deliveryRequestDto;
    MemberOrderRequestDto memberOrderRequestDto;
    List<OrderBookRequestDto> orderBookRequestDtos;
    String method;
}
