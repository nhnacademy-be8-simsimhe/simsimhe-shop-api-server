package com.simsimbookstore.apiserver.orders.order.history;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.orders.order.dto.OrderHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;

    @GetMapping(value = "/api/shop/users/{userId}/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<OrderHistoryResponseDto>> getOrderBook(@PathVariable Long userId,
                                                                              @RequestParam(defaultValue = "1")
                                                                              int page,
                                                                              @RequestParam(defaultValue = "15")
                                                                              int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseEntity.ok().body(orderHistoryService.getOrderHistory(userId, pageable));
    }
}
