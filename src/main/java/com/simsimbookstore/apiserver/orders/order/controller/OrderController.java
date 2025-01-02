package com.simsimbookstore.apiserver.orders.order.controller;

import com.simsimbookstore.apiserver.orders.facade.OrderFacade;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeRequestDto;
import com.simsimbookstore.apiserver.orders.facade.OrderFacadeResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.BookListResponseDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalRequestDto;
import com.simsimbookstore.apiserver.orders.order.dto.TotalResponseDto;
import com.simsimbookstore.apiserver.orders.order.service.OrderListService;
import com.simsimbookstore.apiserver.orders.order.service.OrderTotalService;
import com.simsimbookstore.apiserver.orders.orderbook.dto.OrderBookRequestDto;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
import com.simsimbookstore.apiserver.users.address.service.AddressService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController()
@RequiredArgsConstructor
public class OrderController {

    private final OrderListService orderListService;
    private final WrapTypeService wrapTypeService;
    private final OrderTotalService orderTotalService;
    private final OrderFacade orderFacade;
    private final AddressService addressService;


    @PostMapping("/api/order")
    public ResponseEntity<List<BookListResponseDto>> getOrderPage(
            @RequestBody List<BookListRequestDto> bookListRequestDto) {

        List<BookListResponseDto> response = orderListService.toBookOrderList(bookListRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/order/total")
    public ResponseEntity<TotalResponseDto> calculateTotal(@RequestBody TotalRequestDto requestDto) {
        requestDto.setUserId(1L);
        log.info("요청 데이터 userId: {}", requestDto.getUserId());
        // 총합 계산 요청
        TotalResponseDto response = orderTotalService.calculateTotal(requestDto);
        log.info("response : {}", response);
        return ResponseEntity.ok(response); // 명시적으로 JSON 응답 설정
    }


    @PostMapping("/api/order/prepare")
    public ResponseEntity<?> createPrepareOrder(
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
}



