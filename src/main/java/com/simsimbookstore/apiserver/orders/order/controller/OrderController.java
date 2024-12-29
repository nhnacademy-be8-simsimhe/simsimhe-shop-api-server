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
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
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

    @PostMapping("/api/order/total")
    public TotalResponseDto calculateTotal(@RequestBody TotalRequestDto requestDto) {
        log.info("요청 데이터: {}", requestDto);
        // 총합 계산 요청
        return orderTotalService.calculateTotal(requestDto);
    }


    @PostMapping("/api/order/prepare")
    public ResponseEntity<?> createPrepareOrder(
            @RequestBody OrderFacadeRequestDto facadeRequestDto
    ) {
        OrderFacadeResponseDto response = orderFacade.createPrepareOrder(facadeRequestDto);
        return ResponseEntity.ok(response);
    }
}



