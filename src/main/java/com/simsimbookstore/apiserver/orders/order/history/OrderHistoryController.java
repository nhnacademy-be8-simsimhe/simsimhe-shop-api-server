package com.simsimbookstore.apiserver.orders.order.history;

import com.simsimbookstore.apiserver.orders.order.dto.OrderHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;

    @GetMapping(value = "/api/users/{userId}/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<OrderHistoryResponseDto>> getOrderBook(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok().body(orderHistoryService.getOrderHistory(userId, pageable));
    }
}
