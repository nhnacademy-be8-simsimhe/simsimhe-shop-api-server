package com.simsimbookstore.apiserver.orders.order.detail.controller;

import com.simsimbookstore.apiserver.orders.order.detail.dto.OrderDetailResponseDto;
import com.simsimbookstore.apiserver.orders.order.detail.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @GetMapping("/api/shop/users/{userId}/orders/{orderNumber}")
    public ResponseEntity<OrderDetailResponseDto> orderDetailHistory(@PathVariable Long userId,
                                                @PathVariable String orderNumber) {
        OrderDetailResponseDto detailResponseDto = orderDetailService.getOrderDetail(userId, orderNumber);
        return ResponseEntity.ok(detailResponseDto);
    }

    @GetMapping("/api/shop/guest-orders")
    public ResponseEntity<OrderDetailResponseDto> guestOrderDetail(@RequestParam String orderNumber,
                                                                   @RequestParam String email) {
        return ResponseEntity.ok(orderDetailService.getGuestOrderDetail(orderNumber, email));
    }
}
