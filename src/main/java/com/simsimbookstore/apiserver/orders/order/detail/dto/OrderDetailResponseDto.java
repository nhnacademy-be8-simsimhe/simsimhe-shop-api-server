package com.simsimbookstore.apiserver.orders.order.detail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderDetailResponseDto {
    OrderDetailInfoDto orderDetailInfoDto;
    List<OrderDetailProduct> orderDetailProductList;
}
