package com.simsimbookstore.apiserver.orders.order.controller;


import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.orders.order.dto.OrderBookStateUpdateRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderOrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.OrderResponseDto;
import com.simsimbookstore.apiserver.orders.order.service.MemberOrderService;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookResponseDto;
import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.service.OrderBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {

    private final MemberOrderService memberOrderService;
    private final OrderBookService orderBookService;

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponseDto>> getOrderBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page-1, size);
        PageResponse<OrderResponseDto> allOrders = memberOrderService.getAllOrders(pageable);

        return ResponseEntity.ok(allOrders);
    }



    @GetMapping("/{id}")
    public ResponseEntity<OrderOrderBookResponseDto> getOrderOrderBook(@PathVariable Long id) {

        OrderOrderBookResponseDto orderOrderBook = memberOrderService.getOrderOrderBook(id);

        return ResponseEntity.ok(orderOrderBook);
    }

    @PostMapping("/{orderBookId}/state")
    public ResponseEntity<OrderBookResponseDto> updateOrderBookState(
            @PathVariable("orderBookId") Long orderBookId,
            @RequestBody OrderBookStateUpdateRequestDto requestDto) {

        OrderBook.OrderBookState newOrderBookState = requestDto.getNewOrderBookState();

        OrderBookResponseDto updatedOrderBook = orderBookService.updateOrderBook(orderBookId, newOrderBookState);

        return ResponseEntity.ok(updatedOrderBook);
    }
}
