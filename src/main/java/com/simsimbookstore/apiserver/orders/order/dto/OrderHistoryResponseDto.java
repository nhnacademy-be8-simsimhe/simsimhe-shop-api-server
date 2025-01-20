package com.simsimbookstore.apiserver.orders.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryResponseDto {
    private String orderNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;
    private String orderName;
    private BigDecimal orderAmount;
    private Order.OrderState orderState;
    private String trackingNumber;
    private String orderUserName;
    private String receiverName;

}
