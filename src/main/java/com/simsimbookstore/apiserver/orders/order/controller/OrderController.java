package com.simsimbookstore.apiserver.orders.order.controller;

import com.simsimbookstore.apiserver.orders.facade.OrderFacade;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalResponseDto;
import com.simsimbookstore.apiserver.orders.order.entity.Order;
import com.simsimbookstore.apiserver.orders.order.service.OrderListService;
import com.simsimbookstore.apiserver.orders.order.service.OrderTotalService;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/shop/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderListService orderListService;
    private final OrderTotalService orderTotalService;
    private final OrderFacade orderFacade;


    @PostMapping
    public ResponseEntity<List<BookListResponseDto>> getGuestOrderPage(
            @RequestBody List<BookListRequestDto> bookListRequestDto) {

        List<BookListResponseDto> response = orderListService.toBookOrderList(bookListRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<List<BookListResponseDto>> getMemberOrderPage(
            @PathVariable Long userId,
            @RequestBody List<BookListRequestDto> bookListRequestDto) {
        List<BookListResponseDto> response = orderListService.createBookOrderWithCoupons(orderListService.toBookOrderList(bookListRequestDto), userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/total")
    public ResponseEntity<TotalResponseDto> calculateTotal(@RequestBody TotalRequestDto requestDto) {
        log.info("요청 데이터 userId: {}", requestDto.getUserId());
        TotalResponseDto response = orderTotalService.calculateTotal(requestDto);
        log.info("response : {}", response);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/prepare")
    public ResponseEntity<OrderFacadeResponseDto> createPrepareOrder(
            @RequestBody OrderFacadeRequestDto facadeRequestDto
    ) {
        log.info("log del = {}", facadeRequestDto.getDeliveryRequestDto().toString());
        log.info("log orderBook = {}", facadeRequestDto.getOrderBookRequestDtos().toString());
        log.info("log MemberOrder = {}", facadeRequestDto.getMemberOrderRequestDto().toString());

        // 각 OrderBookRequestDto의 bookId와 관련 필드 확인
        for (OrderBookRequestDto obReq : facadeRequestDto.getOrderBookRequestDtos()) {
            log.info("OrderBookRequestDto: {}", obReq);
            if (obReq.getBookId() == null) {
                log.error("BookId is null for OrderBookRequestDto: {}", obReq);
            }
        }
        OrderFacadeResponseDto response = orderFacade.createPrepareOrder(facadeRequestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 포인트 환불 버튼
     * @param orderId
     * @return 포인트 환불된거 주문 리턴
     */

    @PostMapping("/point-refund")
    public ResponseEntity<Order> pointRefund(Long orderId) {
        return ResponseEntity.ok(orderFacade.orderRefund(orderId));
    }
}



